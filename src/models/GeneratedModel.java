package models;

/**
 * Class describing generation of future entities.
 * Based on this information the generator produces entities to scene.
 *
 */
public class GeneratedModel {

	private TexturedModel model;		// which model and texture to use
	private float density;		// density of generated objets
	private float low;			// lowest height on terrain to generate this
	private float high;			// highest pint in terrain to generate
	private float scaleLow;		// random scale min value
	private float scaleHigh;	// random scale max value
	private boolean fakeNormal;	// use fake normal for foliage or not for real objs
	private float visibilityDistance;	// maximum distance from camera when object is rendered
	private float visDistRandom;	// randomizing the visibility distance for entities
	
	public GeneratedModel(TexturedModel model, float density, float low, float high, float scaleLow, float scaleHigh,
			boolean fakeNormal, float visibilityDistance, float visDistRandom) {
		super();
		this.model = model;
		this.density = density;
		this.low = low;
		this.high = high;
		this.scaleLow = scaleLow;
		this.scaleHigh = scaleHigh;
		this.fakeNormal = fakeNormal;
		this.visibilityDistance = visibilityDistance;
		this.visDistRandom = visDistRandom;
	}

	public TexturedModel getModel() {
		return model;
	}

	public float getDensity() {
		return density;
	}

	public float getLow() {
		return low;
	}

	public float getHigh() {
		return high;
	}

	public float getScaleLow() {
		return scaleLow;
	}

	public float getScaleHigh() {
		return scaleHigh;
	}

	public boolean isFakeNormal() {
		return fakeNormal;
	}

	public float getVisibilityDistance() {
		return visibilityDistance;
	}

	public float getVisDistRandom() {
		return visDistRandom;
	}
	
	
	
}
