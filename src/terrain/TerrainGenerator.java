package terrain;

import org.lwjgl.util.vector.Vector3f;

/**
 * Using noise generates and holds the terrain height map as 2D array
 * @author smid
 *
 */
public class TerrainGenerator {

	// - amp 150, heightShigt 100, billow type, smooth 1
	// - amp 200, heightShift 0, rigidMulti, smooth 7

	private int xSize = 2048;
	private int ySize = 2048;
	private float gain = 0.4f;
	private float lacunarity = 1.6f;
	private float frequency = 0.0012f;
//	private int seed = (int) (Math.random() * 1000);
	private int seed = 42;
	private float amplitude = 200;
	private float heightShift = 0;

	private float[][] heightMap = new float[xSize + 1][ySize + 1]; // 2D heightmap to create terrain

	/**
	 * create new terrain heights
	 */
	public void generateTerrainData() {

		FastNoise myNoise = new FastNoise(); // Create a FastNoise object
		myNoise.SetNoiseType(FastNoise.NoiseType.SimplexFractal); // Set the desired noise type
		myNoise.SetFractalLacunarity(lacunarity);
		myNoise.SetFractalGain(gain);
		myNoise.SetSeed(seed);
		myNoise.SetFrequency(frequency);
		myNoise.SetFractalType(FastNoise.FractalType.RigidMulti);

		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				heightMap[x][y] = myNoise.GetNoise(x, y);
			}
		}

		heightMap = filter(heightMap, smoothKernel, 7);

	}

	/**
	 * provide heights for vertices in terrain, for the once on the edge connect to the lower LOD
	 * @param x  position x
	 * @param y position z in world space
	 * @param connectFactor how many times lower is the resolution of LOD connecting to (in this scene we use 4)
	 * @return altitude of vertex
	 */
	public float getHeight(int x, int y, int connectFactor) {
		if (y % connectFactor == 0) {
			// on y
			int xLow = ((int) (x / connectFactor)) * connectFactor;
			float low = heightMap[Math.max(0, Math.min(xSize, xLow))][Math.max(0, Math.min(ySize, y))];
			float high = heightMap[Math.max(0, Math.min(xSize, xLow + connectFactor))][Math.max(0, Math.min(ySize, y))];
			float ratio = ((float) (x % connectFactor)) / (float) connectFactor;
			return (ratio * high + (1 - ratio) * low) * amplitude + heightShift;
		}
		if (x % connectFactor == 0) {
			// on x
			int yLow = ((int) (y / connectFactor)) * connectFactor;
			float low = heightMap[Math.max(0, Math.min(xSize, x))][Math.max(0, Math.min(ySize, yLow))];
			float high = heightMap[Math.max(0, Math.min(xSize, x))][Math.max(0, Math.min(ySize, yLow + connectFactor))];
			float ratio = ((float) (y % connectFactor)) / (float) connectFactor;
			return (ratio * high + (1 - ratio) * low) * amplitude + heightShift;
		}
		return heightMap[Math.max(0, Math.min(xSize, x))][Math.max(0, Math.min(ySize, y))] * amplitude + heightShift;
	}

	public float getHeight(int x, int y) {
		return heightMap[Math.max(0, Math.min(xSize, x))][Math.max(0, Math.min(ySize, y))] * amplitude + heightShift;
	}

	/**
	 * height anywhere on the surface
	 * @param x
	 * @param y
	 * @return
	 */
	public float getHeight(float x, float y) {
		int xl = (int) Math.floor(x / 8) * 8;
		int xh = (int) Math.ceil(x / 8) * 8;
		int yl = (int) Math.floor(y / 8) * 8;
		int yh = (int) Math.ceil(y / 8) * 8;

		float xr = (x - xl) / 8f;
		float yr = (y - yl) / 8f;
	
//		return rx
		if (xr > yr) {
			return (getHeight(xh, yl) * xr + getHeight(xl, yl) * (1 - xr))
					+ (getHeight(xh, yh) - getHeight(xh, yl)) * yr;
		} else {
			return (getHeight(xl, yh) * (1 - xr) + getHeight(xh, yh) * xr)
					- (getHeight(xl, yh) - getHeight(xl, yl)) * (1 - yr);
		}
	}

	/**
	 * normal of terrain vertices calculated based on neighboring vertices
	 * @param x
	 * @param y
	 * @return normal
	 */
	public Vector3f getNormal(int x, int y) {
		float hL = getHeight(x - 1, y);
		float hR = getHeight(x + 1, y);
		float hU = getHeight(x, y + 1);
		float hD = getHeight(x, y - 1);
		Vector3f normal = new Vector3f(hL - hR, 2f, hD - hU);
		normal.normalise();
		return new Vector3f(normal.z, normal.y, normal.x);
	}

	public void randomizeSeed() {
		this.seed = (int) (Math.random() * 1000);
	}

	float[][] smoothKernel = { { 0.1f, 0.1f, 0.1f }, { 0.1f, 0.2f, 0.1f }, { 0.1f, 0.1f, 0.1f } };

	/**
	 * smooth the terrain with blur
	 * @param gray source heights
	 * @param kernel to use for smooth
	 * @param iterations how many iterations to go throug
	 * @return smoothed array of height
	 */
	public static float[][] filter(float[][] gray, float[][] kernel, int iterations) {

		// gray is the image matrix, and kernel is the array I specifed above
		float current = 0.0f;
		float around = 0.0f;
		float[][] smooth = new float[gray.length][gray[0].length];
		int scale = 4;
		for (int col = 0; col < gray.length; col++) {
			for (int row = 0; row < gray[0].length; row++) {
				// first two for loops are used to do this procedure on every single pixel
				// the next two call upon the respective pixels around the one in question
				for (int i = -1; i < 2; i++) {
					for (int j = -1; j < 2; j++) {
						around = gray[Math.min(gray.length - 1, Math.max(0, i * scale + col))][Math
								.min(gray[0].length - 1, Math.max(0, j * scale + row))]; // This calls a method which
																							// checks for the
						// pixels around the one being modified
						current += around * kernel[i + 1][j + 1];
						// after the application of the filter these are then added to the new value
					}
				}
				smooth[col][row] = current;
				current = 0.0f;
				// The new value is now set into the smooth matrix
			}
		}
		if (iterations == 0) {
			return smooth;
		} else {
			return filter(smooth, kernel, iterations - 1);
		}
	}

	public int getxSize() {
		return xSize;
	}

	public int getySize() {
		return ySize;
	}

}
