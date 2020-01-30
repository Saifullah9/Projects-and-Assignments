 /*
    This file is part of TinyRender, an educative rendering system.

    Designed for ECSE 446/546 Realistic/Advanced Image Synthesis.
    Derek Nowrouzezahrai, McGill University.
*/

#pragma once

TR_NAMESPACE_BEGIN

/**
 * Simple direct illumination integrator.
 */
struct SimpleIntegrator : Integrator {
    explicit SimpleIntegrator(const Scene& scene) : Integrator(scene) { }

    v3f render(const Ray& ray, Sampler& sampler) const override {
        v3f Li(0.f);

        // TODO(A2): Implement this
		SurfaceInteraction& info = SurfaceInteraction();
		v3f lightPos = scene.getFirstLightPosition();
		v3f lightIntens = scene.getFirstLightIntensity();
		v3f intensityPerSquareDist = lightIntens / (pow(glm::distance(lightPos, info.p), 2));


		
		if (scene.bvh->intersect(ray, info)) { // if intersecting
			SurfaceInteraction& shadowIntersection = SurfaceInteraction();
			v3f shdwPos = lightPos - info.p;
			float max = glm::distance(info.p, shdwPos);
			Ray shdwRay = Ray(info.p + Epsilon, shdwPos / max, Epsilon, max - Epsilon);


			if (scene.bvh->intersect(shdwRay, shadowIntersection)) {
				Li = v3f(0.f);

			}else {

				const BSDF* materialSurface = getBSDF(info);
				info.wi = lightPos - info.p;
				info.wi = info.frameNs.toLocal(info.wi);
				info.wi = glm::normalize(info.wi);
				Li = intensityPerSquareDist * materialSurface->eval(info);
			
			}
		
		
		
		}

        return Li;
    }
};

TR_NAMESPACE_END