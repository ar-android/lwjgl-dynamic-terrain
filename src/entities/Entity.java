package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;
import textures.ModelTexture;

/**
 *
 * Class describing an entity - spawned instance of an object in the scene.
 *
 */
public class Entity {
    
    private TexturedModel model;	// model to render
    protected Vector3f position;	// position
    private float rotX, rotY, rotZ;	// rotation
    private float scale;   			// scale
    // fake normals (all the normals in models set to the fake one, used for foliage)
    private Vector3f fakeNormal = new Vector3f(0, 1f, 0);	
    protected float visibilityDistance;	// the maximum distance when the entity will be rendered
    protected Vector3f toCamera = new Vector3f(0, 0, 0);	// vector from position to camera

    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, float visibilityDistance) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
        this.visibilityDistance = visibilityDistance;
    }
    
    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, Vector3f fakeNormal, float visibilityDistance) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
        this.fakeNormal = fakeNormal;
        this.visibilityDistance = visibilityDistance;
    }
    
    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, float visibilityDistance, float visDistRandom) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
        this.visibilityDistance = visibilityDistance - (float) (Math.random() * visDistRandom);
    }
   
    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale, Vector3f fakeNormal, float visibilityDistance, float visDistRandom) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
        this.fakeNormal = fakeNormal;
        this.visibilityDistance = visibilityDistance - (float) (Math.random() * visDistRandom);
    }
    
    public void increasePosition(float dx, float dy, float dz) {
        position.x += dx;
        position.y += dy;
        position.z += dz;
    }
    
    public void increaseRotation(float dx, float dy, float dz) {
        rotX += dx;
        rotY += dy;
        rotZ += dz;
    }
    
    public ModelTexture getTexture() {
        return model.getTexture();
    }

    /**
     * returns model if can be seen, otherwise null
     * @param cameraPosition current camera position
     * @return model to render
     */
    public TexturedModel getModelBasedOnDistance(Vector3f cameraPosition) {
    	Vector3f.sub(cameraPosition, position, toCamera);
    	if(toCamera.length() > visibilityDistance) {
    		return null;
    	}
        return model;
    }

    public void setModel(TexturedModel model) {
        this.model = model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getRotX() {
        return rotX;
    }

    public void setRotX(float rotX) {
        this.rotX = rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public void setRotZ(float rotZ) {
        this.rotZ = rotZ;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }


    public Vector3f getFakeNormal() {
        return fakeNormal;
    }

    public void setFakeNormal(Vector3f fakeNormal) {
        this.fakeNormal = fakeNormal;
    }

	public float getVisibilityDistance() {
		return visibilityDistance;
	}

	public void setVisibilityDistance(float visibilityDistance) {
		this.visibilityDistance = visibilityDistance;
	}

	
	
    

}
