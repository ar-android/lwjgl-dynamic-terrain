package controls;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import renderEngineTest.MainGameLoop;
import utils.Maths;

/**
 * Class implementing the basic fly around movement
 *
 */
public class FreeFlySmooth implements CameraController {

	private float speed = 0.2f;
	private float smooth = 0.07f; // ratio between new data and old data
	private float pitchSpeed = 0.17f;
	private float yawSpeed = 0.15f;
	private float pitchLimit = 80;
	private float newPitch = 0;
	private Vector3f up = new Vector3f(0, 1, 0);

	private Vector3f direction = new Vector3f();
	private Vector3f directionMovement = new Vector3f();
	private Vector3f directionMovementFinal = new Vector3f();
	private Vector3f movementOutput = new Vector3f();

	private Vector3f rotation = new Vector3f();
	private boolean mouseButton1 = false;

	public FreeFlySmooth() {
		// use default values
	}

	public FreeFlySmooth(float speed, float smooth, float pitchSpeed, float yawSpeed) {
		super();
		this.speed = speed;
		this.smooth = smooth;
		this.pitchSpeed = pitchSpeed;
		this.yawSpeed = yawSpeed;
	}

	public Vector3f[] processInputs(int deltaTime, Camera camera) {

		// grab and ungrab mouse
		if (Mouse.isButtonDown(0) && !mouseButton1 && !Mouse.isGrabbed()) {
			Mouse.setGrabbed(true);
		} else if (((Mouse.isButtonDown(0) && !mouseButton1) || (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)))
				&& Mouse.isGrabbed()) {
			Mouse.setGrabbed(false);
		}
		mouseButton1 = Mouse.isButtonDown(0);

		// if grabbed, control the look vector with mouse
		if (Mouse.isGrabbed()) {
			newPitch = rotation.y - Mouse.getDY() * pitchSpeed;
			if (Math.abs(newPitch) <= pitchLimit) {
				rotation.y = newPitch;
			}
			rotation.x += Mouse.getDX() * yawSpeed;
		}

		// process key buffer
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) { // key pressed
				if (Keyboard.getEventKey() == Keyboard.KEY_W) {
					direction.z = -1;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_D) {
					direction.x = 1;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_S) {
					direction.z = 1;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_A) {
					direction.x = -1;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_Q) {
					direction.y = -1;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_E) {
					direction.y = 1;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_I) {
					this.speed += 0.01f;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_K) {
					this.speed -= 0.01f;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_P) {
					if (MainGameLoop.walk) {
						this.speed = 0.2f;
						this.smooth = 0.07f;
					} else {
						this.speed = 0.02f;
						this.smooth = 0.5f;
						// On change of controller resets the vertical acceleration
						WalkController.setFallSpeedZero();
						// for falling adjust gravity to realistic values
						WalkController.adjustGravity();
					}
					MainGameLoop.walk = !MainGameLoop.walk;
				}
			} else { // key released
				if (Keyboard.getEventKey() == Keyboard.KEY_W) {
					if (direction.z == -1) {
						direction.z = 0;
					}
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_D) {
					if (direction.x == 1) {
						direction.x = 0;
					}
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_S) {
					if (direction.z == 1) {
						direction.z = 0;
					}
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_A) {
					if (direction.x == -1) {
						direction.x = 0;
					}
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_Q) {
					if (direction.y == -1) {
						direction.y = 0;
					}
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_E) {
					if (direction.y == 1) {
						direction.y = 0;
					}
				}
			}
		}

		// normalize the direction, if not zero
		if (direction.length() != 0) {
			direction.normalise(directionMovement);
		} else {
			directionMovement.set(0, 0, 0);
		}

		// resolve the smooth delta
		directionMovementFinal.scale(1 - smooth);
		directionMovement.scale(smooth);
		Vector3f.add(directionMovementFinal, directionMovement, directionMovementFinal);

		// move based on time, not framerate
		movementOutput.set(directionMovementFinal.x * deltaTime * speed, directionMovementFinal.y * deltaTime * speed,
				directionMovementFinal.z * deltaTime * speed);

		// rotate movement according to view (yaw)
		movementOutput = Maths.rotV3fByV3f(movementOutput, up, rotation.x);

		return new Vector3f[] { movementOutput, rotation };
	}

}
