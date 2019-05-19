package models;

import java.util.List;

/**
 * Class describing the template for generator to generate entities with LODs
 *
 */
public class GeneratedModelLOD extends GeneratedModel{
    
    private List<TexturedModel> models;	// array of LODs for model
    private List<Float> distances;  	// array of distances (see more at EntityLOD)

    public GeneratedModelLOD(List<TexturedModel> models, float density, float low, float high, float scaleLow, float scaleHigh,
            boolean fakeNormal, float visibilityDistance, List<Float> distances) {
        super(models.get(0), density, low, high, scaleLow, scaleHigh, fakeNormal, visibilityDistance, 0);
        
        this.models = models;
        this.distances = distances;
        
    }

    public List<TexturedModel> getModels() {
        return models;
    }

    public void setModels(List<TexturedModel> models) {
        this.models = models;
    }

    public List<Float> getDistances() {
        return distances;
    }

    public void setDistances(List<Float> distances) {
        this.distances = distances;
    }   
    
}
