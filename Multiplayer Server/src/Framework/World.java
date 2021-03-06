package Framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;

public class World {
	public final int COMPRESSION, C_WIDTH, C_HEIGHT, WIDTH, HEIGHT, TILE_SIZE;
	private final long UPDATE_DELAY;
	private final Sprite[] SPRITES;
	private PlayerUpdate pu;
	private final ArrayList<Terrain> terrainDisplay;
	private final ArrayList<Char> charDisplay;
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
		terrainDisplay = new ArrayList<Terrain>();
		charDisplay = new ArrayList<Char>();
		terrainDisplay.addAll(t);
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
				break;
			case "compression":
				compression = Integer.parseInt(temp[1]);
				break;
			case "tileSize":
				tileSize = Integer.parseInt(temp[1]);
				break;
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
			if (temp[0].equals("background"))
				files.add(new Sprite(Arrays.copyOfRange(temp, 1, temp.length), TILE_SIZE, TILE_SIZE));
			else if (temp[0].equals("terrain"))
				files.add(new Sprite(Arrays.copyOfRange(temp, 1, temp.length), COMPRESSION, COMPRESSION));
			else
				files.add(new Sprite(Arrays.copyOfRange(temp, 2, temp.length), Integer.parseInt(temp[0]), Integer.parseInt(temp[1])));

		}
		SPRITES = files.toArray(new Sprite[0]);

		int type, x, y, passable, breakable, graphics, offset, health;
		terrainDisplay = new ArrayList<Terrain>();
		charDisplay = new ArrayList<Char>();
		TERRAIN = new Terrain[C_WIDTH][C_HEIGHT];
		while (terrain.ready()) {
			temp = terrain.readLine().split(" ");// Type, X, Y, Passable, Breakable,Graphics,Offset,Health
			type = Integer.parseInt(temp[0]);
			x = Integer.parseInt(temp[1]);
			y = Integer.parseInt(temp[2]);
			passable = Integer.parseInt(temp[3]);
			breakable = Integer.parseInt(temp[4]);
			graphics = Integer.parseInt(temp[5]);
			if (temp.length >= 8) {
				offset = Integer.parseInt(temp[6]);
				health = Integer.parseInt(temp[7]);
			} else {
				offset = 0;
				health = 0;
			}
			if (type == 1) {// Terrain
				TERRAIN[x][y] = new Terrain(x * COMPRESSION, y * COMPRESSION, passable == 1 ? true : false, breakable == 1 ? true : false, graphics, SPRITES[graphics].getWidth(), SPRITES[graphics].getHeight(), offset, health);
				terrainDisplay.add(TERRAIN[x][y]);
			}

		}
		setPlayerUpdate(UPDATE_DELAY);

		info.close();
		resources.close();
		terrain.close();
	}

	public void setPlayerUpdate(long updateDelay) {
		pu = new PlayerUpdate(updateDelay);
		pu.updateDisplay(charDisplay, terrainDisplay);
		Thread t = new Thread(pu);
		t.start();
	}

	public boolean addPlayer(Char c) {
		charDisplay.add(c);
		pu.updateDisplay(charDisplay, terrainDisplay);
		return pu.add(c);
	}

	public boolean removePlayer(Char c) {
		System.out.println(charDisplay.remove(c));
		pu.updateDisplay(charDisplay, terrainDisplay);
		return pu.delete(c);
	}

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}

	public ArrayList<Displayable> getDisplay(int x, int y, int width, int height, Char cur) {
		x -= width >> 1;
		y -= height >> 1;
		ArrayList<Displayable> display = new ArrayList<Displayable>();
		for (Terrain t : terrainDisplay) {
			if (t.isWithin(x, y, width, height))
				display.add(t);
		}
		for (Char c : charDisplay) {
			for (Projectile p : c.getProjectiles())
				if (p.isWithin(x, y, width, height))
					display.add(p);
			if (c.equals(cur))
				continue;
			display.add(c);
		}
		return display;
	}

	public Sprite[] getResources() {
		return SPRITES;
	}

	@Deprecated
	public ArrayList<int[]> getRenderData(int tx, int ty, int bx, int by, Char character) {
		ArrayList<int[]> res = new ArrayList<int[]>();
		for (Char c : pu.getPlayers())
			if (c.getX() >= bx && c.getX() <= tx && c.getY() >= by && c.getY() <= ty && !c.equals(character))
				res.add(new int[] { c.getX(), c.getY(), c.getGraphics()[0], c.getGraphics()[1] });
		return res;
	}

	public boolean isBlocked(int qx, int qy) {
		if (qx < 0 || qy < 0 || qx >= TERRAIN.length || qy >= TERRAIN[qx].length)
			return true;
		return TERRAIN[qx][qy] != null && !TERRAIN[qx][qy].isPassable();
	}
}

class PlayerUpdate implements Runnable {

	final long UPDATE_DELAY;
	private ArrayList<Char> players;
	private ArrayList<Displayable> objects;

	public PlayerUpdate(long ud) {
		UPDATE_DELAY = ud;
		players = new ArrayList<Char>();
		objects = new ArrayList<Displayable>();
	}

	public void updateDisplay(ArrayList<Char> player, ArrayList<Terrain> enemy) {
		objects.clear();
		objects.addAll(player);
		objects.addAll(enemy);
	}

	public ArrayList<Char> getPlayers() {
		return players;
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
				for (Char c : players) {
					c.update();
					c.updateProjectiles(objects);
				}
				while (System.currentTimeMillis() - time < UPDATE_DELAY);
			} catch (ConcurrentModificationException e) {

			}
		}
	}

}
