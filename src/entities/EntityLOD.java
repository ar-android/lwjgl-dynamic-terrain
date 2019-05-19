package entities;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

/**
 * Implementation of Level of Detail.
 *
 */
public class EntityLOD extends Entity {

	private List<TexturedModel> models; // contains N LODs from most simple to most detailed
	private List<Float> distances; // contains N - 1 distances to switch LOD (from 0 to 1 as ratio of the
									// viewDistance) in descending order

	/**
	 * Init the entity using LODs
	 * 
	 * @param models             an array of models from the most simple to most
	 *                           detailed
	 * @param position           position in the scene
	 * @param rotX               x rotation
	 * @param rotY               y rotation
	 * @param rotZ               z rotation
	 * @param scale              uniform scale of the model
	 * @param visibilityDistance maximum viewing distance for all the models
	 * @param distances          for N models N-1 distances (0 - 1) dividing the
	 *                           visibility distance to parts occupied by the LODs
	 */
	public EntityLOD(List<TexturedModel> models, Vector3f position, float rotX, float rotY, float rotZ, float scale,
			float visibilityDistance, List<Float> distances) {
		super(models.get(0), position, rotX, rotY, rotZ, scale, visibilityDistance);

		this.models = models;
		this.distances = distances;
	}

	/**
	 * returns the correct LOD based on distance from camera, if the object is too far (distance > visibilityDistance), returns null
	 */
	@Override
	public TexturedModel getModelBasedOnDistance(Vector3f cameraPosition) {
		Vector3f.sub(cameraPosition, super.position, super.toCamera);
		if (super.toCamera.length() > super.visibilityDistance) {
			return null;
		}
		for (int i = 0; i < distances.size(); i++) {
			if (super.toCamera.length() > super.visibilityDistance * distances.get(i)) {
				return models.get(i);
			}
		}
		return models.get(models.size() - 1);
	}

}
