/*
	This file is part of TinyRender, an educative rendering system.

	Designed for ECSE 446/546 Realistic/Advanced Image Synthesis.
	Derek Nowrouzezahrai, McGill University.
*/

#pragma once

TR_NAMESPACE_BEGIN

/*
 * Ambient occlusion integrator
 */
	struct AOIntegrator : Integrator {

	// Use this in your switch statement to select the sampling type 
	ESamplingType m_samplingStrategy;

	explicit AOIntegrator(const Scene& scene) : Integrator(scene) {
		m_samplingStrategy = scene.config.integratorSettings.ao.sampling_type;

	}

	v3f render(const Ray& ray, Sampler& sampler) const override {

		v3f w_i;
		SurfaceInteraction data;
		float Li = 0;


		if (scene.bvh->intersect(ray, data)) {
			
			p2f x(sampler.next2D());
			
			if (m_samplingStrategy == ESpherical) {
				w_i = Warp::squareToUniformSphere(x); 
			}
			if (m_samplingStrategy == EHemispherical) {
				w_i = Warp::squareToUniformHemisphere(x);
			}
			if (m_samplingStrategy == ECosineHemispherical) {
				w_i = Warp::squareToCosineHemisphere(x);
			}
		
			Ray shRay = Ray(data.p, glm::normalize(data.frameNs.toWorld(w_i)), Epsilon, scene.aabb.getBSphere().radius / 2);
			
			
			if (scene.bvh->intersect(shRay, data)) {
				Li = 0.f;
			}
			else {
				float pdf;
				data.wi = w_i;
				if (m_samplingStrategy == ECosineHemispherical) {
					pdf = Warp::squareToCosineHemispherePdf(data.wi);
				}
				if (m_samplingStrategy == EHemispherical) {
					pdf = Warp::squareToUniformHemispherePdf(data.wi); 
				}
				if (m_samplingStrategy == ESpherical) {
					pdf = Warp::squareToUniformSpherePdf(); 
				}

				float cosAng = glm::dot(glm::normalize(data.wi), glm::normalize(data.frameNs.toLocal(data.frameNs.n)));

				
				if (Frame::cosTheta(w_i) > 0) {
		
					Li += INV_PI / pdf * cosAng;
				}
			}

		}
		return v3f(Li);
	}
};

TR_NAMESPACE_END