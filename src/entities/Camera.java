package entities;

import org.lwjgl.util.vector.Vector3f;

import controls.CameraController;

/**
 * Simple camera class handling the position, rotation and update of these values.
 *
 */
public class Camera {

	private Vector3f position = new Vector3f(600, 200, 700);
	private float pitch;
	private float yaw;

	/**
	 * Move the camera to new location, called every frame.
	 * @param controller class used to calculate the new transforms
	 * @param delta time since last frame
	 */
	public void move(CameraController controller, int delta) {
		
		Vector3f[] userMove = controller.processInputs(delta, this);	
	
		position.x += userMove[0].x;
		position.y += userMove[0].y;
		position.z += userMove[0].z;	
		pitch = userMove[1].y;
		yaw = userMove[1].x;		
		
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getPitch() {
		return pitch;
	}
	
	public void invertPitch() {
		pitch = -pitch;
	}

	public float getYaw() {
		return yaw;
	}

}
