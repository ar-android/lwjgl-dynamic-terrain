#version 400 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector;
in vec3 toCameraVector;
in float height;

out vec4 out_Color;

uniform sampler2D texture0;
uniform sampler2D texture1;
uniform sampler2D texture2;
uniform sampler2D texture3;
uniform vec3 lightColor;
uniform float scaleMultiplier;

void main(void){

	vec2 textureCoordsL = pass_textureCoords * 128 / scaleMultiplier * scaleMultiplier;
	vec2 textureCoordsH = pass_textureCoords * 32 / scaleMultiplier * scaleMultiplier;
	
	float closeTilingL = 60;
	float closeTilingH = 230;
	
	if(scaleMultiplier > 8){
		closeTilingL = 300;
		closeTilingH = 600;		
		vec2 textureCoordsH = pass_textureCoords * 64 / scaleMultiplier * scaleMultiplier;
	}
	
	float closeTilingRatio = 0;
	float distance = length(toCameraVector);
	//closeTilingRatio = max(1, min(0, (distance - closeTilingL) / (closeTilingH - closeTilingL)));	
	if(distance < closeTilingL){
		closeTilingRatio = 0;
	}else if( distance < closeTilingH){
		closeTilingRatio = (distance - closeTilingL) / (closeTilingH - closeTilingL);	
	}else{
		closeTilingRatio = 1;
	}

	float level0 = 40;
	float level1 = 80;
	float level2 = 130;
	float gradient0 = 5;
	float gradient1 = 20;
	float gradient2 = 8;

	float add0 = 0;
	float add1 = 0;
	float add2 = 0;
	float add3 = 0;
	
	if(height < level0){
		add0 = 1;
	}else if(height < level0 + gradient0){
		add1 = (height - level0)/gradient0;
		add0 = 1 - add1;
	}else if(height < level1){
		add1 = 1;
	}else if(height < level1 + gradient1){
		add2 = (height - level1)/gradient1;
		add1 = 1 - add2;
	}else if(height < level2){
		add2 = 1;
	}else if(height < level2 + gradient2){
		add3 = (height - level2)/gradient2;
		add2 = 1 - add3;
	}else{
		add3 = 1;
	}
	
	vec4 tex0colorL = texture(texture0, textureCoordsL) * add0;
	vec4 tex1colorL = texture(texture1, textureCoordsL) * add1;
	vec4 tex2colorL = texture(texture2, textureCoordsL) * add2;
	vec4 tex3colorL = texture(texture3, textureCoordsL) * add3;
	
	vec4 tex0colorH = texture(texture0, textureCoordsH) * add0;
	vec4 tex1colorH = texture(texture1, textureCoordsH) * add1;
	vec4 tex2colorH = texture(texture2, textureCoordsH) * add2;
	vec4 tex3colorH = texture(texture3, textureCoordsH) * add3;

	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitLightVector = normalize(toLightVector);
	
	float nDotP = dot(unitNormal, unitLightVector);
	float brightness = max(0.3, nDotP);
	vec3 diffuse = brightness * lightColor;

	out_Color = vec4(diffuse, 1.0) * ((tex0colorL + tex1colorL+ tex2colorL + tex3colorL) * (1 - closeTilingRatio) + (tex0colorH + tex1colorH + tex2colorH + tex3colorH) * closeTilingRatio);
	//out_Color = vec4(diffuse, 1.0) * (vec4(1, 0, 0, 1)* closeTilingRatio + vec4(0, 0, 1, 1) * (1 - closeTilingRatio));


}













