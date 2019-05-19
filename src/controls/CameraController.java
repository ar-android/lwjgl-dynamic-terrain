package controls;

import org.lwjgl.util.vector.Vector3f;
import entities.Camera;

/**
 * Interface for camera controllers
 *
 */
public interface CameraController {

	/**
	 * processes inputs and outputs direction vector in which the camera should move
	 * @param deltaTime time since the last frame was rendered
	 * @param camera camera object
	 * @return direction vector to move camera
	 */
	public abstract Vector3f[] processInputs(int deltaTime, Camera camera);
	
	
}
