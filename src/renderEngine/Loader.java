package renderEngine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import models.RawModel;
import textures.TextureData;

/**
 * Class used for loading data into GPU
 *
 */
public class Loader {

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    /** creates new Vertec array object, return pointer */
    public int createVAO() {
        int vaoId = GL30.glGenVertexArrays();
        vaos.add(vaoId);
        GL30.glBindVertexArray(vaoId);
        return vaoId;
    }
    
	/**
	 * Loads data into the VAO
	 * @param vertexPositions
	 * @param textureCoordinates
	 * @param normals
	 * @param indices
	 * @param dynamic is the object's shape going to change during time? (terrain is changing, other objects are static)
	 * @return raw model with complete setup
	 */
    public RawModel loadToVAO(float[] vertexPositions, float[] textureCoordinates, float[] normals, int[] indices, boolean dynamic) {
        int vaoId = createVAO();
        int[] vboIds = new int[4];
        vboIds[0] = bindIndicesBuffer(indices, dynamic);
        vboIds[1] = storeDatainAttributeList(0, 3, vertexPositions, dynamic);
        vboIds[2] = storeDatainAttributeList(1, 2, textureCoordinates, dynamic);
        vboIds[3] = storeDatainAttributeList(2, 3, normals, dynamic);
        unbindVAO();
        return new RawModel(vaoId, indices.length, vboIds);
    }
    
    /**
     * Loads just vertices to VAO
     * @param vertexPositions
     * @param coordinateSize
     * @return
     */
    public RawModel loadToVAO(float[] vertexPositions, int coordinateSize) {
        int vaoId = createVAO();
        int[] vboIds = new int[1];
        vboIds[0] = storeDatainAttributeList(0, coordinateSize, vertexPositions, false);
        unbindVAO();
        return new RawModel(vaoId, vertexPositions.length / coordinateSize, vboIds);
    }
    
    /**
     * Replaces VAO with new data
     * @param model
     * @param vertexPositions
     * @param textureCoordinates
     * @param normals
     * @param indices
     */
    public void replaceVAO(RawModel model, float[] vertexPositions, float[] textureCoordinates, float[] normals, int[] indices) {
        //GL30.glBindVertexArray(model.getVaoId());
        updateIndicesBuffer(indices, model.getVboIds()[0]);
        updateDatainAttributeList(model.getVboIds()[1], 0, 3, vertexPositions);
        updateDatainAttributeList(model.getVboIds()[2], 1, 2, textureCoordinates);
        updateDatainAttributeList(model.getVboIds()[3], 2, 3, normals);
        unbindVAO();
    }

    /**
     * Loads texture from file to GPU
     * @param fileName
     * @return textureID
     */
    public int loadTexture(String fileName) {
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture("PNG", new FileInputStream("res/textures/" + fileName + ".png"));
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int id = texture.getTextureID();
        textures.add(id);
        return id;
    }    
    
    /**
     * Loads a cube map texture to the GPU
     * @param textureFiles six file names of the cube map
     * @return textureID
     */
    public int loadCubeMap(String[] textureFiles) {
        int texId = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texId);        
        for (int i = 0; i < textureFiles.length; i++) {
            TextureData data = decodeTextureFile("res/textures/sky/" + textureFiles[i] + ".png");
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
        }
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        textures.add(texId);
        return texId;
    }
    
    /**
     * Decodes texture data from PNG format
     * @param fileName
     * @return
     */
    private TextureData decodeTextureFile(String fileName) {
        int width = 0;
        int height = 0;
        ByteBuffer buffer = null;
        try {
            FileInputStream in = new FileInputStream(fileName);
            PNGDecoder decoder = new PNGDecoder(in);
            width = decoder.getWidth();
            height = decoder.getHeight();
            buffer = ByteBuffer.allocateDirect(4 * width * height);
            decoder.decode(buffer, width * 4, Format.RGBA);
            buffer.flip();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Tried to load texture " + fileName + ", didn't work");
            System.exit(-1);
        }
        return new TextureData(buffer, width, height);
    }

    /**
     * Stores a float array of data in a single VBO
     * @param attributeNumber
     * @param coordinateSize
     * @param data
     * @param dynamic - are the data going to change over the time?
     * @return
     */
    public int storeDatainAttributeList(int attributeNumber, int coordinateSize, float[] data, boolean dynamic) {
        int vboId = GL15.glGenBuffers();
        vbos.add(vboId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        if(!dynamic) {
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        }else {
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STREAM_DRAW);
        }      
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);// unbinds the current vbo
        return vboId;
    }
    
    /**
     * Replaces the values in VBO with new ones
     * @param vboId
     * @param attributeNumber
     * @param coordinateSize
     * @param data
     * @return
     */
    public int updateDatainAttributeList(int vboId, int attributeNumber, int coordinateSize, float[] data) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        FloatBuffer buffer = storeDataInFloatBuffer(data);      
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);        
        //GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);// unbinds the current vbo
        return vboId;
    }


    public void cleanUp() {
        vaos.stream().forEach(vao -> {
            GL30.glDeleteVertexArrays(vao);
        });
        vbos.stream().forEach(vbo -> {
            GL15.glDeleteBuffers(vbo);
        });
        textures.stream().forEach(tex -> {
            GL11.glDeleteTextures(tex);
        });
    }

    /**
     * Binds indices
     * @param indices
     * @param dynamic
     * @return
     */
    private int bindIndicesBuffer(int[] indices, boolean dynamic) {
        int vboId = GL15.glGenBuffers();
        vbos.add(vboId);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        if(!dynamic) {
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        }else {
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STREAM_DRAW);
        } 
        return vboId;     
    }
    
    /**
     * replaces the indices with new ones
     * @param indices
     * @param vboId
     */
    private void updateIndicesBuffer(int[] indices, int vboId) {
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);  // ? maybe dont need to do
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, buffer);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    /**
     * Helper method to store array in buffer and flip the buffer to prepare for reading
     * @param data
     * @return
     */
    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
    
    /**
     * Helper method to store array in buffer and flip the buffer to prepare for reading
     * @param data
     * @return
     */
    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

}
