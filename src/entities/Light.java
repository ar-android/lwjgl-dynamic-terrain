package entities;

import org.lwjgl.util.vector.Vector3f;

/**
 * very simple light representation holding position and color of a light source
 *
 */
public class Light {

    Vector3f position;
    Vector3f color;
    
    public Light(Vector3f position, Vector3f color) {
        super();
        this.position = position;
        this.color = color;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }
    
    
    
    
}
