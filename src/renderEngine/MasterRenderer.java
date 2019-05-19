package renderEngine;

import shaders.StaticShader;
import shaders.TerrainShader;
import skybox.SkyboxRenderer;
import terrain.Terrain;
import models.TexturedModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import entities.Camera;
import entities.Entity;
import entities.Light;;

/**
 * Master renderer class optimizes the rendering of many entities, renders the
 * static, terrain and sky
 *
 */
public class MasterRenderer {

	private static float FOV = 80; // camera field of view
	private static float NEAR_PLANE = 0.1f;
	private static float FAR_PLANE = 4000f;

	private Matrix4f projectionMatrix;

	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;

	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();

	private SkyboxRenderer skyboxRenderer;

	private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
	private List<Terrain> terrains = new ArrayList<>();

	public MasterRenderer(Loader loader) {
		enableCulling();
		initProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix, loader);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	/**
	 * Renders the whole scene, first processes entities to divide them based on
	 * their TextureModels, then render
	 * 
	 * @param light
	 * @param camera
	 * @param terrains
	 * @param entitiesClose
	 * @param entitiesMiddle
	 * @param entitiesFar
	 * @param clipPlane
	 */
	public void renderScene(Light light, Camera camera, List<Terrain> terrains, List<Entity> entitiesClose,
			List<Entity> entitiesMiddle, List<Entity> entitiesFar, Vector4f clipPlane) {

		// preparation, optimization
		entitiesClose.stream().forEach(e -> {
			processEntity(e, camera);
		});
		entitiesMiddle.stream().forEach(e -> {
			processEntity(e, camera);
		});

		entitiesFar.stream().forEach(e -> {
			processEntity(e, camera);
		});

		terrains.stream().forEach(t -> {
			processTerrain(t);
		});
		// rendering
		render(light, camera, clipPlane);
	}

	/**
	 * Main rendering method, prepares shaders and calls the renderers to draw
	 * objects
	 * 
	 * @param sun
	 * @param camera
	 * @param clipPlane
	 */
	public void render(Light sun, Camera camera, Vector4f clipPlane) {

		prepare();
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadLight(sun);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();

		terrainShader.start();
		terrainShader.loadClipPLane(clipPlane);
		terrainShader.loadLight(sun);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();

		skyboxRenderer.render(camera);

		entities.clear();
		terrains.clear();
	}

	public void processTerrain(Terrain terrain) {
		terrains.add(terrain);
	}

	/**
	 * Prepares entity to be rendered, based on the TextureModel adds entity to the
	 * map. Also checks the distance from camera if the entity is visible and picks
	 * the right LOD.
	 * 
	 * @param entity entity to render
	 * @param camera active camera object
	 */
	public void processEntity(Entity entity, Camera camera) {
		TexturedModel model = entity.getModelBasedOnDistance(camera.getPosition());
		if (model == null) {
			// System.out.println("dont display");
			return;
		}
		List<Entity> batch = entities.get(model);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<>();
			newBatch.add(entity);
			entities.put(model, newBatch);
		}
	}

	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(0.0f, 0.0f, 1.0f, 1f);
	}

	public void cleanUp() {
		shader.cleanUp();
		terrainShader.cleanUp();
		skyboxRenderer.cleanUp();
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	/**
	 * creates the projection matrix based on camera parameters
	 */
	private void initProjectionMatrix() {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float yScale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float xScale = yScale / aspectRatio;
		float length = FAR_PLANE - NEAR_PLANE;
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = xScale;
		projectionMatrix.m11 = yScale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / length);
		projectionMatrix.m33 = 0;
	}

}
