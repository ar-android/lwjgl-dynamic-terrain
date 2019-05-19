package renderEngine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import textures.ModelTexture;
import utils.Maths;

import java.util.List;
import java.util.Map;

/**
 * Renderer for simple entities (foliage, trees, rocks...)
 *
 */
public class EntityRenderer {
    
    private StaticShader shader;
    int noiseId;
    private final String noiseTexturePath = "noiseSquare";
   
    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix, Loader loader) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix); 
        //load noise texture  - used for dissolve appear/disappear
        noiseId = loader.loadTexture(noiseTexturePath);
        GL13.glActiveTexture(GL13.GL_TEXTURE5);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, noiseId);
        shader.connectTextureUnits();
        shader.stop();
    }
 
    /**
     * renders entities from textureModel-entity map
     * @param entities
     */
    public void render(Map<TexturedModel, List<Entity>> entities) {
        entities.keySet().stream().forEach(model -> {
            prepareTexturedModels(model);
            entities.get(model).stream().forEach(entity -> {
                prepareInstance(entity, model);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            });
            unbindTexturedModel();
        });
    }
    
    /**
     * Sets up textureModel to be rendered
     * @param model textureModel to render
     */
    private void prepareTexturedModels(TexturedModel model) {
        RawModel rawModel = model.getRawModel();

        GL30.glBindVertexArray(rawModel.getVaoId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        
        ModelTexture modelTex = model.getTexture();
        if(modelTex.isHasTransparency()) {
            MasterRenderer.disableCulling();
        }
      
        shader.loadShineVariables(modelTex.getShineDumper(), modelTex.getReflectivity());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureId());        
    }
    
    private void unbindTexturedModel() {
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1); 
        GL20.glEnableVertexAttribArray(2); 
        GL30.glBindVertexArray(0);
    }
    
    /**
     * Prepares a single instance for rendering
     * @param entity the entity to render
     * @param model model of the entity
     */
    private void prepareInstance(Entity entity, TexturedModel model) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(),
                entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        shader.loadFakeLightingVariable(model.getTexture().getUseFakeLight(), entity.getFakeNormal(), model.getTexture().getAmbient());
        shader.loadVisibilityDistance(entity.getVisibilityDistance());
    }
    


}
