package utils;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;

/**
 * Math utils
 * @author smid
 *
 */
public class Maths {
    
	/**
	 * create transform matrix based on translation, rotation axes in degrees and scale
	 * @param translation movement
	 * @param rx rotate x
	 * @param ry rotate y
	 * @param rz rotate z
	 * @param scale uniform scale
	 * @return transformation matrix
	 */
    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix , matrix);
        Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix , matrix);
        Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix , matrix);
        Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
        
        return matrix;
    }

    /**
     * Produces view matrix based on camera (which holds position and rotation)
     * @param camera object 
     * @return view matrix
     */
    public static Matrix4f createViewMatrix(Camera camera) {
        
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        
        
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), matrix , matrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), matrix , matrix);
        
        Vector3f negativePosition = new Vector3f(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);
        Matrix4f.translate(negativePosition, matrix, matrix);
        
        
        return matrix;
    }
    
    
    /**
     * rotates vector by around vector by angle in degrees
     * @param npos vector to rotete
     * @param nrot vector to rotate around
     * @param rotation angle in degrees
     * @return final vector
     */
    public static Vector3f rotV3fByV3f(Vector3f npos, Vector3f nrot, float rotation) {
    	   Matrix4f matrix = new Matrix4f();

    	   Vector3f pos = new Vector3f(npos);

    	   matrix.m03 = pos.x;
    	   matrix.m13 = pos.y;
    	   matrix.m23 = pos.z;

    	   Vector3f rot = new Vector3f(nrot);

    	   Matrix4f.rotate((float) Math.toRadians(rotation), rot, matrix, matrix);

    	   return new Vector3f(matrix.m03, matrix.m13, matrix.m23);
    	}
    
    /**
     * create transformation matrix without rotation
     * @param translation move vector
     * @param scale uniform scale vector
     * @return transformation matrix
     */
    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
        return matrix;
    }
    
}
