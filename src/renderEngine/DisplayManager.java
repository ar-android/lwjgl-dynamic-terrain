package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

/**
 * Display manager creates the viewport and sets basic openGL attributes like super sampling and bits for depth buffer.
 *
 */
public class DisplayManager {
    
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int FPS_CAP = 60;
    
    private static long lastFrameTime;
    private static float delta;
    private static long deltaLong;
    
    public static void createDisplay() {
        
        ContextAttribs attribs = new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
        
        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            //Display.create(new PixelFormat().withSamples(4), attribs);
            Display.create(new PixelFormat().withDepthBits(24).withSamples(4), attribs);
            Display.setTitle("Lime Engine");
            
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
        
        GL11.glViewport(0, 0, WIDTH, HEIGHT);
        lastFrameTime = getCurrentTime();      
    }
    
    /**
     * Swap the buffers and update the viewport.
     */
    public static void updateDisplay() {
        Display.sync(FPS_CAP);
        Display.setVSyncEnabled(true);
        Display.update();
        long currentFrameTime = getCurrentTime();
        deltaLong = currentFrameTime - lastFrameTime;
        delta = ((float)(deltaLong))/((float)1000);
        lastFrameTime = currentFrameTime;
        
    }
    
    /**
     * time since last frame on ticks
     * @return time since last frame on ticks
     */
    public static int getDelta() {
    	return (int)deltaLong;
    }
    
    /**
     * time since last frame in seconds
     * @return time in last frame in seconds
     */
    public static float getFrameTimeSeconds() {
        return delta;
    }
    
    public static void closeDisplay() {
        Display.destroy();
    }
    
    private static long getCurrentTime() {
        return (Sys.getTime()*1000)/Sys.getTimerResolution();
    }


}
