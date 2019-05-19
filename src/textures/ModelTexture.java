package textures;

/**
 * Class holding the texture and material data
 * @author smid
 *
 */
public class ModelTexture {
    
    private int textureId;
    private float shineDumper = 1.0f;
    private float reflectivity = 0.001f;
    private boolean hasTransparency = false;
    private float useFakeLight = 0.0f;	// how much of fake normal to use (ratio between real and fake normals)
    private float ambient = 0.3f;

    

    public ModelTexture(int textureId) {
        super();
        this.textureId = textureId;
    }
    
    public ModelTexture(int textureId, float shineDumper, float reflectivity, boolean hasTransparency, float useFakeLigh) {
        super();
        this.textureId = textureId;
        this.shineDumper = shineDumper;
        this.reflectivity = reflectivity;
        this.hasTransparency = hasTransparency;
        this.useFakeLight = useFakeLight;
    }
    
    public ModelTexture(int textureId, boolean hasTransparency, float useFakeLight) {
        super();
        this.textureId = textureId;
        this.hasTransparency = hasTransparency;
        this.useFakeLight = useFakeLight;
    }
    
    public int getTextureId() {
        return textureId;
    }

    public float getShineDumper() {
        return shineDumper;
    }

    public void setShineDumper(float shineDumper) {
        this.shineDumper = shineDumper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public boolean isHasTransparency() {
        return hasTransparency;
    }

    public void setHasTransparency(boolean hasTransparency) {
        this.hasTransparency = hasTransparency;
    }

    public float getUseFakeLight() {
        return useFakeLight;
    }

    public void setUseFakeLight(float useFakeLight) {
        this.useFakeLight = useFakeLight;
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    public float getAmbient() {
        return ambient;
    }

    public void setAmbient(float ambient) {
        this.ambient = ambient;
    } 
    
    
    
}
