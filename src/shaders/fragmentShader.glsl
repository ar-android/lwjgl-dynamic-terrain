#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;
in vec4 FragCoords;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform sampler2D noiseTexture;
uniform vec3 lightColor;
uniform float shineDumper;
uniform float reflectivity;
uniform float visibilityDistance;
uniform float ambient;

void main(void){

	float dissolveDistance = 15.0;
	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitLightVector = normalize(toLightVector);
	
	float nDotP = dot(unitNormal, unitLightVector);
	float brightness = max(ambient, nDotP);
	vec3 diffuse = brightness * lightColor;
	
	vec3 unitCameraVector = normalize(toCameraVector);	
	vec3 lightDirection = -unitLightVector;
	
	vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
	float specularFactor = dot(reflectedLightDirection, unitCameraVector);
	specularFactor = max(0.0, specularFactor);
	float dampedFactor = pow(specularFactor, shineDumper);
	vec3 finalSpecular = dampedFactor * reflectivity * lightColor;
	
	vec4 textureColor = texture(textureSampler, pass_textureCoords);
	if(textureColor.a < 0.5){
		discard;
	}
	
	
	if(length(toCameraVector) > visibilityDistance - dissolveDistance){
		vec4 noise = texture(noiseTexture, vec2(gl_FragCoord.x/1280, gl_FragCoord.y/720 ));
		if(length(toCameraVector) > visibilityDistance - noise.x * dissolveDistance){
			discard;
		}		
	}	
	

	out_Color = vec4(diffuse, 1.0) *  textureColor + vec4(finalSpecular, 1.0);
	
}