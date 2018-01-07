package Framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;

public class World {
	public final int COMPRESSION, C_WIDTH, C_HEIGHT;
	private final long UPDATE_DELAY;
	private final String[] path;
	private final int width, height;
	private PlayerUpdate pu;
	private final ArrayList<Displayable> display;
	private final Terrain[][] TERRAIN;

	public World(int width, int height, int c, long ud, String[] path, ArrayList<Terrain> t) throws Exception {
		this.width = width;
		this.height = height;
		this.path = path;
		UPDATE_DELAY = ud;
		COMPRESSION = c;
		C_WIDTH = width / c;
		C_HEIGHT = height / c;
		setPlayerUpdate(UPDATE_DELAY);
		display = new ArrayList<Displayable>();
		display.addAll(t);
		TERRAIN = new Terrain[C_WIDTH][C_HEIGHT];
		for (Terrain ter : t) {
			if (ter.validBound(C_WIDTH, C_HEIGHT))
				TERRAIN[ter.getX()][ter.getY()] = ter;
			else
				throw new IndexOutOfBoundsException(String.format("Terrain location [x=%d y=%d] does not fit in World size [x=%d y=%d]", ter.getX(), ter.getY(), width, height));
		}
	}

	/*
	 * public World(String path, long ud) throws IOException { UPDATE_DELAY = ud;
	 * BufferedReader worldRead = new BufferedReader(new FileReader(new
	 * File(path))); width = Integer.parseInt(worldRead.readLine()); height =
	 * Integer.parseInt(worldRead.readLine()); COMPRESSION =
	 * Integer.parseInt(worldRead.readLine()); int[][] data = new
	 * int[width][height]; String[] input; for (int i = 0; i < height / COMPRESSION;
	 * i++) { input = worldRead.readLine().split(" "); for (int j = 0; j < width /
	 * COMPRESSION; j++) { data[j][i] = Integer.parseInt(input[j]); } } int size =
	 * Integer.parseInt(worldRead.readLine()); this.path = new String[size]; type =
	 * new int[size][2]; HashSet<Integer> pass = new HashSet<Integer>(); for (int i
	 * = 0; i < size; i++) { String[] in = worldRead.readLine().split(" "); if
	 * (in[0].equals("P")) pass.add(i); if (in.length == 3) { type[i][0] =
	 * Integer.parseInt(in[2]); type[i][1] = -1; } else { type[i][0] =
	 * Integer.parseInt(in[2]); type[i][1] = Integer.parseInt(in[3]); } this.path[i]
	 * = in[1]; } worldRead.close(); terrain = new Terrain(data, COMPRESSION, pass);
	 * if (terrain.width != width || terrain.height != height) throw new
	 * IllegalArgumentException(String.
	 * format("World size [x=%d y=%d] does not match Terrain size [x=%d y=%d]",
	 * width, height, terrain.width, terrain.height));
	 * setPlayerUpdate(UPDATE_DELAY); }
	 */

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

	public ArrayList<Displayable> getDisplay(int x, int y, int width, int height) {
		ArrayList<Displayable> display = new ArrayList<Displayable>();
		for (Displayable d : this.display) {
			if (d.isWithin(x, y, width, height, d))
				display.add(d);
		}
		return display;
	}

	public String[] getResources() {
		return path;
	}

	public ArrayList<int[]> getRenderData(int tx, int ty, int bx, int by, Char character) {
		ArrayList<int[]> res = new ArrayList<int[]>();
		for (Char c : pu.players)
			if (c.getX() >= bx && c.getX() <= tx && c.getY() >= by && c.getY() <= ty && !c.equals(character))
				res.add(new int[] { c.getX(), c.getY(), c.getGraphics() });
		return res;
	}

	public boolean isBlocked(int qx, int qy) {
		return TERRAIN[qx][qy]!=null;
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
			try {
				time = System.currentTimeMillis();
				for (Char c : players)
					c.update();
				while (System.currentTimeMillis() - time < UPDATE_DELAY);
			} catch (ConcurrentModificationException e) {

			}
		}
	}

}
