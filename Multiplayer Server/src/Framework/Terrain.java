package Framework;

public class Terrain {
	private int[][] data;
	public final int COMPRESSION;
	public int width, height;

	public Terrain(int x, int y, int compression) {
		width = x;
		height = y;
		data = new int[x][y];
		COMPRESSION = compression;
	}

	public Terrain(int[][] data, int compression) {
		width = data.length;
		height = data[0].length;
		this.data = data;
		COMPRESSION = compression;
	}

	public int[][] get(int x, int y, int width, int height) {
		int[][] res = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (x + i < 0 || x + i >= data.length || y + j < 0 || y + j >= data.length)
					res[i][j] = 0;
				else
					res[i][j] = data[x + i][y + j];
			}
		}
		return res;
	}

	protected void change(int x, int y, int type) {
		data[x][y] = type;
	}

}
