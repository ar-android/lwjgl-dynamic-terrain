package textures;

/**
 * Class for multiple textures used with terrain
 * @author smid
 *
 */
public class TerrainTexturePack {

	private TerrainTexture tex0;
	private TerrainTexture tex1;
	private TerrainTexture tex2;
	private TerrainTexture tex3;
	
	public TerrainTexturePack(TerrainTexture tex0, TerrainTexture tex1, TerrainTexture tex2, TerrainTexture tex3) {
		super();
		this.tex0 = tex0;
		this.tex1 = tex1;
		this.tex2 = tex2;
		this.tex3 = tex3;
	}

	public TerrainTexture getTex0() {
		return tex0;
	}

	public TerrainTexture getTex1() {
		return tex1;
	}

	public TerrainTexture getTex2() {
		return tex2;
	}

	public TerrainTexture getTex3() {
		return tex3;
	}
	
	
	
}
