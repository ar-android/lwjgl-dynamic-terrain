package terrain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.lwjgl.util.vector.Vector3f;
import entities.Camera;

/**
 * Class responsible for regenerating the terrain detail based on camera position.
 * Runs in separete thread, when needed generates the vertices to update terrain and initiates swap.
 * 
 * @author smid
 *
 */
public class TerrainManager implements Runnable{
    
	// terrains to manage
    private List<Terrain> terrainList;
    // positions of the terrains in wordspace
    private int[][] currentLocations;
    boolean quit = false;
    Camera camera;
    TerrainSwapObject tso;

    
    public TerrainManager(List<Terrain> terrainList, Camera camera,TerrainSwapObject tso) {
        this.terrainList = terrainList;
        this.camera = camera;
        currentLocations = new int[terrainList.size()][2];
        this.tso = tso;
    }    

    @Override
    public void run() {

        Vector3f position;
        int ratio;
        int tileX;
        int tileZ;
        int halfSquareInTiles;
        Set<Integer> toSwap = new HashSet<>();
        while(!quit) {        
            position = camera.getPosition();
            //apply to all, not to the smallest terrain
            for (int i = 0; i < terrainList.size(); i++) {   
                
                if(i == 0) {
                    ratio = (int) (terrainList.get(i).getSize()/(Terrain.VERTEX_COUNT -1));               
                    tileX = (int) Math.floor(position.x/ratio);
                    tileZ = (int) Math.floor(position.z/ratio); 
                    
                    if(currentLocations[i][0] != tileX || currentLocations[i][1] != tileZ) {
                        // camera moved enough to update the terrain
                        currentLocations[i][0] = tileX;
                        currentLocations[i][1] = tileZ;
                        halfSquareInTiles = (Terrain.VERTEX_COUNT - 1)/Terrain.LOD_SCALE/2;
                        //regenerate big one
                        terrainList.get(i).regenerate(tileX - halfSquareInTiles, tileZ - halfSquareInTiles);
                        //move small one  
                        terrainList.get(i + 1).regenerate(128, 128, (tileX - halfSquareInTiles) * ratio, (tileZ - halfSquareInTiles) * ratio, true);
                        
                        toSwap.add(i);  
                        toSwap.add(i + 1);
                    }                      
                }else if(i == 1) {
                    
                    
                    
                }else if(i == 2) {
                    
                }                   
            } 
            
            tso.initSwap(toSwap);
            toSwap.clear();
        }
    }
    
    public void quit() {
        quit = true;
    }


}
