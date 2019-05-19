package models;

/**
 * Pure model loaded in GPU
 *
 */
public class RawModel {
    
    private int vaoId;	// Vertex array object id
    private int vertexCount;	// number of vertices
    private int[] vboIds;	// array of vertex buffer objects
    
    public RawModel(int vaoId, int vertexCount, int[] vboIds) {
        super();
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
        this.vboIds = vboIds;
    }
    
    public int getVaoId() {
        return vaoId;
    }
    public int getVertexCount() {
        return vertexCount;
    }
    
    public int[] getVboIds() {
        return vboIds;
    }

}
