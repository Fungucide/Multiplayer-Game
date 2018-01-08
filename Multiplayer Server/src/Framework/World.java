package Framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class World {
	public final int COMPRESSION, C_WIDTH, C_HEIGHT, WIDTH, HEIGHT, TILE_SIZE;
	private final long UPDATE_DELAY;
	private final Sprite[] SPRITES;
	private PlayerUpdate pu;
	private final ArrayList<Displayable> display;
	private final Terrain[][] TERRAIN;

	public World(int width, int height, int c, int ts, long ud, Sprite[] sprites, ArrayList<Terrain> t) throws Exception {
		WIDTH = width;
		HEIGHT = height;
		SPRITES = sprites;
		UPDATE_DELAY = ud;
		COMPRESSION = c;
		TILE_SIZE = ts;
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

	public World(String path, long ud) throws IOException {
		path = path.replace("\\", "/");
		if (new File(path).isFile())
			throw new IOException("Not a valid path");
		if (!path.endsWith("/"))
			path += "/";
		UPDATE_DELAY = ud;
		BufferedReader info = new BufferedReader(new FileReader(new File(path + "Info.dat")));
		BufferedReader resources = new BufferedReader(new FileReader(new File(path + "Resources.dat")));
		BufferedReader terrain = new BufferedReader(new FileReader(new File(path + "Terrain.dat")));

		String[] temp;

		int width = 0, height = 0, compression = 0, tileSize = 0;
		while (info.ready()) {
			temp = info.readLine().split("=");
			switch (temp[0]) {
			case "width":
				width = Integer.parseInt(temp[1]);
				break;
			case "height":
				height = Integer.parseInt(temp[1]);
			case "compression":
				compression = Integer.parseInt(temp[1]);
			case "tileSize":
				tileSize = Integer.parseInt(temp[1]);
			}
		}
		WIDTH = width;
		HEIGHT = height;
		COMPRESSION = compression;
		TILE_SIZE = tileSize;
		C_WIDTH = WIDTH / COMPRESSION;
		C_HEIGHT = HEIGHT / COMPRESSION;

		ArrayList<Sprite> files = new ArrayList<Sprite>();
		while (resources.ready()) {
			temp = resources.readLine().split(" ");
			if (temp[1].equals("background"))
				files.add(new Sprite(temp[0], TILE_SIZE, TILE_SIZE));
			else if (temp[1].equals("terrain"))
				files.add(new Sprite(temp[0], COMPRESSION, COMPRESSION));
			else
				files.add(new Sprite(temp[0], Integer.parseInt(temp[1]), Integer.parseInt(temp[2])));

		}
		SPRITES = files.toArray(new Sprite[0]);

		int type, x, y, passable, size, graphics, offset;
		display = new ArrayList<Displayable>();
		TERRAIN = new Terrain[C_WIDTH][C_HEIGHT];
		while (terrain.ready()) {
			temp = terrain.readLine().split(" ");// Type, X, Y, Passable, Graphics, Size ?Offset
			type = Integer.parseInt(temp[0]);
			x = Integer.parseInt(temp[1]);
			y = Integer.parseInt(temp[2]);
			passable = Integer.parseInt(temp[3]);
			graphics = Integer.parseInt(temp[4]);
			if (temp.length >= 7)
				offset = Integer.parseInt(temp[6]);
			else
				offset = 0;
			if (type == 0)// Terrain
				display.add(new Terrain(x, y, passable == 1 ? true : false, graphics, SPRITES[graphics].getWidth(), offset));

		}
		setPlayerUpdate(UPDATE_DELAY);

		info.close();
		resources.close();
		terrain.close();
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
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}

	public ArrayList<Displayable> getDisplay(int x, int y, int width, int height) {
		x = x / COMPRESSION - width / 2;
		y = y / COMPRESSION - height / 2;
		ArrayList<Displayable> display = new ArrayList<Displayable>();
		for (Displayable d : this.display) {
			if (d.isWithin(x, y, width, height, d))
				display.add(d);
		}
		return display;
	}

	public Sprite[] getResources() {
		return SPRITES;
	}

	public ArrayList<int[]> getRenderData(int tx, int ty, int bx, int by, Char character) {
		ArrayList<int[]> res = new ArrayList<int[]>();
		for (Char c : pu.players)
			if (c.getX() >= bx && c.getX() <= tx && c.getY() >= by && c.getY() <= ty && !c.equals(character))
				res.add(new int[] { c.getX(), c.getY(), c.getGraphics()[0], c.getGraphics()[1] });
		return res;
	}

	public boolean isBlocked(int qx, int qy) {
		return TERRAIN[qx][qy] != null;
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
