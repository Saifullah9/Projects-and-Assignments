/*
	This file is part of TinyRender, an educative rendering system.

	Designed for ECSE 446/546 Realistic/Advanced Image Synthesis.
	Derek Nowrouzezahrai, McGill University.
*/

#pragma once

#include <tiny_obj_loader.h>
#define RAY_EPS_CV 1e-5 // Use when setting min and max dist for ray in control variates code
TR_NAMESPACE_BEGIN

/**
 * Direct illumination integrator for polygonal light sources
 * Follows Arvo '94.
 */
	struct PolygonalIntegrator : Integrator {

	float m_alpha;             // Control variates "strength"
	size_t m_visSamples;       // # of samples to estimate h - alpha*g
	bool m_traceShadows;       // Trace shadows or not
	EPolygonalMethod m_method; // Method to use (Arvo, or control variates)

	std::vector<std::vector<v3f>> m_triangles; // Data structure to store triangles

	explicit PolygonalIntegrator(const Scene& scene) : Integrator(scene) {
		m_alpha = scene.config.integratorSettings.poly.alpha;
		m_visSamples = scene.config.integratorSettings.poly.visSamples;
		m_traceShadows = scene.config.integratorSettings.poly.traceShadows;
		m_method = scene.config.integratorSettings.poly.method;

		/**
		 * 1) Get # of triangles on emitter
		 * 2) Store vertices in m_triangles
		 */
		 // TODO(A4): Implement this
		Emitter emit = scene.emitters[0];
		auto s = scene.worldData.shapes[emit.shapeID];

		for (int i = 0; i < s.mesh.indices.size() / 3; i++) {
			std::vector<v3f> t(3);

			t[0] = scene.getObjectVertexPosition(emit.shapeID, 3 * i);
			t[1] = scene.getObjectVertexPosition(emit.shapeID, (3 * i) + 1);
			t[2] = scene.getObjectVertexPosition(emit.shapeID, (3 * i) + 2);
			m_triangles.push_back(t);

		}

	}

	/// Reflect
	inline v3f reflect(const v3f& d) const {
		return v3f(-d.x, -d.y, d.z);
	}

	/**
	 * === PHONG BONUS ONLY ===
	 * Compute the following integral:
	 *    T(a, b, n, x) = \int_0^x [a \cos(\theta) + b \sin(\theta)]ˆn d\theta
	 * Uses a recurrent relation (see Snyder's note, 1996)
	 *
	 * Series function:
	 *    T_sum(a, b, n, x) = \sum_{i=0}ˆ{(n-1)/2} T(a, b, 2i+1, x)
	 * assuming n is _odd_
	 */
	float cosineSinePowerIntegralSum(float a, float b, int exp, float theta) const {
		if (exp % 2 == 0) exp += 1; // Make exponent odd
		float Tsum = 0.f;

		// Implementing this function may be useful if you attempt the bonus

		// TODO(A4): Implement this

		return Tsum;
	}

	/**
	 * Compute edge (v1--v2) contribution
	 * The exp term is only needed if you attempt the bonus, otherwise, you can ignore it
	 */
	float getEdgeContrib(const v3f& v1, const v3f& v2, const SurfaceInteraction& i, int exp = 0) const {
		float contrib = 0.f;

		// TODO(A4): Implement this
		v3f d1 = v1 - i.p;
		v3f d2 = v2 - i.p;
		v3f ang1 = d1 / glm::l2Norm(d1);
		v3f ang2 = d2 / glm::l2Norm(d2);

		v3f g = glm::cross(d2, d1) / glm::l2Norm(glm::cross(d2, d1));
		float d = glm::dot(g, i.frameNs.n);


		float d_ang = glm::dot(ang1, ang2);


		if (d_ang < -1.0) {
			d_ang = -1.0;
		}

		if (d_ang > 1.0) {
			d_ang = 1.0;
		}

		contrib = acosf(d_ang) * d;
		return contrib;
	}


	/// Direct illumination using Arvo '94 analytic solution for polygonal lights
	v3f renderAnalytic(const Ray& ray, Sampler& sampler) const {
		v3f Lr(0.f);

		// TODO(A4): Implement this
		SurfaceInteraction data;
		if (scene.bvh->intersect(ray, data)) {

			v3f emission = getEmission(data);

			if (glm::l2Norm(emission) > 0.0f) {
				Lr = emission;
			}
			else {

				Emitter emit = scene.emitters[0];
				v3f emis = emit.getPower() / emit.area;
				float c = 0.0f;

				for (int i = 0; i < m_triangles.size(); i++) {
					std::vector<v3f> t = m_triangles[i];

					c += getEdgeContrib(t[0], t[1], data);
					c += getEdgeContrib(t[1], t[2], data);
					c += getEdgeContrib(t[2], t[0], data);
				}

				v3f e_pol = (emission * c) / (2 * M_PI);
				data.wi = v3f(0, 0, 1);

				v3f p = getBSDF(data)->eval(data);
				Lr = p * e_pol;
			}
		}
		return Lr;
	}

	/**
	 * Stand-alone estimator for h - alpha*g (with primary ray)
	 * Trace a primary ray, check for emitter hit, and then call `estimateVisDiff()`
	 * Used by polygonal render pass
	 */
	v3f estimateVisDiffRealTime(const Ray& ray, Sampler& sampler, const Emitter& em) {
		v3f D(0.f);

		SurfaceInteraction hit;
		if (!scene.bvh->intersect(ray, hit)) return D;

		const BSDF* bsdf = getBSDF(hit);
		if (bsdf->isEmissive()) return D;

		hit.wi = v3f(0, 0, 1); // Trick to get 1/pi * albedo without cosine term
		D = estimateVisDiff(sampler, hit, em);

		return D;
	}

	/// Stand-alone estimator for h - alpha*g (without primary ray)
	/// Use RAY_EPS_CV when setting min and max dist for shadow ray
	v3f estimateVisDiff(Sampler& sampler, SurfaceInteraction& i, const Emitter& em) const {
		v3f sum(0.f);

		// TODO(A4): Implement this
		std::vector<v3f> values;
		float emit_pdf = 1.0f / scene.emitters.size();
		v3f emit_c = scene.getShapeCenter(em.shapeID);
		float emit_r = scene.getShapeRadius(em.shapeID);
		v3f n;
		v3f p;
		float pdf;

		sampleEmitterPosition(sampler, em, n, p, pdf);
		v3f w_i = glm::normalize(p - i.p);
		v3f emis, h, g = v3f(0.0f);
		values.push_back(h);
		values.push_back(g);

		for (int j = 0; j < values.size(); j++) {
			v3f c = i.p - p;

			if (glm::dot(n, c) >= 0.0f) {

				float l_dist = glm::distance(p, i.p);
				SurfaceInteraction sh;
				Ray shRay = Ray(i.p, glm::normalize(w_i), Epsilon, l_dist + Epsilon);
				bool trace_sh = (bool)m_traceShadows;
				if (!scene.bvh->intersect(shRay, sh) || j == 1) {
					emis = em.getRadiance();
					i.wi = i.frameNs.toLocal(w_i);
					v3f bsdf = getBSDF(i)->eval(i);

					float cosAng = glm::abs(glm::dot(-w_i, glm::normalize(n)));
					pdf *= (powf(l_dist, 2) * emit_pdf) / (cosAng);
					values[j] += v3f((emis * bsdf));
				}
			}
		}

		if (pdf != 0) {
			sum = (values[0] - m_alpha * values[1]) / pdf;
		}

		return sum;
	}

	/// Control variates using Arvo '94 for direct illumination; ray trace shadows

	v3f renderControlVariates(const Ray& ray, Sampler& sampler) const {
		v3f Lr(0.f);

		// TODO(A4): Implement this
		SurfaceInteraction data;
		if (scene.bvh->intersect(ray, data)) {

			v3f emis = getEmission(data);
			if (glm::l2Norm(emis) > 0.0f) {
				Lr = emis;
			}

			else {
				for (int i = 0; i < m_visSamples; i++) {
					float em_pdf;

					size_t ID = selectEmitter(sampler.next(), em_pdf);
					const Emitter& emit = getEmitterByID(ID);

					Lr += estimateVisDiff(sampler, data, emit);
				}


				if (m_visSamples > 0) {
					Lr /= m_visSamples;
				}

				v3f gAng = v3f(0.0f);
				Emitter emit = scene.emitters[0];
				emis = emit.getPower() / emit.area;

				float c = 0.0f;

				for (int i = 0; i < m_triangles.size(); i++) {
					std::vector<v3f> t = m_triangles[i];

					c += getEdgeContrib(t[0], t[1], data);
					c += getEdgeContrib(t[1], t[2], data);
					c += getEdgeContrib(t[0], t[2], data);
				}

				data.wi = v3f(0, 0, 1);
				v3f e_pol = (emis * c) / (2 * M_PI);
				v3f p = getBSDF(data)->eval(data);
				gAng = (p)* e_pol;
				Lr += m_alpha * gAng;
			}
		}

		Lr = v3f(std::max(0.f, (float)Lr.x), std::max(0.f, (float)Lr.y), std::max(0.f, (float)Lr.z));

		return Lr;
	}

	/// Direct illumination using surface area sampling
	v3f renderArea(const Ray& ray, Sampler& sampler) const {
		v3f Lr(0.f);

		// TODO(A4): Implement this
		SurfaceInteraction data;
		if (scene.bvh->intersect(ray, data)) {
			v3f emis = getEmission(data);

			if (glm::l2Norm(emis) > 0.0f) {
				Lr = emis;
			}
			else {
				float em_pdf;
				size_t ID = selectEmitter(sampler.next(), em_pdf);

				const Emitter& emit = getEmitterByID(ID);
				float emit_r = scene.getShapeRadius(emit.shapeID);
				v3f emit_c = scene.getShapeCenter(emit.shapeID);
				v3f n, p;
				float pdf;

				sampleEmitterPosition(sampler, emit, n, p, pdf);

				v3f c = data.p - p;

				v3f w_i = glm::normalize(p - data.p);
				if (glm::dot(n, c) >= 0.0f) {
					float l_dist = glm::distance(p, data.p);

					SurfaceInteraction sh;
					Ray shRay = Ray(data.p, glm::normalize(w_i), Epsilon, l_dist - Epsilon);
					if (!scene.bvh->intersect(shRay, sh) || !(bool)m_traceShadows) {
						emis = emit.getRadiance();
						data.wi = data.frameNs.toLocal(w_i);
						v3f bsdf = getBSDF(data)->eval(data);

						float cosAng = glm::abs(glm::dot(-w_i, glm::normalize(n)));
						pdf *= (powf(l_dist, 2) * em_pdf) / (cosAng);

						if (pdf > 0) {

							Lr += v3f((emis * bsdf) / (pdf));

						}

					}
				}


			}
		}
		return Lr;
	}

	/// Branch to corresponding method
	v3f render(const Ray& ray, Sampler& sampler) const override {
		switch (m_method) {
		case EPolygonalMethod::ESurfaceArea:
			return PolygonalIntegrator::renderArea(ray, sampler);
			break;
		case EPolygonalMethod::EControlVariates:
			return PolygonalIntegrator::renderControlVariates(ray, sampler);
			break;
		default:
			return PolygonalIntegrator::renderAnalytic(ray, sampler);
			break;
		}
	}

};

TR_NAMESPACE_END