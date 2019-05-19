package renderEngineTest;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import controls.CameraController;
import controls.FreeFlySmooth;
import controls.WalkController;
import entities.Camera;
import entities.Entity;
import entities.Light;
//import guis.GuiRenderer;
//import guis.GuiTexture;
import models.GeneratedModel;
import models.GeneratedModelLOD;
import models.GeneratedModelPack;
import models.RawModel;
import models.TexturedModel;
import randomObjects.SpawnManager;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrain.Terrain;
import terrain.TerrainGenerator;
import terrain.TerrainManager;
import terrain.TerrainSwapObject;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;
import water.WaterTileGenerator;

/**
 * Class that produces the playable scene.
 * Set's up display, camera controllers, entity generators, terrain, lights.
 * Runs the main game loop.
 *
 */
public class MainGameLoop {

    private static List<Entity> entitiesClose;	// foliage entities
    private static List<Entity> entitiesMiddle;	// rocks
    private static List<Entity> entitiesFar;	// trees
    public static boolean walk = false;			// is camera in walk mode

    public static void main(String[] args) {
    	
    	// setup basic classes needed for the scene
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        entitiesClose = new ArrayList<>();
        entitiesMiddle = new ArrayList<>();
        entitiesFar = new ArrayList<>();
        List<Terrain> terrains = new ArrayList<>();
        TerrainGenerator terrainGenerator = new TerrainGenerator();
        TerrainSwapObject tso = new TerrainSwapObject();
        
        // init terrain vertices
        terrainGenerator.generateTerrainData();
        
        // set the sun very far away, point light will act like directional light
        Light light = new Light(new Vector3f(8000f, 10000.0f, -2000f), new Vector3f(1, 1, 1));
        
        // init camera and movement controllers
        Camera camera = new Camera();
        CameraController freeFlyController = new FreeFlySmooth();
        CameraController walkController = new WalkController(terrainGenerator, (FreeFlySmooth)freeFlyController);
        
        MasterRenderer renderer = new MasterRenderer(loader);

        // ************TERRAIN SETUP***********

        // load textures
        TerrainTexture tex0 = new TerrainTexture(loader.loadTexture("sand"));
        TerrainTexture tex1 = new TerrainTexture(loader.loadTexture("ground1"));
        TerrainTexture tex2 = new TerrainTexture(loader.loadTexture("ground2"));
        TerrainTexture tex3 = new TerrainTexture(loader.loadTexture("snow"));
        TerrainTexturePack texturePack = new TerrainTexturePack(tex0, tex1, tex2, tex3);
        
        // init two LODs for terrain
        terrains.add(new Terrain(0, 0, 2048, loader, texturePack, terrainGenerator));
        terrains.add(new Terrain(0, 0, 512, loader, texturePack, terrainGenerator));

        // init terrain generating thread
        TerrainManager terrainManager = new TerrainManager(terrains, camera, tso);
        Thread terrainManagerThread = new Thread(terrainManager);
        terrainManagerThread.start();

        // ************GENERATED ENTITIES SETUP***********
        
        // init spawn managers for different distance sets
        SpawnManager spawnManagerClose = new SpawnManager(32, 2048, camera, terrainGenerator, 0);
        SpawnManager spawnManagerMiddle = new SpawnManager(64, 2048, camera, terrainGenerator, 1);
        SpawnManager spawnManagerFar = new SpawnManager(512, 2048, camera, terrainGenerator, 2);

        // grass for lower altitude
        RawModel modelGrass1 = OBJLoader.loadObjModel("grass", loader);
        ModelTexture textureGrass1 = new ModelTexture(loader.loadTexture("atlas1"), true, 1);
        TexturedModel texturedModelGrass1 = new TexturedModel(modelGrass1, textureGrass1);
        GeneratedModel generatedModelGrass1 = new GeneratedModel(texturedModelGrass1, 0.7f, 52, 70, 0.3f, 0.8f, true,
                65, 25);

        RawModel modelGrass2 = OBJLoader.loadObjModel("grass2", loader);
        ModelTexture textureGrass2 = new ModelTexture(loader.loadTexture("atlas1"), true, 1);
        TexturedModel texturedModelGrass2 = new TexturedModel(modelGrass2, textureGrass2);
        GeneratedModel generatedModelGrass2 = new GeneratedModel(texturedModelGrass2, 0.3f, 52, 70, 0.3f, 0.8f, true,
                65, 35);

        RawModel modelGrass3 = OBJLoader.loadObjModel("grass3", loader);
        ModelTexture textureGrass3 = new ModelTexture(loader.loadTexture("atlas1"), true, 1);
        TexturedModel texturedModelGrass3 = new TexturedModel(modelGrass3, textureGrass3);
        GeneratedModel generatedModelGrass3 = new GeneratedModel(texturedModelGrass3, 0.3f, 52, 70, 0.3f, 0.8f, true,
                65, 35);

        RawModel modelGrass4 = OBJLoader.loadObjModel("grass4", loader);
        ModelTexture textureGrass4 = new ModelTexture(loader.loadTexture("atlas1"), true, 1);
        TexturedModel texturedModelGrass4 = new TexturedModel(modelGrass4, textureGrass4);
        GeneratedModel generatedModelGrass4 = new GeneratedModel(texturedModelGrass4, 0.3f, 52, 70, 0.3f, 0.8f, true,
                65, 35);

        RawModel modelGrass5 = OBJLoader.loadObjModel("grass5", loader);
        ModelTexture textureGrass5 = new ModelTexture(loader.loadTexture("atlas1"), true, 1);
        TexturedModel texturedModelGrass5 = new TexturedModel(modelGrass5, textureGrass5);
        GeneratedModel generatedModelGrass5 = new GeneratedModel(texturedModelGrass5, 0.3f, 52, 70, 0.3f, 0.8f, true,
                65, 35);
        
        spawnManagerClose.addModelToGenerate(new GeneratedModelPack(generatedModelGrass1));
        spawnManagerClose.addModelToGenerate(new GeneratedModelPack(generatedModelGrass2));
        spawnManagerClose.addModelToGenerate(new GeneratedModelPack(generatedModelGrass3));
        spawnManagerClose.addModelToGenerate(new GeneratedModelPack(generatedModelGrass4));
        spawnManagerClose.addModelToGenerate(new GeneratedModelPack(generatedModelGrass5));    
        
     // grass for higher altitude
        RawModel modelGrass6 = OBJLoader.loadObjModel("grass6", loader);
        ModelTexture textureGrass6 = new ModelTexture(loader.loadTexture("atlas2"), true, 0.45f);
        TexturedModel texturedModelGrass6 = new TexturedModel(modelGrass6, textureGrass6);
        GeneratedModel generatedModelGrass6 = new GeneratedModel(texturedModelGrass6, 0.5f, 75, 120, 0.5f, 1.2f, true,
                65, 35);
        RawModel modelGrass7 = OBJLoader.loadObjModel("grass7", loader);
        ModelTexture textureGrass7 = new ModelTexture(loader.loadTexture("atlas2"), true, 1f);
        TexturedModel texturedModelGrass7= new TexturedModel(modelGrass7, textureGrass7);
        GeneratedModel generatedModelGrass7 = new GeneratedModel(texturedModelGrass7, 0.2f, 75, 120, 0.6f, 0.9f, true,
                65, 35);
        RawModel modelGrass8 = OBJLoader.loadObjModel("grass8", loader);
        ModelTexture textureGrass8= new ModelTexture(loader.loadTexture("atlas2"), true, 1f);
        TexturedModel texturedModelGrass8 = new TexturedModel(modelGrass8, textureGrass8);
        GeneratedModel generatedModelGrass8 = new GeneratedModel(texturedModelGrass8, 0.8f, 75, 120, 0.8f, 1.3f, true,
                65, 35);
        RawModel modelGrass9 = OBJLoader.loadObjModel("grass9", loader);
        ModelTexture textureGrass9 = new ModelTexture(loader.loadTexture("atlas2"), true, 1f);
        TexturedModel texturedModelGrass9= new TexturedModel(modelGrass9, textureGrass9);
        GeneratedModel generatedModelGrass9= new GeneratedModel(texturedModelGrass9, 0.2f, 75, 120, 0.6f, 0.9f, true,
                65, 35);
        
        
        spawnManagerClose.addModelToGenerate(new GeneratedModelPack(generatedModelGrass6));
        spawnManagerClose.addModelToGenerate(new GeneratedModelPack(generatedModelGrass7));
        spawnManagerClose.addModelToGenerate(new GeneratedModelPack(generatedModelGrass8));
        spawnManagerClose.addModelToGenerate(new GeneratedModelPack(generatedModelGrass9));
        

        // rocks
        RawModel modelRock = OBJLoader.loadObjModel("rock2", loader);
        ModelTexture textureRock = new ModelTexture(loader.loadTexture("rock"), false, 0.3f);
        ;
        TexturedModel texturedModelRock = new TexturedModel(modelRock, textureRock);
        GeneratedModel generatedModelRock = new GeneratedModel(texturedModelRock, 0.015f, 0, 120, 0.05f, 0.5f, false,
                150, 40);

        spawnManagerMiddle.addModelToGenerate(new GeneratedModelPack(generatedModelRock));
        
        // ************TREES SETUP***********

        // init the oak trees
        List<TexturedModel> leavesLODs = new ArrayList<>();
        ModelTexture leavesTex = new ModelTexture(loader.loadTexture("branch"), true, 0.5f);
        ModelTexture spriteTex = new ModelTexture(loader.loadTexture("tree1LOD3"), false, 1f);
        spriteTex.setAmbient(0.3f);
        leavesLODs.add(new TexturedModel(OBJLoader.loadObjModel("tree1L2", loader), spriteTex));
        leavesLODs.add(new TexturedModel(OBJLoader.loadObjModel("tree1leavesL1", loader), leavesTex));
        leavesLODs.add(new TexturedModel(OBJLoader.loadObjModel("tree1leavesL0", loader), leavesTex));
        List<Float> LODdistances = new ArrayList<>();
        LODdistances.add(0.2f);
        LODdistances.add(0.075f);
        
        GeneratedModelPack treeModelPack = new GeneratedModelPack(new GeneratedModelLOD(leavesLODs, 0.002f, 52, 70, 0.6f, 1.0f, true, 500, LODdistances));

        List<TexturedModel> trunkLODs = new ArrayList<>();
        ModelTexture trunkTex = new ModelTexture(loader.loadTexture("bark2"), false, 0.4f);
        trunkLODs.add(new TexturedModel(OBJLoader.loadObjModel("empty", loader), spriteTex));
        trunkLODs.add(new TexturedModel(OBJLoader.loadObjModel("tree1trunkL1", loader), trunkTex));
        trunkLODs.add(new TexturedModel(OBJLoader.loadObjModel("tree1trunkL0", loader), trunkTex));
        
        treeModelPack.addModel(new GeneratedModelLOD(trunkLODs, 0.002f, 52, 70, 0.6f, 1.0f, true, 500, LODdistances));
        
        spawnManagerFar.addModelToGenerate(treeModelPack);
        
        // pine trees    
        List<TexturedModel>  pineleavesLODs = new ArrayList<>();
        ModelTexture pineleavesTex = new ModelTexture(loader.loadTexture("pinebranch"), true, 0.5f);
        ModelTexture pinespriteTex = new ModelTexture(loader.loadTexture("pineSprite"), false, 1f);
        pinespriteTex.setAmbient(0.3f);
        pineleavesLODs.add(new TexturedModel(OBJLoader.loadObjModel("pineSprite", loader), pinespriteTex));
        pineleavesLODs.add(new TexturedModel(OBJLoader.loadObjModel("pineleavesL1", loader), pineleavesTex));
        pineleavesLODs.add(new TexturedModel(OBJLoader.loadObjModel("pineleavesL0", loader), pineleavesTex));
        List<Float> pineLODdistances = new ArrayList<>();
        pineLODdistances.add(0.2f);
        pineLODdistances.add(0.075f);
        
        GeneratedModelPack pineModelPack = new GeneratedModelPack(new GeneratedModelLOD(pineleavesLODs, 0.002f, 75, 120, 1.0f, 2f, true, 500, LODdistances));
        
        List<TexturedModel> pinetrunkLODs = new ArrayList<>();
        ModelTexture pinetrunkTex = new ModelTexture(loader.loadTexture("bark3"), false, 0.4f);
        pinetrunkLODs.add(new TexturedModel(OBJLoader.loadObjModel("empty", loader), spriteTex));
        pinetrunkLODs.add(new TexturedModel(OBJLoader.loadObjModel("pinetrunkL1", loader), pinetrunkTex));
        pinetrunkLODs.add(new TexturedModel(OBJLoader.loadObjModel("pinetrunkL0", loader), pinetrunkTex));
        
        pineModelPack.addModel(new GeneratedModelLOD(pinetrunkLODs, 0.002f, 52, 70, 0.6f, 1.0f, true, 500, LODdistances));    
        
        spawnManagerFar.addModelToGenerate(pineModelPack);
        
        // run the threads with entity generators
        Thread spawnManagerCloseThread = new Thread(spawnManagerClose);
        Thread spawnManagerMiddleThread = new Thread(spawnManagerMiddle);
        Thread spawnManagerFarThread = new Thread(spawnManagerFar);
        spawnManagerCloseThread.start();
        spawnManagerMiddleThread.start();
        spawnManagerFarThread.start();

        // ************WATER SETUP***********

        float waterHeight = 35;
        WaterShader waterShader = new WaterShader();
        WaterFrameBuffers fbos = new WaterFrameBuffers();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
        WaterTileGenerator wtg = new WaterTileGenerator(terrainGenerator);
        List<WaterTile> waters = wtg.generateTiles(waterHeight);

        // ************GUI SETUP***********

        //List<GuiTexture> guis = new ArrayList<>();
        // no guis needed for this demo
//        GuiTexture gui = new GuiTexture(fbos.getReflectionTexture(), new Vector2f(0.5f, 0.5f),  new Vector2f(0.25f, 0.25f));
//        GuiTexture gui2 = new GuiTexture(fbos.getRefractionTexture(), new Vector2f(-0.5f, 0.5f),  new Vector2f(0.25f, 0.25f));                
//        guis.add(gui);
//        guis.add(gui2);
        //GuiRenderer guiRenderer = new GuiRenderer(loader);

        // ************MAIN GAME LOOP***********

        while (!Display.isCloseRequested()) {

            // game logic	-	camera movement
        	if(walk) {
        		camera.move(walkController, DisplayManager.getDelta());
        	}else {
        		camera.move(freeFlyController, DisplayManager.getDelta());
        	}
        
            // if new loaded terrain, swap vbos
            if (tso.isActive()) {
                for (Integer i : tso.getTerrainsToSwap()) {
                    terrains.get(i).swap();
                }
            }

            // rendering water reflection
            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
            fbos.bindReflectionFrameBuffer();
            float cameraAboveWaterDistance = 2 * (camera.getPosition().y - waterHeight);
            camera.getPosition().y -= cameraAboveWaterDistance;
            camera.invertPitch();
            renderer.renderScene(light, camera, terrains, entitiesClose, entitiesMiddle, entitiesFar,
                    new Vector4f(0, 1, 0, -waterHeight + 2));
            camera.getPosition().y += cameraAboveWaterDistance;
            camera.invertPitch();
            // refraction
            fbos.bindRefractionFrameBuffer();
            renderer.renderScene(light, camera, terrains, entitiesClose, entitiesMiddle, entitiesFar,
                    new Vector4f(0, -1, 0, waterHeight));
            fbos.unbindCurrentFrameBuffer();

            // render entities, terrain, lastly water
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            renderer.renderScene(light, camera, terrains, entitiesClose, entitiesMiddle, entitiesFar,
                    new Vector4f(0, 1, 0, 100000));
            waterRenderer.render(waters, camera, light);

            // render GUIs
            //guiRenderer.render(guis);

            // update
            DisplayManager.updateDisplay();

        }
        // clean up, stop threads
       //guiRenderer.cleanUp();
        fbos.cleanUp();
        waterShader.cleanUp();
        terrainManager.quit();
        spawnManagerClose.quit();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
        System.exit(0);
    }

    /**
     * Set new list of generated entities to be rendered - foliage
     * @param newEntities
     */
    public static void setEntitiesClose(List<Entity> newEntities) {
        entitiesClose = newEntities;
    }

    /**
     * Set new list of generated entities to be rendered - rocks
     * @param newEntities
     */
    public static void setEntitiesMiddle(List<Entity> newEntities) {
        entitiesMiddle = newEntities;
    }

    /**
     * Set new list of generated entities to be rendered - trees
     * @param newEntities
     */
    public static void setEntitiesFar(List<Entity> newEntities) {
        entitiesFar = newEntities;
    }

}
