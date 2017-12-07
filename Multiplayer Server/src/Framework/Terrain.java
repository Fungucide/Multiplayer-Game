package Framework;

import java.util.HashSet;

public class Terrain {
	private int[][] data;
	public final int COMPRESSION;
	public int width, height;
	private final HashSet<Integer> passable;

	public Terrain(int x, int y, int compression, HashSet<Integer> pass) {
		width = x;
		height = y;
		data = new int[x][y];
		COMPRESSION = compression;
		passable = pass;
	}

	public Terrain(int[][] data, int compression, HashSet<Integer> pass) {
		width = data.length;
		height = data[0].length;
		this.data = data;
		COMPRESSION = compression;
		passable = pass;
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

	public boolean isBlocked(int qx, int qy) {
		if (qx < 0 || qy < 0)
			return false;
		return !passable.contains(data[qx][qy]);
	}

}
