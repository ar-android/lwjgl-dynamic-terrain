package randomObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.EntityLOD;
import models.GeneratedModel;
import models.GeneratedModelLOD;
import models.GeneratedModelPack;
import renderEngineTest.MainGameLoop;
import terrain.TerrainGenerator;

/**
 * Class that runs in separate thread and produces the random generated entities
 * to the scene based on camera position. The word is divided into tiles of
 * certain size. These tiles are evaluated separately. If camera moves close to
 * tile, SpawnManager will generate models on this tile and pass them to be
 * rendered.
 * 
 * @author smid
 *
 */
public class SpawnManager implements Runnable {

	int tileSize;	// size of the tile for generated objects
	int maxSize;	// maximum size of the word where tiles are evaluated
	private float loadDistSqr;	// maximum distance from camera when the tile generates objects squared
	Camera camera;	// the camera object
	boolean quit = false;	// flag if the game has ended
	Set<Tile> activeTiles = new HashSet<>();	//set of tiles that are already generated and being rendered
	Map<Tile, List<Entity>> activeEntities = new HashMap<>();	// map of all tiles and their already generated entities
	List<GeneratedModelPack> modelsToGenerate = new ArrayList<>();	// templates to use for model generation
	TerrainGenerator terrainGenerator;	// terrain generator, used for height
	float visibilityInTiles = 2.6f;		// max visibility based on tile size -> produces loadDistSqr
	float heighBlend = 10;		// blending object generation probability based on altitude
	int distanceType;		// small, middle, far -> distances for foliage, rocks and trees
	Random random = new Random();

	public SpawnManager(int tileSize, int maxSize, Camera camera, TerrainGenerator terrainGenerator, int distanceType) {
		this.tileSize = tileSize;
		this.maxSize = maxSize;
		this.camera = camera;
		this.loadDistSqr = (float) Math.pow((tileSize * visibilityInTiles), 2);
		this.terrainGenerator = terrainGenerator;
		this.distanceType = distanceType;
	}

	@Override
	public void run() {

		Set<Tile> newActiveTiles = new HashSet<>();
		Set<Tile> addTiles = new HashSet<>();		//new tiles to be generated
		Set<Tile> removeTiles = new HashSet<>();	// old tile, that are too far and entities should be discarded
		while (!quit) {

			// generate new tiles here
			newActiveTiles.clear();
			addTiles.clear();
			removeTiles.clear();

			for (int i = 0; i < maxSize / tileSize; i++) {
				for (int j = 0; j < maxSize / tileSize; j++) {

					int tileCenterX = (int) ((i + 0.5) * tileSize);
					int tileCenterZ = (int) ((j + 0.5) * tileSize);

					if (Math.pow(Math.abs((camera.getPosition().getX() - tileCenterX)), 2)
							+ Math.pow(Math.abs((camera.getPosition().getZ() - tileCenterZ)), 2) < loadDistSqr) {

						newActiveTiles.add(new Tile(i, j));
					}
				}
			}

			// compare to existing set
			for (Tile tile : newActiveTiles) {
				if (!activeTiles.contains(tile)) {
					addTiles.add(tile);
				}
			}
			;
			for (Tile tile : activeTiles) {
				if (!newActiveTiles.contains(tile)) {
					removeTiles.add(tile);
				}
			}
			;

			// remove old objects from map
			for (Tile tile : removeTiles) {
				activeTiles.remove(tile);
				activeEntities.remove(tile);
			}
			;

			// generate and add new entities
			for (Tile tile : addTiles) {
				activeTiles.add(tile);
				List<Entity> tileEntities = new LinkedList<>();
				modelsToGenerate.stream().forEach(modelPack -> {
					tileEntities.addAll(generate(modelPack, tile.x, tile.y, (int) tileSize));
				});
				activeEntities.put(tile, tileEntities);
			}
			;

			// update
			if ((!addTiles.isEmpty()) || (!removeTiles.isEmpty())) {
				List<Entity> allEntities = new LinkedList<>();
				activeEntities.values().stream().forEach(tileList -> {
					allEntities.addAll(tileList);
				});

				publish(allEntities);

			}

		}
	}

	/**
	 * Produces list of new entities on tile
	 * @param modelPack	which model to use
	 * @param x	x coordinate of the tile(in tiles, not meters)
	 * @param z z coordinate of the tile (in tiles, not meters)
	 * @param tileSize	size of tile in meters
	 * @return	list of new randomly generated entities
	 */
	private List<Entity> generate(GeneratedModelPack modelPack, int x, int z, int tileSize) {

		int totalRuns = (int) (tileSize * tileSize * modelPack.getDensity());
		List<Entity> entities = new LinkedList<>();

		for (int i = 0; i < totalRuns; i++) {

			float randomX = (float) ((random.nextFloat() + x) * tileSize);
			float randomZ = (float) ((random.nextFloat() + z) * tileSize);
			float randomScale = random.nextFloat();
			float height = terrainGenerator.getHeight(randomZ, randomX);
			// valid height
			if (height < modelPack.getHigh() && height > modelPack.getLow()) {
				prepareEntity(modelPack, entities, randomX, randomZ, height, randomScale);
			} // low blend
			else if (height < modelPack.getLow()) {
				if (random.nextFloat() * heighBlend + height > modelPack.getLow()) {
					prepareEntity(modelPack, entities, randomX, randomZ, height, randomScale);
				}
			} // high blend
			else if (height > modelPack.getHigh()) {
				if (height - random.nextFloat() * heighBlend < modelPack.getHigh()) {
					prepareEntity(modelPack, entities, randomX, randomZ, height, randomScale);
				}
			}
		}

		return entities;
	}

	/**
	 * Generate a single entity on certain position
	 * @param modelPack model to produce
	 * @param entities	list where the entity should be added
	 * @param randomX	x coordinate
	 * @param randomZ	z coordinate
	 * @param height	altitude of the entity
	 * @param randomScale	scale of the entity
	 */
	private void prepareEntity(GeneratedModelPack modelPack, List<Entity> entities, float randomX, float randomZ,
			float height, float randomScale) {
		for (GeneratedModel model : modelPack.getModels()) {
			if (model instanceof GeneratedModelLOD) {
				prepareLODEntity((GeneratedModelLOD) model, entities, randomX, randomZ, height, randomScale);
			} else {
				prepareSimpleEntity(model, entities, randomX, randomZ, height, randomScale);
			}
		}
	}

	/**
	 * Produces a simple entity with no LODs
	 */
	private void prepareSimpleEntity(GeneratedModel model, List<Entity> entities, float randomX, float randomZ,
			float height, float randomScale) {
		Entity entity = new Entity(model.getModel(), new Vector3f(randomX, height, randomZ), random.nextFloat() * 10,
				(random.nextFloat() * 360), (random.nextFloat() * 10),
				(randomScale * (model.getScaleHigh() - model.getScaleLow())) + model.getScaleLow(),
				model.getVisibilityDistance(), model.getVisDistRandom());

		if (model.isFakeNormal()) {
			entity.setFakeNormal(terrainGenerator.getNormal((int) randomZ, (int) randomX));
		}

		entities.add(entity);
	}

	/**
	 * Produces entity which has LODs
	 */
	private void prepareLODEntity(GeneratedModelLOD model, List<Entity> entities, float randomX, float randomZ,
			float height, float randomScale) {
		Entity entity = new EntityLOD(model.getModels(), new Vector3f(randomX, height, randomZ), random.nextFloat() * 1,
				0, (random.nextFloat() * 1),
				(randomScale * (model.getScaleHigh() - model.getScaleLow())) + model.getScaleLow(),
				model.getVisibilityDistance(), model.getDistances());

		if (model.isFakeNormal()) {
			entity.setFakeNormal(terrainGenerator.getNormal((int) randomZ, (int) randomX));
		}

		entities.add(entity);
	}

	/**
	 * Push new entity list to the scene
	 * @param allEntities	new list to be rendered
	 */
	private void publish(List<Entity> allEntities) {
		if (distanceType == 0) {
			MainGameLoop.setEntitiesClose(allEntities);
			return;
		}
		if (distanceType == 1) {
			MainGameLoop.setEntitiesMiddle(allEntities);
			return;
		}
		if (distanceType == 2) {
			MainGameLoop.setEntitiesFar(allEntities);
			return;
		}
	}

	/**
	 * Adds new GeneratedModel template to be produced
	 * @param model new model
	 */
	public void addModelToGenerate(GeneratedModelPack model) {
		modelsToGenerate.add(model);
	}

	/**
	 * the game has ended, kill the thread
	 */
	public void quit() {
		this.quit = true;
	}

	/**
	 * Simple description of the tile
	 */
	class Tile {

		// x, y are tiles coordinates in the tilegrid, not in wordspace
		int x;
		int y;

		public Tile(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (!this.getClass().equals(obj.getClass())) {
				return false;
			}
			Tile other = (Tile) obj;

			return other.getX() == getX() && other.getY() == getY();

		}

		@Override
		public int hashCode() {
			return 3 * x + 7 * y;
		}

	}

}
