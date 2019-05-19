package terrain;

import org.lwjgl.util.vector.Vector3f;

import models.RawModel;
import renderEngine.Loader;
import textures.TerrainTexturePack;

/**
 * Class representing the terrain object
 * @author smid
 *
 */
public class Terrain {
	
	// four textures for the terrain mapped based on altitude
    private TerrainTexturePack texturePack;
    
    // total size in meters
    private int size;
    // number of vertices on one edge 128 parts -> 129 vertices
    public static final int VERTEX_COUNT = 128 + 1;
    public static final int LOD_SCALE = 4;
    
    private int xPosition;
    private int zPosition;
    private Loader loader;
    
    // two models to swap quickly on update
    private RawModel model0;
    private RawModel model1;
    // which one of the two models is being rendered
    private int activeModel = 0;
   
    private TerrainGenerator generator;
    
    // terrain dto for the newly generated vertices
    private TerrainDto tmp;
    
    public Terrain(int x, int z, int size, Loader loader, TerrainTexturePack texPack, TerrainGenerator generator) {
        this.texturePack = texPack;
        this.size = size;
        this.xPosition = x;
        this.zPosition = z;
        this.generator = generator;
        this.model0 = createNewTerrain(loader, generateTerrain(128, 128, xPosition, zPosition, false)); 
        this.model1 = createNewTerrain(loader, generateTerrain(128, 128, xPosition, zPosition, false));  
        this.loader = loader;
    }
    
    // regenerate the model, put the result to tmp (used for large terrain with hole for higher LOD)
    public void regenerate(int x, int y) {
        tmp = generateTerrain(x, y, xPosition, zPosition, false);
    }
    
    // regenerate model with conntecting the edges to the lower LOD
    public void regenerate(int x, int y, int xShift, int zShift, boolean connect) {
        tmp = generateTerrain(x, y, xShift, zShift, connect);
    }
    
    // swap the active terrains when loaded
    public void swap() {
        updateTerrain(loader, tmp);
        activeModel = activeModel == 1 ? 0 : 1;
    }
    
    /**
     * generates terrain coordinates, with hole for higher LOD
     * @param x coordinate of the hole <0, 127>, 128 - no hole  
     * @param y coordinate of the hole <0,127>, 128 - no hole  
     * @return
     */
    private TerrainDto generateTerrain(int x, int y, int xShift, int zShift, boolean connect){
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        int xShiftTile = xShift/(size/(VERTEX_COUNT - 1));
        int zShiftTile = zShift/(size/(VERTEX_COUNT - 1));
        for(int i=0;i<VERTEX_COUNT;i++){
            for(int j=0;j<VERTEX_COUNT;j++){
                    vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * size + xShift;
                    
                    if(connect && (i == 0 || i == VERTEX_COUNT - 1 || j == 0 || j == VERTEX_COUNT - 1)) {
                        vertices[vertexPointer*3+1] = (float) (getHeight(i + zShiftTile, j + xShiftTile, size/(VERTEX_COUNT - 1) * LOD_SCALE));
                    }else {
                        vertices[vertexPointer*3+1] = (float) (getHeight(i + zShiftTile, j + xShiftTile));
                    } 
                    
                    vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * size + zShift;
                    Vector3f normal = calculateNormal(i + zShiftTile, j + xShiftTile);
                    normals[vertexPointer*3] = normal.x;
                    normals[vertexPointer*3+1] = normal.y;
                    normals[vertexPointer*3+2] = normal.z;
                    textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
                    textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
                    vertexPointer++;                                 
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                if(!((gx >= x && gx < x + (VERTEX_COUNT - 1)/LOD_SCALE) && (gz >= y && gz < y + (VERTEX_COUNT - 1)/LOD_SCALE))) {
                    int topLeft = (gz*VERTEX_COUNT)+gx;
                    int topRight = topLeft + 1;
                    int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                    int bottomRight = bottomLeft + 1;
                    indices[pointer++] = topLeft;
                    indices[pointer++] = bottomLeft;
                    indices[pointer++] = topRight;
                    indices[pointer++] = topRight;
                    indices[pointer++] = bottomLeft;
                    indices[pointer++] = bottomRight;
                }
            }
        }
        
        return new TerrainDto(vertices, normals, textureCoords, indices);
    }
    
    private RawModel createNewTerrain(Loader loader, TerrainDto dto) {
        return loader.loadToVAO(dto.vertices, dto.textureCoords, dto.normals, dto.indices, true);
    }
    
    /**
     * replaces the inactive terrain with newly generated one (need to swap to show terrain)
     * @param loader
     * @param dto
     */
    private void updateTerrain(Loader loader, TerrainDto dto) {
        //System.out.println("update terrain");
        RawModel newModel = activeModel == 0 ? model1 : model0;
        loader.replaceVAO(newModel, dto.vertices, dto.textureCoords, dto.normals, dto.indices); 
    }
    
    private Vector3f calculateNormal(int x, int y) {
        return generator.getNormal((int)(x * size/(VERTEX_COUNT - 1)), (int)(y * size/(VERTEX_COUNT - 1)));
    }
    
    private float getHeight(int x, int y) {
    	    return generator.getHeight(x * size/(VERTEX_COUNT - 1),y * size/(VERTEX_COUNT - 1));
    }   

    private float getHeight(int x, int y, int conntectFactor) {
        return generator.getHeight(x * size/(VERTEX_COUNT - 1),y * size/(VERTEX_COUNT - 1), conntectFactor);
}   
    
    public float getX() {
        return xPosition;
    }

    public float getY() {
        return zPosition;
    }
    
    public void setPosition(int x, int z) {
        this.xPosition = x;
        this.zPosition = z;
    }

    public RawModel getModel() {
        return activeModel == 0 ? model0 : model1;
    }
    
    public TerrainTexturePack getTexturePack() {
		return texturePack;
	}

	public float getSize() {
        return size;
    }
	
	public float getScaleMultiplier() {
		return size/(VERTEX_COUNT - 1);
	}
    
    class TerrainDto {
        float[] vertices;
        float[] normals;
        float[] textureCoords;
        int[] indices;
        
        public TerrainDto(float[] vertices, float[] normals, float[] textureCoords, int[] indices) {
            super();
            this.vertices = vertices;
            this.normals = normals;
            this.textureCoords = textureCoords;
            this.indices = indices;
        }
        
    }

}
