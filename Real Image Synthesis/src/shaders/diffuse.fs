/*
I added this new file
*/
#version 330 core

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 normalMat;
uniform vec3 camPos;
uniform vec3 lightPos;
uniform vec3 lightIntensity;
uniform vec3 albedo;
in vec3 vPos;
in vec3 vNormal;
out vec3 color;

void main(){

vec3 wi = normalize(lightPos - vPos);
float cosNorm = dot(wi,vNormal);
vec3 distSquared = pow(vPos - lightPos,vec3(2));
float floatDistSquared = distSquared.x+distSquared.y+distSquared.z;
vec3 flux = lightIntensity/floatDistSquared;

if(cosNorm > 0){
	color = flux*albedo*cosNorm/3.14159265359;
}else{

color = vec3(0.f);
}

}