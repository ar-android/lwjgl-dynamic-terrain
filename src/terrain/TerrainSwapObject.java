package terrain;

import java.util.HashSet;
import java.util.Set;

/**
 * Class representing the terrains to swap. Used for synchronization of main
 * thread and the Terrain Manager
 * 
 * @author smid
 *
 */
public class TerrainSwapObject {

	private Set<Integer> terrainsToSwap = new HashSet<>();
	private Boolean swapTerrains = false;

	public boolean isActive() {
		synchronized (this) {
			return swapTerrains;
		}
	}

	public Set<Integer> getTerrainsToSwap() {
		synchronized (this) {

			Set<Integer> out = new HashSet<>();
			for (Integer i : terrainsToSwap) {
				out.add(i);
			}
			swapTerrains = false;
			terrainsToSwap.clear();
			return out;
		}
	}

	public void clear() {
		synchronized (this) {
			swapTerrains = false;
			terrainsToSwap.clear();
		}
	}

	public void initSwap(Set<Integer> terrainsToSwap) {
		synchronized (this) {
			terrainsToSwap.stream().forEach(i -> {
				this.terrainsToSwap.add(i);
			});
			swapTerrains = true;
		}
	}

}
