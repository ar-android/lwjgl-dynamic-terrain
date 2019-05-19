package models;

import java.util.LinkedList;
import java.util.List;

/**
 * Pack containing models to be generated at one place as one entity. In case we
 * want the entity to consist from more objects, all of them should be generated
 * with the same parameters as one entity. For example tree has leaves and
 * trunk, but both models should have the same transforms.
 *
 */
public class GeneratedModelPack {

	List<GeneratedModel> models;

	public GeneratedModelPack(GeneratedModel model) {
		this.models = new LinkedList<>();
		this.addModel(model);
	}

	public List<GeneratedModel> getModels() {
		return models;
	}

	public void setModels(List<GeneratedModel> models) {
		this.models = models;
	}

	public void addModel(GeneratedModel model) {
		this.models.add(model);
	}

	public float getDensity() {
		return models.get(0).getDensity();
	}

	public float getLow() {
		return models.get(0).getLow();
	}

	public float getHigh() {
		return models.get(0).getHigh();
	}

}
