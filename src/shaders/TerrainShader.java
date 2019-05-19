package shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Light;
import terrain.Terrain;
import utils.Maths;

/**
 * Shader specifically for terrain rendering
 *
 */
public class TerrainShader extends ShaderProgram{

    private static final String VERTEX_FILE = "src/shaders/terrainVertexShader.glsl";
    private static final String FRAGMENT_FILE = "src/shaders/terrainFragmentShader.glsl";
    
    private int location_transformationMatrix;
    private int location_projectionMatrix; 
    private int location_viewMatrix; 
    private int location_lightPosition; 
    private int location_lightColor; 
    private int location_texture0;
    private int location_texture1;
    private int location_texture2;
    private int location_texture3;
    private int location_scaleMultiplier;
    private int location_plane;
    
    public TerrainShader() {
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
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");       
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");    
        location_viewMatrix = super.getUniformLocation("viewMatrix");  
        location_lightPosition = super.getUniformLocation("lightPosition");
        location_lightColor = super.getUniformLocation("lightColor");
        // textures from lowest to highest based on altitude
        location_texture0 = super.getUniformLocation("texture0");
        location_texture1 = super.getUniformLocation("texture1");
        location_texture2 = super.getUniformLocation("texture2");
        location_texture3 = super.getUniformLocation("texture3");
        // what is the scale multiplier of the terrain
        location_scaleMultiplier = super.getUniformLocation("scaleMultiplier");
        // clip plane
        location_plane = super.getUniformLocation("plane");
    }
    
    public void connectTextureUnits() {
    	super.loadInt(location_texture0, 0);
    	super.loadInt(location_texture1, 1);
    	super.loadInt(location_texture2, 2);
    	super.loadInt(location_texture3, 3);
    }
    
    public void loadClipPLane(Vector4f plane) {
    	super.loadVector(location_plane, plane);
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
    
    public void loadTerrainScale(Terrain terrain) {
    	super.loadFloat(location_scaleMultiplier, terrain.getScaleMultiplier());
    }
    
}
