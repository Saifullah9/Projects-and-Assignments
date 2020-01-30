/*
    This file is part of TinyRender, an educative rendering system.

    Designed for ECSE 446/546 Realistic/Advanced Image Synthesis.
    Derek Nowrouzezahrai, McGill University.
*/

#version 330 core


#define PI 3.14159265359
#define MAX_NUM_EMITTER_TRIANGLES 40 // Max number of emitter triangles allowed (tuned for A4)
uniform float emitterVertices[MAX_NUM_EMITTER_TRIANGLES*3*3]; // Need to specify a size at compile time (max size is 512 floats)

uniform int nbTriangles; // Use nbTriangles to index the buffer correctly
uniform vec3 lightIrradiance;
uniform vec3 albedo;
uniform vec2 windowSize; // [width, height]

uniform sampler2D cvTerm; // Creates a 2D texture we can sample from (range x,y = [0,1])

in vec3 vNormal;
in vec3 vPos;

out vec3 color;

// Compute edge (v1--v2) contribution
float getEdgeContrib(vec3 v1, vec3 v2, vec3 pos) {
	// Adapt your getEdgeContrib code from the offline part
	float value = 0.f;
// TODO(A4): Implement this
	vec3 d1 = v1 - pos;
	vec3 d2 = v2 - pos;
	vec3 ang1 = d1 / length(d1);
	vec3 ang2 = d2 = d2 / length(d2);

	vec3 g = cross(d2, d1)/length(cross(d2, d1));
	float d_r = dot(g, vNormal);
	value = acos(dot(ang1, ang2)) * d_r;

	return value;
}


void main()
{	
	// 1) Extract vertices of triangles from `emitterVertices` buffer using `nbTriangles`
	// 2) Calculate G term
	// 3) Subtract modification term for G after extracting it from texture (use built-in `texture()` function)
	//	    e.g. `vec3 delta = texture(cvTerm, coords).xyz;`

	color = vec3(0);

    // TODO(A4): Implement this

	for (int i = 0; i < nbTriangles; i++) {
		int j = 9*i;
	    vec3 v_0 = vec3(emitterVertices[j], emitterVertices[j+1], emitterVertices[j+2]);
	    vec3 v_1 = vec3(emitterVertices[j+3], emitterVertices[j+4], emitterVertices[j+5]);
	    vec3 v_2 = vec3(emitterVertices[j+6], emitterVertices[j+7], emitterVertices[j+8]);
		color += getEdgeContrib(v_0, v_1, vPos);
		color += getEdgeContrib(v_1, v_2, vPos);
		color += getEdgeContrib(v_2, v_0, vPos);
	}

	vec3 e_pol = (lightIrradiance * color) / (2*PI);

	vec3 G = (albedo / PI) * e_pol;
	float x = (gl_FragCoord.x / windowSize.x);
	float y = (gl_FragCoord.y / windowSize.y);
	vec2 coordinates = vec2(x, y);
	vec3 dlt = texture(cvTerm, coordinates).xyz;

	color = max(vec3(0, 0, 0), G - dlt);

}

