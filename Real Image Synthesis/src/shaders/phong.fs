#version 330 core

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 normalMat;
uniform vec3 lightPos;
uniform vec3 lightIntensity;
uniform vec3 camPos;
uniform vec3 rho_d;
uniform vec3 rho_s;
uniform int exponent;
//uniform sampler2D diffuseTexture;
//uniform sampler2D shadowMap;
in vec3 vPos;
in vec3 vNormal;
//in vec2 TexCoords;
//in vec4 FragPosLightSpace;
out vec3 color;


//bool shadowMapping(vec4 fPLSpace){
//	vec3 pCoord = fPLSpace.xyz / fPLSpace.w;
//	pCoord = pCoord*0.5 + 0.5;
//	float nearestDepth = texture(shadowMap, pCoord.xy).r;
//	float currentDepth = pCoord.z;
//	bool isshadow = currentDepth > nearestDepth ?  true: false;
//	return isshadow;
//	}
	

void main(){

vec3 w_i = normalize(lightPos - vPos);
float distSquared = pow(distance(lightPos, vPos),2);
float cosIncoming = dot(w_i, vNormal);
vec3 intensityPerDistSquared = lightIntensity/distSquared;
//bool isshadow = shadowMapping(FragPosLightSpace);

if(cosIncoming > 0){
vec3 w_r = 2 * (vNormal * dot(vNormal, w_i)) - w_i;
vec3 w_o = vec3(camPos.x,camPos.y,camPos.z) - vPos;
float cosWr = dot(normalize(w_r),normalize(w_o));
vec3 phongEq = (rho_d / 3.14159265359) + (rho_s * (exponent + 2) / (2 * 3.14159265359))*pow(cosWr,exponent);
color = phongEq*intensityPerDistSquared*cosIncoming;

}else{
	color = vec3(0.f,0.f,0.f);
}

}