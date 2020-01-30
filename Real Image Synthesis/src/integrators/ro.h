/*
    This file is part of TinyRender, an educative rendering system.

    Designed for ECSE 446/546 Realistic/Advanced Image Synthesis.
    Derek Nowrouzezahrai, McGill University.
*/

#pragma once
#include <random>
#include "bsdfs/phong.h"

TR_NAMESPACE_BEGIN

/**
 * Reflection occlusion integrator
 */
struct ROIntegrator : Integrator {

    float m_exponent;

    explicit ROIntegrator(const Scene& scene) : Integrator(scene) {
        m_exponent = scene.config.integratorSettings.ro.exponent;
    }

    inline v3f reflect(const v3f& d) const {
        return v3f(-d.x, -d.y, d.z);
    }


    v3f render(const Ray& ray, Sampler& sampler) const override {
        v3f Li(0.f);

        // TODO(A3): Implement this
		SurfaceInteraction& data = SurfaceInteraction();
		//TinyRender::BSphere lightS = scene.aabb.getBSphere();
		if (scene.bvh->intersect(ray, data)) {
			SurfaceInteraction& shIntersect = SurfaceInteraction();
			p2f x(sampler.next2D());
			data.wi = Warp::squareToPhongLobe(x, m_exponent);
			float pdf = Warp::squareToPhongLobePdf(data.wi, m_exponent);
			float cosA = data.wi.z;

			if (cosA < 0.f) {
				cosA = 0.f;
			}				
				v3f w_r = reflect(data.wo);
				data.wi = Frame(w_r).toWorld(data.wi);
				Ray shRay = Ray(data.p, data.frameNs.toWorld(glm::normalize(data.wi)));
				float li_1 = cosA * (m_exponent + 2) * INV_TWOPI * max(0.0f, pow(cosA, m_exponent));
				float v = 1.f;
				if (scene.bvh->intersect(shRay, shIntersect)) {
					v = 0.f;
				}
				if(pdf>0.f) {
					Li = (v3f(li_1 * v * cosA * 1.f/ (pdf)));
					
				}
		}

        return Li;
    }
};

TR_NAMESPACE_END