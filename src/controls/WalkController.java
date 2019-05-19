package controls;

import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import terrain.TerrainGenerator;

/**
 * Class implementing the FPS like walking on the terrain. Uses the terrain
 * generator to get collision points, composition of the FreeFlySmooth to handle
 * inputs.
 *
 */
public class WalkController implements CameraController {

	private FreeFlySmooth freeFly;
	private TerrainGenerator generator;
	private static final float PLAYER_HEIGHT = 1.8f;
	private static float GRAVITY = -0.02f;
	private static float fallSpeed = 0;
	private static boolean gravityLow = false;

	public WalkController(TerrainGenerator generator, FreeFlySmooth flySmooth) {
		this.freeFly = flySmooth;
		this.generator = generator;
	}

	public Vector3f[] processInputs(int deltaTime, Camera camera) {

		Vector3f[] movementVector = freeFly.processInputs(deltaTime, camera);
		Vector3f positionDelta = movementVector[0];

		// solving the height
		float terrainHeight = generator.getHeight(camera.getPosition().z + positionDelta.z,
				camera.getPosition().x + positionDelta.x) + PLAYER_HEIGHT;

		// in the air
		if (terrainHeight < camera.getPosition().y) {
			float gchange = GRAVITY * deltaTime + fallSpeed;
			if (camera.getPosition().y + gchange > terrainHeight) {
				positionDelta.y = gchange;
				fallSpeed = gchange;
			} else {
				fallSpeed = 0;
				positionDelta.y = terrainHeight - camera.getPosition().y;
			}
		} else {
			// on the ground
			fallSpeed = 0;
			movementVector[0].y = terrainHeight - camera.getPosition().y;

			if (gravityLow) {
				gravityLow = false;
				GRAVITY = -0.02f;
			}
		}
		return movementVector;
	}

	/**
	 * For falling adjust gravity, on lend to ground make gravity great again
	 */
	public static void adjustGravity() {
		GRAVITY = -0.003f;
		gravityLow = true;
	}

	/**
	 * On change of controller resets the vertical acceleration
	 */
	public static void setFallSpeedZero() {
		fallSpeed = 0;
	}

}
