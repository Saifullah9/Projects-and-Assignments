/*
    This file is part of TinyRender, an educative rendering system.

    Designed for ECSE 446/546 Realistic/Advanced Image Synthesis.
    Derek Nowrouzezahrai, McGill University.
*/

#pragma once

#include <core/core.h>
#include "core/renderpass.h"
#include "tiny_obj_loader.h"
#include "integrators/path.h"

TR_NAMESPACE_BEGIN

/**
 * Global Illumination baking renderpass.
 */
struct GIPass : RenderPass {
    GLuint shader{0};

    GLuint modelMatUniform{0};
    GLuint viewMatUniform{0};
    GLuint projectionMatUniform{0};

    int m_samplePerVertex;

    std::unique_ptr<PathTracerIntegrator> m_ptIntegrator;

    explicit GIPass(const Scene& scene) : RenderPass(scene) {
        m_ptIntegrator = std::unique_ptr<PathTracerIntegrator>(new PathTracerIntegrator(scene));
        m_ptIntegrator->m_maxDepth = scene.config.integratorSettings.gi.maxDepth;
        m_ptIntegrator->m_rrProb = scene.config.integratorSettings.gi.rrProb;
        m_ptIntegrator->m_rrDepth = scene.config.integratorSettings.gi.rrDepth;
        m_samplePerVertex = scene.config.integratorSettings.gi.samplesByVertex;
    }

    virtual void buildVBO(size_t objectIdx) override {
        GLObject& obj = objects[objectIdx];

        // TODO(A5): Implement this
		obj.nVerts = scene.getObjectNbVertices(objectIdx);
		obj.vertices.resize(obj.nVerts* N_ATTR_PER_VERT);
		for (int i = 0; i < scene.getObjectNbVertices(objectIdx); i++) {
			Sampler sampler = Sampler(260733168);
			v3f v_pos = scene.getObjectVertexPosition(objectIdx, i);
			v3f v_norm = scene.getObjectVertexNormal(objectIdx, i);
			v_pos += v_norm * Epsilon;

			v3f wo = v_norm;
			SurfaceInteraction& data = SurfaceInteraction();
			data.wo = v3f(0.f, 0.f, 1.f);
			data.p = v_pos;
			data.shapeID = objectIdx;
			data.primID = scene.getPrimitiveID(i);
			data.frameNs = Frame(v_norm);
			data.frameNg = Frame(v_norm);
			data.matID = scene.getMaterialID(objectIdx, data.primID);
		
			Ray ray = Ray(data.p, data.wo);
			v3f vertexColor(0.f);
			for (int j = 0; j < m_samplePerVertex; j++) {
				vertexColor += m_ptIntegrator->renderExplicit(ray, sampler, data);
			}
			vertexColor = vertexColor / m_samplePerVertex;
			obj.vertices[(6 * i) + 0] = v_pos.x;
			obj.vertices[(6 * i) + 1] = v_pos.y;
			obj.vertices[(6 * i) + 2] = v_pos.z;

			obj.vertices[(6 * i) + 3] = vertexColor.x;
			obj.vertices[(6 * i) + 4] = vertexColor.y;
			obj.vertices[(6 * i) + 5] = vertexColor.z;

		}

        // VBO
        glGenVertexArrays(1, &obj.vao);
        glBindVertexArray(obj.vao);

        glGenBuffers(1, &obj.vbo);
        glBindBuffer(GL_ARRAY_BUFFER, obj.vbo);
        glBufferData(GL_ARRAY_BUFFER,
                     sizeof(GLfloat) * obj.nVerts * N_ATTR_PER_VERT,
                     (GLvoid*) (&obj.vertices[0]),
                     GL_STATIC_DRAW);
    }

    bool init(const Config& config) override {
        RenderPass::init(config);

        // Create shader
        GLuint vs = compileShader("gi.vs", GL_VERTEX_SHADER);
        GLuint fs = compileShader("gi.fs", GL_FRAGMENT_SHADER);
        shader = compileProgram(vs, fs);
        glDeleteShader(vs);
        glDeleteShader(fs);

        // Create uniforms
        modelMatUniform = GLuint(glGetUniformLocation(shader, "model"));
        viewMatUniform = GLuint(glGetUniformLocation(shader, "view"));
        projectionMatUniform = GLuint(glGetUniformLocation(shader, "projection"));

        // Create vertex buffers
        objects.resize(scene.worldData.shapes.size());
        for (size_t i = 0; i < objects.size(); i++) {
            buildVBO(i);
            buildVAO(i);
        }

        return true;
    }

    void cleanUp() override {
        // Delete vertex buffers
        for (size_t i = 0; i < objects.size(); i++) {
            glDeleteBuffers(1, &objects[i].vbo);
            glDeleteVertexArrays(1, &objects[i].vao);
        }

        RenderPass::cleanUp();
    }

    void render() override {
        glBindFramebuffer(GL_FRAMEBUFFER, postprocess_fboScreen);
        glClearColor(0.f, 0.f, 0.f, 1.f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        // TODO(A5): Implement this
		glUseProgram(shader);
		glm::mat4 m, v, p;
		camera.Update();
		camera.GetMatricies(p, v, m);

		glUniformMatrix4fv(modelMatUniform,1,GL_FALSE, &(modelMat[0][0]));
		glUniformMatrix4fv(viewMatUniform, 1, GL_FALSE, &(v[0][0]));
		glUniformMatrix4fv(projectionMatUniform, 1, GL_FALSE, &(p[0][0]));
		
		for (auto& object : objects) {
			glEnableVertexAttribArray(0);
			glBindVertexArray(object.vao);
			glDrawArrays(GL_TRIANGLES, 0, object.nVerts);
			glDisableVertexAttribArray(0);
		}


        RenderPass::render();
    }

};

TR_NAMESPACE_END
