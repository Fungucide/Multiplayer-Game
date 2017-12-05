package Framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class World {
	private final int COMPRESSION;
	private final long UPDATE_DELAY;
	private final String[] path;
	private final int[][] type;
	private final int width, height;
	private Terrain terrain;
	private PlayerUpdate pu;

	public World(int width, int height, int c, long ud, String[] path, int[][] type) {
		this(width, height, new Terrain(width, height, c), ud, path, type);
	}

	public World(int width, int height, int[][] data, int c, long ud, String[] path, int[][] type) {
		this(width, height, new Terrain(data, c), ud, path, type);
	}

	public World(int width, int height, Terrain t, long ud, String[] path, int[][] type) {
		this.width = width;
		this.height = height;
		this.terrain = t;
		this.path = path;
		this.type = type;
		COMPRESSION = t.COMPRESSION;
		if (terrain.width != width || terrain.height != height)
			throw new IllegalArgumentException(String.format("World size [x=%d y=%d] does not match Terrain size [x=%d y=%d]", width, height, terrain.width, terrain.height));
		UPDATE_DELAY = ud;
		setPlayerUpdate(UPDATE_DELAY);
	}

	public World(String path, long ud) throws IOException {
		UPDATE_DELAY = ud;
		BufferedReader worldRead = new BufferedReader(new FileReader(new File(path)));
		width = Integer.parseInt(worldRead.readLine());
		height = Integer.parseInt(worldRead.readLine());
		COMPRESSION = Integer.parseInt(worldRead.readLine());
		int[][] data = new int[width][height];
		String[] input;
		for (int i = 0; i < height / COMPRESSION; i++) {
			input = worldRead.readLine().split(" ");
			for (int j = 0; j < width / COMPRESSION; j++) {
				data[j][i] = Integer.parseInt(input[j]);
			}
		}
		int size = Integer.parseInt(worldRead.readLine());
		this.path = new String[size];
		type = new int[size][2];
		for (int i = 0; i < size; i++) {
			String[] in = worldRead.readLine().split(" ");
			if (in.length == 2) {
				type[i][0] = Integer.parseInt(in[1]);
				type[i][1] = -1;
			} else {
				type[i][0] = Integer.parseInt(in[1]);
				type[i][1] = Integer.parseInt(in[2]);
			}
			this.path[i] = in[0];
		}
		worldRead.close();
		terrain = new Terrain(data, COMPRESSION);
		if (terrain.width != width || terrain.height != height)
			throw new IllegalArgumentException(String.format("World size [x=%d y=%d] does not match Terrain size [x=%d y=%d]", width, height, terrain.width, terrain.height));
		setPlayerUpdate(UPDATE_DELAY);
	}

	public void setPlayerUpdate(long updateDelay) {
		pu = new PlayerUpdate(updateDelay);
		Thread t = new Thread(pu);
		t.start();
	}

	public boolean addPlayer(Char c) {
		return pu.add(c);
	}

	public boolean removePlayer(Char c) {
		return pu.delete(c);
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

	public String[] getResources() {
		return path;
	}

	public int[][] getType() {
		return type;
	}

	public ArrayList<int[]> getRenderData(int tx, int ty, int bx, int by, Char character) {
		ArrayList<int[]> res = new ArrayList<int[]>();
		for (Char c : pu.players)
			if (c.getX() >= bx && c.getX() <= tx && c.getY() >= by && c.getY() <= ty && !c.equals(character))
				res.add(new int[] { c.getX(), c.getY(), c.getGraphics() });
		return res;
	}
}

class PlayerUpdate implements Runnable {

	final long UPDATE_DELAY;
	protected ArrayList<Char> players;

	public PlayerUpdate(long ud) {
		UPDATE_DELAY = ud;
		players = new ArrayList<Char>();
	}

	public boolean add(Char c) {
		return players.add(c);
	}

	public boolean delete(Char c) {
		return players.remove(c);

	}

	@Override
	public void run() {
		long time;
		while (true) {
			time = System.currentTimeMillis();
			for (Char c : players)
				c.update();
			while (System.currentTimeMillis() - time < UPDATE_DELAY);
		}
	}

}
