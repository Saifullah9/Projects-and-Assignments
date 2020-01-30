/*
    This file is part of TinyRender, an educative rendering system.

    Designed for ECSE 446/546 Realistic/Advanced Image Synthesis.
    Derek Nowrouzezahrai, McGill University.
*/

#pragma once

#include "core/core.h"

TR_NAMESPACE_BEGIN

/**
 * Modified Phong reflectance model
 */
struct PhongBSDF : BSDF {

    std::unique_ptr<Texture < v3f>> specularReflectance;
    std::unique_ptr<Texture < v3f>> diffuseReflectance;
    std::unique_ptr<Texture < float>> exponent;
    float specularSamplingWeight;
    float scale;

    PhongBSDF(const WorldData& scene, const Config& config, const size_t& matID) : BSDF(scene, config, matID) {
        const tinyobj::material_t& mat = scene.materials[matID];

        if (mat.specular_texname.empty())
            specularReflectance = std::unique_ptr<Texture<v3f>>(new ConstantTexture3f(glm::make_vec3(mat.specular)));
        else
            specularReflectance = std::unique_ptr<Texture<v3f>>(new BitmapTexture3f(config, mat.specular_texname));

        if (mat.diffuse_texname.empty())
            diffuseReflectance = std::unique_ptr<Texture<v3f>>(new ConstantTexture3f(glm::make_vec3(mat.diffuse)));
        else
            diffuseReflectance = std::unique_ptr<Texture<v3f>>(new BitmapTexture3f(config, mat.diffuse_texname));

        exponent = std::unique_ptr<Texture<float>>(new ConstantTexture1f(mat.shininess));

        //get scale value to ensure energy conservation
        v3f maxValue = specularReflectance->getMax() + diffuseReflectance->getMax();
        float actualMax = max(max(maxValue.x, maxValue.y), maxValue.z);
        scale = actualMax > 1.0f ? 0.99f * (1.0f / actualMax) : 1.0f;

        float dAvg = getLuminance(diffuseReflectance->getAverage() * scale);
        float sAvg = getLuminance(specularReflectance->getAverage() * scale);
        specularSamplingWeight = sAvg / (dAvg + sAvg);

        components.push_back(EGlossyReflection);
        components.push_back(EDiffuseReflection);

        combinedType = 0;
        for (unsigned int component : components)
            combinedType |= component;
    }

    inline float getExponent(const SurfaceInteraction& i) const override {
        return exponent->eval(worldData, i);
    }

    inline v3f reflect(const v3f& d) const {
        return v3f(-d.x, -d.y, d.z);
    }

    v3f eval(const SurfaceInteraction& i) const override {
        v3f val(0.f);

        // TODO(A2): Implement this
		float costheta_wi = Frame::cosTheta(glm::normalize(i.wi));
		
		
		if (Frame::cosTheta(glm::normalize(i.wi)) >= 0.f &&
			Frame::cosTheta(glm::normalize(i.wo)) >= 0.f) {
			v3f pd = diffuseReflectance->eval(worldData, i);
			v3f ps = specularReflectance->eval(worldData, i);
			float e = exponent->eval(worldData, i);
			float cosAngle = abs(glm::dot(PhongBSDF::reflect(glm::normalize(i.wi)),
				glm::normalize(i.wo)));
			val = ((pd / M_PI) + (ps * ((e + 2) / (2 * M_PI))) * max(0.f, powf(cosAngle, e))) *
				PhongBSDF::scale * costheta_wi;
		}

        return val;
    }

    float pdf(const SurfaceInteraction& i) const override {
        float pdf = 0.f;

        // TODO(A3): Implement this
		float diff_pdf, spec_pdf;
		float n = exponent->eval(worldData, i);
		diff_pdf = Warp::squareToCosineHemispherePdf(i.wi);
		v3f w_r = reflect(glm::normalize(i.wo));
		v3f w_i = Frame(w_r).toLocal(i.wi);
		spec_pdf = Warp::squareToPhongLobePdf(w_i, n);
		return (1.0f - specularSamplingWeight) * diff_pdf + specularSamplingWeight * spec_pdf;

    }

    v3f sample(SurfaceInteraction& i, Sampler& sampler, float* pdf) const override {
        v3f val(0.f);

        // TODO(A3): Implement this
		if (sampler.next() < specularSamplingWeight) {
			float e = PhongBSDF::exponent->eval(worldData, i);
			i.wi = Warp::squareToPhongLobe(sampler.next2D(), e);
			v3f w_r = reflect(glm::normalize(i.wo));
			i.wi = Frame(w_r).toWorld(i.wi);
			*pdf = this->pdf(i);
		}
		else {
			i.wi = Warp::squareToCosineHemisphere(sampler.next2D());
			*pdf = this->pdf(i);
		}
		if (*pdf == 0.0f) {
			
			return v3f(0.0f); 
		}


		val = eval(i) / *pdf;
        return val;
    }

    std::string toString() const override { return "Phong"; }
};

TR_NAMESPACE_END