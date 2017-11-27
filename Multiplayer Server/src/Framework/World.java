package Framework;

import java.util.ArrayList;

public class World {
	private final int COMPRESSION;
	private final long UPDATE_DELAY;
	private final int width, height;
	private Terrain terrain;
	private PlayerUpdate pu;

	public World(int width, int height, int c, long ud) {
		this.width = width;
		this.height = height;
		COMPRESSION = c;
		this.terrain = new Terrain(width, height, COMPRESSION);
		UPDATE_DELAY = ud;
		setPlayerUpdate(UPDATE_DELAY);
	}

	public World(int width, int height, Terrain t, long ud) {
		this.width = width;
		this.height = height;
		this.terrain = t;
		COMPRESSION = t.COMPRESSION;
		if (terrain.width * COMPRESSION != width || terrain.height * COMPRESSION != height)
			throw new IllegalArgumentException(String.format("World size [x=%d y=%d] does not match Terrain size [x=%d y=%d]", width, height, terrain.width, terrain.height));
		UPDATE_DELAY = ud;
		setPlayerUpdate(UPDATE_DELAY);
	}

	public World(int width, int height, int[][] data, int c, long ud) {
		this.width = width;
		this.height = height;
		COMPRESSION = c;
		this.terrain = new Terrain(data, COMPRESSION);
		if (terrain.width != width || terrain.height != height)
			throw new IllegalArgumentException(String.format("World size [x=%d y=%d] does not match Terrain size [x=%d y=%d]", width, height, terrain.width, terrain.height));
		UPDATE_DELAY = ud;
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
}

class PlayerUpdate implements Runnable {

	final long UPDATE_DELAY;
	private ArrayList<Char> players;

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
		// TODO Auto-generated method stub
		long time;
		while (true) {
			time = System.currentTimeMillis();
			for (Char c : players)
				c.update();
			while (System.currentTimeMillis() - time < UPDATE_DELAY)
				;
		}
	}

}
