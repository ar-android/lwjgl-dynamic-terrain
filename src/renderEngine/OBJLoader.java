package renderEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import models.RawModel;

/**
 * Loader to read models from OBJ files
 * @author smid
 *
 */
public class OBJLoader {

	/**
	 * Loads the OBJ model to GPU and returns RawModel
	 * @param file file name of the model saved in res/models/
	 * @param loader loader to use for pushing model to GPU
	 * @return complete raw model
	 */
    public static RawModel loadObjModel(String file, Loader loader) {
        FileReader reader = null;
        try {
            reader = new FileReader(new File("res/models/" + file + ".obj"));
        } catch (FileNotFoundException e) {
            System.err.println("Could not load obj model " + file);
            e.printStackTrace();
        }

        BufferedReader bfr = new BufferedReader(reader);
        String line;
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        float[] verticesArray = null;
        float[] normalsArray = null;
        float[] textureArray = null;
        int[] indicesArray = null;

        try {

            // load vertices, normals and UV
            while (true) {
                line = bfr.readLine();
                String[] currentLine = line.split(" ");
                if (line.startsWith("v ")) {
                    Vector3f vert = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    vertices.add(vert);

                } else if (line.startsWith("vn ")) {
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    normals.add(normal);

                } else if (line.startsWith("vt ")) {
                    Vector2f tex = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
                    textures.add(tex);

                } else if (line.startsWith("f ")) {
                    textureArray = new float[vertices.size() * 2];
                    normalsArray = new float[vertices.size() * 3];
                    break;
                }
            }

            // load faces
            while (line != null) {
                // skip line if does not describe face
                if (!line.startsWith("f ")) {
                    line = bfr.readLine();
                    continue;
                }

                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                
                processVertex(vertex1, indices, textures, normals, textureArray, normalsArray);
                processVertex(vertex2, indices, textures, normals, textureArray, normalsArray);
                processVertex(vertex3, indices, textures, normals, textureArray, normalsArray);

                line = bfr.readLine();
            }
            
            bfr.close();

        } catch (Exception e) {
            System.err.println("Wrong file format.");
            e.printStackTrace();
        }
        
        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[indices.size()];
        
        int vertexPointer = 0;
        for(Vector3f vertex:vertices) {
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }
        
        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }
        return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray, false);

    }

    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures,
            List<Vector3f> normals, float[] textureArray, float[] normalArray) {
        
        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);
        Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
        textureArray[currentVertexPointer * 2] = currentTex.x;
        textureArray[currentVertexPointer * 2 + 1] = 1 - currentTex.y;
        Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
        normalArray[currentVertexPointer * 3] = currentNorm.x;
        normalArray[currentVertexPointer * 3 + 1] = currentNorm.y;
        normalArray[currentVertexPointer * 3 + 2] = currentNorm.z;
        
    }

}




































