/*
    This file is part of TinyRender, an educative rendering system.

    Designed for ECSE 446/546 Realistic/Advanced Image Synthesis.
    Derek Nowrouzezahrai, McGill University.
*/

#version 330 core


uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 normalMat;

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;

out vec3 vNormal;

void main()
{
    // TODO(A1): Implement this
	gl_Position = projection * view * model * (vec4(position.x,position.y,position.z,1));

	vNormal =  vec3(normalMat * vec4(normal, 1.0));

}
