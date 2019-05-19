package water;

import java.util.LinkedList;
import java.util.List;

import terrain.TerrainGenerator;

/**
 * Produces water tiles based on terrain to the places where the altitude is
 * low. Runs in the beginning when the terrain is generated and does not need to
 * update in runtime.
 * 
 * @author smid
 *
 */
public class WaterTileGenerator {

	TerrainGenerator terrainGenerator;

	public WaterTileGenerator(TerrainGenerator terrainGenerator) {
		this.terrainGenerator = terrainGenerator;
	}

	public List<WaterTile> generateTiles(float waterLevelHeight) {

		List<WaterTile> list = new LinkedList<>();
		int tileSize = (int) WaterTile.TILE_SIZE;
		int repeatX = terrainGenerator.getxSize() / tileSize;
		int repeatY = terrainGenerator.getySize() / tileSize;

		// iterate over all possible tiles
		for (int i = 0; i < repeatX; i++) {
			for (int j = 0; j < repeatY; j++) {

				// iterate over all height point at that tile
				boolean noWater = true;
				for (int k = 0; k < tileSize; k++) {
					for (int l = 0; l < tileSize; l++) {
						if (terrainGenerator.getHeight(j * tileSize + l, i * tileSize + k) < waterLevelHeight) {
							noWater = false;
						}
					}
				}

				if (!noWater) {
					list.add(new WaterTile(i * tileSize + tileSize / 2, j * tileSize + tileSize / 2, waterLevelHeight));
				}
			}
		}
		return list;
	}

}
