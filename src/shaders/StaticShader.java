package shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Light;
import utils.Maths;

/**
 * Shader for the static entities (trees, foliage, rocks).
 * Implements simple phong model.
 *
 */
public class StaticShader extends ShaderProgram{
    
    private static final String VERTEX_FILE = "src/shaders/vertexShader.glsl";
    private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.glsl";
    
    private int location_transformationMatrix;
    private int location_projectionMatrix; 
    private int location_viewMatrix; 
    private int location_lightPosition; 
    private int location_lightColor; 
    private int location_shineDumper; 
    private int location_reflectivity; 
    private int location_useFakeLighting;
    private int location_fakeNormal;
    private int location_visibilityDistance;
    private int location_textureSampler;;
    private int location_noiseTexture;
    private int location_plane;
    private int location_ambient;
    
    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");  
        bindAttribute(1, "textureCoords");
        bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocations() {
    	// transformation matrix
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");       
        // projection matrix
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");    
        // view matrix
        location_viewMatrix = super.getUniformLocation("viewMatrix");  
        //light position
        location_lightPosition = super.getUniformLocation("lightPosition");
        //light color
        location_lightColor = super.getUniformLocation("lightColor");
        // specular multiplier (higher value means more intense, but smaller specular reflection)
        location_shineDumper = super.getUniformLocation("shineDumper");
        // insensity of the specular 
        location_reflectivity = super.getUniformLocation("reflectivity");
        // ratio for fake lighting -> all normals in the object are blended with the fake normal
        location_useFakeLighting = super.getUniformLocation("useFakeLighting");
        // the direction of fake normal (usually comes from the normal of the underlying terrain)
        location_fakeNormal = super.getUniformLocation("fakeNormal");
        // maximum distance from camera when the entity is rendered
        location_visibilityDistance = super.getUniformLocation("visibilityDistance");
        // textures
        location_textureSampler = super.getUniformLocation("textureSampler");
        // noise texture used for dissolve effect
        location_noiseTexture = super.getUniformLocation("noiseTexture");
        // clip plane
        location_plane = super.getUniformLocation("plane");
        // ambient light value (0 - 1)
        location_ambient = super.getUniformLocation("ambient");
    }
    
    public void loadClipPlane(Vector4f plane) {
    	super.loadVector(location_plane, plane);
    }
    
    public void connectTextureUnits() {
    	super.loadInt(location_textureSampler, 0);
    	super.loadInt(location_noiseTexture, 5);
    }
    
    public void loadShineVariables(float shineDump, float reflectivity) {
        loadFloat(location_shineDumper, shineDump);
        loadFloat(location_reflectivity, reflectivity);
    }
    
    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }
    
    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(location_projectionMatrix, matrix);
    }
    
    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }
    
    public void loadLight(Light light) {
        super.loadVector(location_lightPosition, light.getPosition());
        super.loadVector(location_lightColor, light.getColor());
    }
    
    public void loadFakeLightingVariable(float useFake, Vector3f fakeNormal, float ambient) {
        super.loadFloat(location_useFakeLighting, useFake);
        super.loadVector(location_fakeNormal, fakeNormal);
        super.loadFloat(location_ambient, ambient);
    }
    
    public void loadVisibilityDistance(float visibility) {
    	super.loadFloat(location_visibilityDistance, visibility);
    }

}
