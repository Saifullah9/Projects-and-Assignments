/*
	This file is part of TinyRender, an educative rendering system.

	Designed for ECSE 446/546 Realistic/Advanced Image Synthesis.
	Derek Nowrouzezahrai, McGill University.
*/

#pragma once

TR_NAMESPACE_BEGIN

/**
 * Direct illumination integrator with MIS
 */
	struct DirectIntegrator : Integrator {
	explicit DirectIntegrator(const Scene& scene) : Integrator(scene) {
		m_emitterSamples = scene.config.integratorSettings.di.emitterSamples;
		m_bsdfSamples = scene.config.integratorSettings.di.bsdfSamples;
		m_samplingStrategy = scene.config.integratorSettings.di.samplingStrategy;
	}

	static inline float balanceHeuristic(float nf, float fPdf, float ng, float gPdf) {
		float f = nf * fPdf, g = ng * gPdf;
		return f / (f + g);
	}

	void sampleSphereByCosineHemisphere(const p2f& sample,
		const v3f& n,
		const p3f& pShading,
		const v3f& emitterCenter,
		float emitterRadius,
		v3f& wiW,
		float& pdf) const {
		// TODO(A3): Implement this
		Frame frame(n);
		v3f w_i = Warp::squareToCosineHemisphere(sample);
		pdf = Warp::squareToCosineHemispherePdf(w_i);
		wiW = frame.toWorld(w_i);

	}

	void sampleSphereByArea(const p2f& sample,
		const p3f& pShading,
		const v3f& emitterCenter,
		float emitterRadius,
		v3f& pos,
		v3f& ne,
		v3f& wiW,
		float& pdf) const {
		// TODO(A3): Implement this
		v3f coord = emitterRadius * Warp::squareToUniformSphere(sample);
		ne = glm::normalize(coord);
		pos = coord + emitterCenter;
		wiW = glm::normalize(pos - pShading);
		pdf = 1 / (4 * M_PI * pow(emitterRadius,2));
	}

	void sampleSphereBySolidAngle(const p2f& sample,
		const p3f& pShading,
		const v3f& emitterCenter,
		float emitterRadius,
		v3f& wiW,
		float& pdf) const {
		// TODO(A3): Implement this

		v3f s = glm::normalize(emitterCenter - pShading);
		Frame sFrame = Frame(s);
		float sDist = glm::distance(emitterCenter, pShading);
		float sin_max = pow(emitterRadius / sDist, 2);
		float cos_max = std::sqrt(std::max(0.f, 1 - sin_max));
		float cosAng = (1 - sample.x) + (sample.x * cos_max);
		float sinAng = std::sqrt(std::max(0.f, 1 - pow(cosAng, 2)));
		float p = 2 * M_PI * sample.y;

		float d = (sDist * cosAng) - std::sqrt(std::max(0.f, pow(emitterRadius, 2) - pow(sDist * sinAng, 2)));
		float cosA = (pow(sDist, 2) + pow(emitterRadius, 2) - pow(d, 2)) / (2 * sDist * emitterRadius);
		float sinA = std::sqrt(std::max(0.f, 1 - pow(cosA, 2)));

		v3f spNormal = v3f(sinA * std::cos(p), sinA * std::sin(p), cosA);
		v3f sp = spNormal * emitterRadius;
		v3f spWorld = sFrame.toWorld(sp);

		wiW = glm::normalize(sFrame.toWorld(v3f(std::cos(p) * sinAng, std::sin(p) * sinAng, cosAng)));
		pdf = Warp::squareToUniformConePdf(cos_max);
	}

	v3f renderArea(const Ray& ray, Sampler& sampler) const {
		v3f Lr(0.f);
		// TODO(A3): Implement this
		SurfaceInteraction& data = SurfaceInteraction();
		if (scene.bvh->intersect(ray, data)) {
			if (getEmission(data) != v3f(0.f, 0.f, 0.f)) {
				size_t em_ID = getEmitterIDByShapeID(data.shapeID);
				const Emitter& em = getEmitterByID(em_ID);
				Lr = em.getRadiance();
			}
			else {
				for (int i = 0; i < m_emitterSamples; i++) {
					float em_PDF;
					size_t id = selectEmitter(sampler.next(), em_PDF);
					const Emitter& ems = getEmitterByID(id);
					float emR = scene.getShapeRadius(ems.shapeID);
					v3f emC = scene.getShapeCenter(ems.shapeID);

					float pdf = 0;
					v3f ne = v3f(0);
					v3f wiW = v3f(0);
					p2f x(sampler.next2D());
					v3f pos = v3f(0);

					sampleSphereByArea(x, data.p, emC, emR, pos, ne, wiW, pdf);
					data.wi = glm::normalize(data.frameNs.toLocal(wiW));
					Ray shRay = Ray(data.p + Epsilon, wiW, Epsilon);
					SurfaceInteraction& shIntersect = SurfaceInteraction();

					if (scene.bvh->intersect(shRay, shIntersect)) {
						float d = glm::distance(pos, data.p);
						float ang = glm::dot(ne, wiW);
						float v = glm::max(0.f, ang) / pow(d, 2);
						const BSDF* material = getBSDF(data);
						v3f BRDF = material->eval(data);

						Lr += BRDF * getEmission(shIntersect) * (v / (m_emitterSamples * pdf * em_PDF));


					}
				}
			}
		}

		return Lr / m_emitterSamples;
	}

	v3f renderCosineHemisphere(const Ray& ray, Sampler& sampler) const {
		v3f Lr(0.f);

		// TODO(A3): Implement this
		SurfaceInteraction& data = SurfaceInteraction();
		if (scene.bvh->intersect(ray, data)) {
			if (getEmission(data) != v3f(0.f, 0.f, 0.f)) {
				size_t em_ID = getEmitterIDByShapeID(data.shapeID);
				const Emitter& em = getEmitterByID(em_ID);
				Lr = em.getRadiance();
			}
			else {
				for (int i = 0; i < m_emitterSamples; i++) {

					p2f x(sampler.next2D());
					data.wi = Warp::squareToCosineHemisphere(x);
					Ray shRay = Ray(data.p + Epsilon, data.frameNs.toWorld(data.wi), Epsilon);
					SurfaceInteraction& shIntersect = SurfaceInteraction();

					if (scene.bvh->intersect(shRay, shIntersect)) {


						const BSDF* material = getBSDF(data);
						v3f BRDF = material->eval(data);

						Lr += BRDF * getEmission(shIntersect) / (m_emitterSamples * Warp::squareToCosineHemispherePdf(data.wi));


					}
				}
			}
		}

		return Lr;
	}

	v3f renderBSDF(const Ray& ray, Sampler& sampler) const {
		v3f Lr(0.f);
		int emissionSample = int(m_emitterSamples);
		SurfaceInteraction data;

		if (scene.bvh->intersect(ray, data)) {
			v3f em = getEmission(data);

			if (em != v3f(0.f)) { 
				return em;

			}
			else {
				for (int i = 0; i < m_bsdfSamples; i++) {
					float pdf;
					v3f sample = getBSDF(data)->sample(data, sampler, &pdf);
					v3f radiance = glm::normalize(data.frameNs.toWorld(data.wi));
					Ray shRay(data.p, radiance);

					SurfaceInteraction sh;
					if (scene.bvh->intersect(shRay, sh)) {
						v3f em = getEmission(sh);
						if (em != v3f(0.f)) {
							v3f brdf = getBSDF(data)->eval(data);
							Lr += em * sample;
						}
					}
				}
			}
		}
		// TODO: Implement this
		return Lr / m_bsdfSamples;
	}

	v3f renderSolidAngle(const Ray& ray, Sampler& sampler) const {
		v3f Lr(0.f);
		SurfaceInteraction& data = SurfaceInteraction();
		if (scene.bvh->intersect(ray, data)) {
			if (getEmission(data) != v3f(0.f, 0.f, 0.f)) {
				size_t em_ID = getEmitterIDByShapeID(data.shapeID);
				const Emitter& em = getEmitterByID(em_ID);
				Lr = em.getRadiance();
			}
			else {
				for (int i = 0; i < m_emitterSamples; i++) {
					float em_PDF;
					size_t id = selectEmitter(sampler.next(), em_PDF);
					const Emitter& ems = getEmitterByID(id);
					float emR = scene.getShapeRadius(ems.shapeID);
					v3f emC = scene.getShapeCenter(ems.shapeID);

					float pdf = 0;
					v3f wiW = v3f(0);
					p2f x(sampler.next2D());

					sampleSphereBySolidAngle(x, data.p, emC, emR, wiW, pdf);
					data.wi = glm::normalize(data.frameNs.toLocal(wiW));
					Ray shRay = Ray(data.p + Epsilon, wiW, Epsilon);
					SurfaceInteraction& shIntersect = SurfaceInteraction();

					if (scene.bvh->intersect(shRay, shIntersect)) {
						float ang = Frame::cosTheta(data.frameNs.toLocal(wiW));
						const BSDF* material = getBSDF(data);
						v3f BRDF = material->eval(data);

						Lr += BRDF * getEmission(shIntersect) / (m_emitterSamples * pdf * em_PDF);


					}
				}
			}
		}
		return Lr;

	}

	v3f renderMIS(const Ray& ray, Sampler& sampler) const {

		v3f Lr(0.f);

		// TODO(A4): Implement this
		SurfaceInteraction& data = SurfaceInteraction();
		bool isIntersect = scene.bvh->intersect(ray, data);
		if (isIntersect) {
			if (getEmission(data) != v3f(0.f, 0.f, 0.f)) {
				size_t em_id = getEmitterIDByShapeID(data.shapeID);
				const Emitter& em = getEmitterByID(em_id);
				Lr = em.getRadiance();
			}
			else {
				for (int i = 0; i < m_emitterSamples; i++) {
					float em_pdf;
					size_t id = selectEmitter(sampler.next(), em_pdf);
					const Emitter& emis = getEmitterByID(id);
					v3f em_c = scene.getShapeCenter(emis.shapeID);
					float em_r = scene.getShapeRadius(emis.shapeID);
					
					float pdf = 0.f;
					p2f x(sampler.next2D());
					v3f wi_w = v3f(0.f);

					sampleSphereBySolidAngle(x, data.p, em_c, em_r, wi_w, pdf);

					data.wi = glm::normalize(data.frameNs.toLocal(wi_w));
					SurfaceInteraction& shIntersect = SurfaceInteraction();
					Ray shRay = Ray(data.p + Epsilon, wi_w, Epsilon);
					if (scene.bvh->intersect(shRay, shIntersect)) {
						const BSDF* material = getBSDF(data);
						v3f BRDF = material->eval(data);

						float pdf_f = pdf * getEmitterPdf(emis);
						float pdf_g = material->pdf(data);
						float w = balanceHeuristic(m_emitterSamples, pdf_f, m_bsdfSamples, pdf_g);
						v3f emis = getEmission(shIntersect);
						Lr += BRDF * getEmission(shIntersect) * w / (m_emitterSamples * pdf * em_pdf);
					}
				}
				//sampling contribution from BSDF
				for (int i = 0; i < m_bsdfSamples; i++) {
					
					float pdf = 0;
					const BSDF* material = getBSDF(data);
					v3f value = material->sample(data, sampler, &pdf);
					float pdf_f = pdf;

					SurfaceInteraction& shIntersect = SurfaceInteraction();
					Ray shRay = Ray(data.p + Epsilon, glm::normalize(data.frameNs.toWorld(data.wi)), Epsilon);
				
					if (scene.bvh->intersect(shRay, shIntersect)) {
						if (getEmission(shIntersect) != v3f(0.f)) {
							size_t emitterID = getEmitterIDByShapeID(shIntersect.shapeID);
							const Emitter& emis = getEmitterByID(emitterID);

							v3f em_c = scene.getShapeCenter(emis.shapeID);
							float em_r = scene.getShapeRadius(emis.shapeID);
							float d = glm::distance(em_c, data.p);

							float cosAng = std::sqrt(std::max(0.f, 1 - pow(em_r / d, 2)));
							float pdf_g = Warp::squareToUniformConePdf(cosAng) * getEmitterPdf(emis);
							float w = balanceHeuristic(m_bsdfSamples, pdf_f, m_emitterSamples, pdf_g);

							Lr += value * getEmission(shIntersect) * w / ((float)m_bsdfSamples);
						}
					}
				}

			}

		}

		return Lr;
	}

	v3f render(const Ray& ray, Sampler& sampler) const override {
		if (m_samplingStrategy == ESamplingStrategy::EMIS)
			return this->renderMIS(ray, sampler);
		else if (m_samplingStrategy == ESamplingStrategy::EArea)
			return this->renderArea(ray, sampler);
		else if (m_samplingStrategy == ESamplingStrategy::ESolidAngle)
			return this->renderSolidAngle(ray, sampler);
		else if (m_samplingStrategy == ESamplingStrategy::ECosineHemisphere)
			return this->renderCosineHemisphere(ray, sampler);
		else
			return this->renderBSDF(ray, sampler);
	}

	size_t m_emitterSamples;     // Number of emitter samples
	size_t m_bsdfSamples;        // Number of BSDF samples
	ESamplingStrategy m_samplingStrategy;   // Sampling strategy to use
};

TR_NAMESPACE_END