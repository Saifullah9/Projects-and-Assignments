/*
    This file is part of TinyRender, an educative rendering system.

    Designed for ECSE 446/546 Realistic/Advanced Image Synthesis.
    Derek Nowrouzezahrai, McGill University.
*/

#pragma once

TR_NAMESPACE_BEGIN

/**
 * Path tracer integrator
 */
struct PathTracerIntegrator : Integrator {
    explicit PathTracerIntegrator(const Scene& scene) : Integrator(scene) {
        m_isExplicit = scene.config.integratorSettings.pt.isExplicit;
        m_maxDepth = scene.config.integratorSettings.pt.maxDepth;
        m_rrDepth = scene.config.integratorSettings.pt.rrDepth;
        m_rrProb = scene.config.integratorSettings.pt.rrProb;
    }


    v3f renderImplicit(const Ray& ray, Sampler& sampler, SurfaceInteraction& hit) const {
        v3f Li(0.f);
        // TODO(A5): Implement this
		v3f tmp(1.f);
		int d = 0;
		while (d <= m_maxDepth) {
			float pdf;
			v3f emis = getEmission(hit);
			
			if (emis != v3f(0.f) && glm::dot(v3f(0, 0, 1), hit.wo) > 0) {
				tmp *= emis;
				Li = tmp;
				return Li;
			}
			v3f thro = getBSDF(hit)->sample(hit, sampler, &pdf);
			tmp *= thro;
			Ray RAY = Ray(hit.p, glm::normalize(hit.frameNs.toWorld(hit.wi)));
			if (!scene.bvh->intersect(RAY, hit)) {
				return v3f(0.f);
			}
			d++;

		}
		


        return Li;
    }

	/*
	Helper method that i created
	*/
	v3f DI(Sampler& sampler, SurfaceInteraction& hit) const{

		const BSDF* material = getBSDF(hit);
		v3f d(0.f);

		float em_pdf;
		v3f p;
		v3f n;
		float d_pdf;
		size_t ID = selectEmitter(sampler.next(), em_pdf);
		const Emitter& emit = getEmitterByID(ID);
		Integrator::sampleEmitterPosition(sampler, emit, n, p, d_pdf);

		v3f d_wi = glm::normalize(p - hit.p);
		hit.wi = glm::normalize(hit.frameNs.toLocal(d_wi)); // iiiii
		SurfaceInteraction& shIntersect = SurfaceInteraction();
		Ray shRay = Ray(hit.p, d_wi, Epsilon);
		if (scene.bvh->intersect(shRay, shIntersect)) {
			if (glm::dot(shIntersect.frameNs.n,d_wi) < 0.f) {
				float dist = glm::distance(p, hit.p);
				float cosAngle = glm::dot(n, -d_wi);
				float g = glm::max(0.f, cosAngle)/pow(dist,2);
				v3f brdf = material->eval(hit);

				d = brdf * g * getEmission(shIntersect) / (d_pdf * em_pdf);


			}
		}
		return d;

	}

	/*
	Helper method that i created
	*/
	v3f GI(Sampler& sampler, SurfaceInteraction& hit,int depth) const {
	
		v3f L_ind(0.f);
		v3f blck(0.f);
		v3f tmp(1.f);

		int the_D = depth + 1;
		float rr_pdf = 1.f;
		if (m_maxDepth == -1) {
			if (the_D >= m_rrDepth) { 
				if (sampler.next() <= m_rrProb) {
					rr_pdf = m_rrProb;	
				}
				else {
					return blck;
				}
			}
		
		}
		else {

			if (the_D >= m_maxDepth) {
				return blck;
			}
		}
		v3f emiss = v3f(1.f);
		SurfaceInteraction data;
		v3f thru(1.f);
		while (emiss != v3f(0.f)) {
			float pdf;
			thru = getBSDF(hit)->sample(hit, sampler, &pdf);
			Ray nextRay = Ray(hit.p, glm::normalize(hit.frameNs.toWorld(hit.wi)));
			if (scene.bvh->intersect(nextRay, data)) {
				emiss = getEmission(data);

			}
			else {
				return v3f(0.f);
			}

		}
		return 1 / rr_pdf * thru * (DI(sampler, data) + GI(sampler, data, the_D));


	}



    v3f renderExplicit(const Ray& ray, Sampler& sampler, SurfaceInteraction& hit) const {
        v3f Li(0.f);

		v3f Le(0.f);
		v3f Ldir;
		v3f Lind;

		v3f emission = getEmission(hit);
		if (emission != v3f(0.f)) { // lies on light source
			return emission;
		}
		if (m_maxDepth == 0) {
			Ldir = v3f(0);
		}
		else {
			Ldir = DI(sampler,hit);
		}

		int depth = 0;
		Lind = GI(sampler, hit, depth);

		Li = Le + Lind + Ldir;
		

        return Li;
    }


    v3f render(const Ray& ray, Sampler& sampler) const override {
        Ray r = ray;
        SurfaceInteraction hit;

        if (scene.bvh->intersect(r, hit)) {
            if (m_isExplicit)
                return this->renderExplicit(ray, sampler, hit);
            else
                return this->renderImplicit(ray, sampler, hit);
        }
        return v3f(0.0);
    }

    int m_maxDepth;     // Maximum number of bounces
    int m_rrDepth;      // When to start Russian roulette
    float m_rrProb;     // Russian roulette probability
    bool m_isExplicit;  // Implicit or explicit
};

TR_NAMESPACE_END
