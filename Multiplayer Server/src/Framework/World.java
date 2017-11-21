package Framework;

public class World {
	private final int COMPRESSION;
	private final int width, height;
	private Terrain terrain;

	public World(int width, int height, int c) {
		this.width = width;
		this.height = height;
		COMPRESSION = c;
		this.terrain = new Terrain(width, height, COMPRESSION);
	}

	public World(int width, int height, Terrain t) {
		this.width = width;
		this.height = height;
		this.terrain = t;
		COMPRESSION = t.COMPRESSION;
		if (terrain.width * COMPRESSION != width || terrain.height * COMPRESSION != height)
			throw new IllegalArgumentException(String.format("World size [x=%d y=%d] does not match Terrain size [x=%d y=%d]", width, height, terrain.width, terrain.height));
	}

	public World(int width, int height, int[][] data, int c) {
		this.width = width;
		this.height = height;
		COMPRESSION = c;
		this.terrain = new Terrain(data, COMPRESSION);
		if (terrain.width != width || terrain.height != height)
			throw new IllegalArgumentException(String.format("World size [x=%d y=%d] does not match Terrain size [x=%d y=%d]", width, height, terrain.width, terrain.height));
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int[][] getTerrain(int x, int y, int width, int height) {
		return terrain.get(x, y, width, height);
	}

	public void changeTerrain(int x, int y, int type) {
		terrain.change(x, y, type);
	}
}
