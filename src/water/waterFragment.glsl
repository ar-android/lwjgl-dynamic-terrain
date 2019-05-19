#version 400 core

in vec4 clipSpace;
in vec2 textureCoords;
in vec3 toCamera;
in vec3 fromLight;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudv;
uniform sampler2D depthMap;
uniform sampler2D normalMap;

uniform float moveFactor;
uniform vec3 lightColor;

const float distortionStrength = 0.05;
const float shineDumper = 40.0;
const float reflectivity = 0.7;
const vec4 murkyWaterColour = vec4(0.05, 0.08, 0.12, 1.0);


void main(void) {

	vec2 ndc = (clipSpace.xy/clipSpace.w)/2.0 + 0.5;	
	vec2 reflectionTexCoords = vec2(ndc.x, -ndc.y);
	vec2 refractionTexCoords = vec2(ndc.x, ndc.y);
	
	float near = 1;
	float far = 3000;
	float depth = texture(depthMap, refractionTexCoords).r;
	float floorDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));
	depth = gl_FragCoord.z;
	float waterDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));
	
	float waterDepth = floorDistance - waterDistance;
	
	vec2 distortedTexCoords = texture(dudv, vec2(textureCoords.x + moveFactor, textureCoords.y)).rg*0.1;
	distortedTexCoords = textureCoords + vec2(distortedTexCoords.x, distortedTexCoords.y+moveFactor);
	vec2 totalDistortion = (texture(dudv, distortedTexCoords).rg * 2.0 - 1.0) * distortionStrength * clamp(waterDepth/100.0, 0.0, 1.0);
	
	refractionTexCoords += totalDistortion;
	reflectionTexCoords += totalDistortion;
	
	refractionTexCoords = clamp(refractionTexCoords, 0.001, 0.999);
	reflectionTexCoords.x = clamp(reflectionTexCoords.x, 0.001, 0.999);
	reflectionTexCoords.y = clamp(reflectionTexCoords.y, -0.999, -0.001);

	vec4 reflectionColor = texture(reflectionTexture, reflectionTexCoords);
	vec4 refractionColor = texture(refractionTexture, refractionTexCoords);
	refractionColor = mix(refractionColor, murkyWaterColour, clamp(waterDepth/130.0, 0.0, 1.0));

	vec4 normalMapColor = texture(normalMap, distortedTexCoords);
	vec3 normal = vec3(normalMapColor.r * 2.0 - 1.0, normalMapColor.b * 3, normalMapColor.g * 2.0 - 1.0);
	normal = normalize(normal);
	
	vec3 viewVector = normalize(toCamera);
	float fresnel = pow(dot(viewVector, normal), 0.75);
	
	vec3 reflectedLight = reflect(normalize(fromLight), normal);
	float specular = max(dot(reflectedLight, viewVector), 0.0);
	specular = pow(specular, shineDumper);
	vec3 specularHighlights = lightColor * specular * reflectivity * clamp(waterDepth/5.0, 0.0, 1.0);

	out_Color = mix(reflectionColor, refractionColor, fresnel);
	out_Color = mix(out_Color, vec4(0.0, 0.2, 0.3, 1.0), 0.2) + vec4(specularHighlights, 0.0);
	
}