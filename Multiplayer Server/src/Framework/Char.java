package Framework;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Char implements Closeable, Displayable {
	public static String PATH = "";
	private static int PLAYER_SIZE = 100;
	private static int PLAYER_SIZE_HALF = 50;
	public static HashMap<String, Integer> CHAR_PIC = new HashMap<String, Integer>();
	public static ArrayList<String> CHAR_PIC_AL = new ArrayList<String>();
	private final String USERNAME;
	private final int INDEX;
	private double DIAGONAL_MOD = Math.sqrt(.5d);
	private int xMove = 0, yMove = 0;
	private int x = 0, y = 0, maxHealth = 100, health = 100, attack = 10, maxMana = 100, mana = 100, power = 10, speed = 5, graphics = 0;
	public World w;

	public Char(String userName, int x, int y, int maxHealth, int health, int attack, int maxMana, int mana, int power, int speed) {
		USERNAME = userName;
		int i;
		for (i = 0; new File(PATH + userName + "/Characters/Character" + i + ".dat").exists(); i++);
		INDEX = i;
		this.x = x;
		this.y = y;
		this.maxHealth = maxHealth;
		this.health = health;
		this.attack = attack;
		this.maxMana = maxMana;
		this.mana = mana;
		this.power = power;
		this.speed = speed;

	}

	public Char(String userName, int index) throws IOException {
		USERNAME = userName;
		INDEX = index;
		String FILE_PATH = PATH + userName + "/Characters/Character" + index + ".dat";
		File f = new File(FILE_PATH);
		f.createNewFile();
		BufferedReader br = new BufferedReader(new FileReader(f));
		String[] input;
		while (br.ready()) {
			input = br.readLine().split("=");
			switch (input[0]) {
			case "X":
				x = Integer.parseInt(input[1]);
				break;
			case "Y":
				y = Integer.parseInt(input[1]);
				break;
			case "MaxHealth":
				maxHealth = Integer.parseInt(input[1]);
				break;
			case "Health":
				health = Integer.parseInt(input[1]);
				break;
			case "Attack":
				attack = Integer.parseInt(input[1]);
				break;
			case "MaxMana":
				maxMana = Integer.parseInt(input[1]);
				break;
			case "Mana":
				mana = Integer.parseInt(input[1]);
				break;
			case "Power":
				power = Integer.parseInt(input[1]);
				break;
			case "Speed":
				speed = Integer.parseInt(input[1]);
				break;
			case "graphics":
				graphics = CHAR_PIC.get(input[1]);
				break;
			}
		}
		br.close();
	}

	public static int getCharSize() {
		return PLAYER_SIZE;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public int getHealth() {
		return health;
	}

	public int getAttack() {
		return attack;
	}

	public int getMaxMana() {
		return maxMana;
	}

	public int getMana() {
		return mana;
	}

	public int getPower() {
		return power;
	}

	public int getSpeed() {
		return speed;
	}

	public int getGraphics() {
		return graphics;
	}

	public void move(int x, int y) {
		if (x == 0)
			xMove = 0;
		else if (x > 0)
			xMove = 1;
		else if (x < 0)
			xMove = -1;

		if (y == 0)
			yMove = 0;
		else if (y > 0)
			yMove = 1;
		else if (y < 0)
			yMove = -1;
	}

	int lQx = -1;
	int lQy = -1;

	private int[] correction(int sx, int sy, int ex, int ey) {
		int xQuad = ex / w.COMPRESSION;
		int yQuad = ey / w.COMPRESSION;
		int rx = ex;
		int ry = ey;
		if (w.isBlocked(xQuad, yQuad)) {
			if (w.isBlocked(xQuad, sy / w.COMPRESSION)) {
				rx = sx;
			}
			if (w.isBlocked(sx / w.COMPRESSION, yQuad)) {
				ry = sy;
			}
		}

		return new int[] { rx, ry };
	}

	public void update() {
		if (xMove == 0 && yMove == 0)
			return;
		int totalX;
		int totalY;
		if (xMove != 0 && yMove != 0) {
			totalX = x + (int) Math.floor(DIAGONAL_MOD * (double) speed * (double) xMove);
			totalY = y + (int) Math.floor(DIAGONAL_MOD * (double) speed * (double) yMove);
		} else {
			totalX = x + xMove * speed;
			totalY = y + yMove * speed;
		}
		int[] cor = correction(x, y, totalX, totalY);
		moveX(cor[0] - x);
		moveY(cor[1] - y);
	}

	private void moveX(int dis) {
		x += dis;
		if (x < 0)
			x = 0;
		else if (w != null && x > w.getWidth())
			x = w.getWidth();
	}

	private void moveY(int dis) {
		y += dis;
		if (y < 0)
			y = 0;
		else if (w != null && y > w.getHeight())
			y = w.getHeight();
	}

	public void setWorld(World w) {
		if (this.w != null)
			this.w.removePlayer(this);
		this.w = w;
		this.w.addPlayer(this);
	}

	public static void setCharSize(int size) {
		PLAYER_SIZE = size;
		PLAYER_SIZE_HALF = size / 2;
	}

	public void tp(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void close() throws IOException {
		w.removePlayer(this);
		FileWriter fw = new FileWriter(PATH + USERNAME + "/Characters/Character" + INDEX + ".dat");
		fw.write("x=" + x + "\n");
		fw.write("y=" + y + "\n");
		fw.write("maxHealth=" + maxHealth + "\n");
		fw.write("health=" + health + "\n");
		fw.write("attack=" + attack + "\n");
		fw.write("maxMana=" + maxMana + "\n");
		fw.write("mana=" + mana + "\n");
		fw.write("power=" + power + "\n");
		fw.write("speed=" + speed + "\n");
		File f = new File(CHAR_PIC_AL.get(graphics));
		fw.write("graphics=" + f.getName().substring(0, f.getName().indexOf('.')));
		fw.close();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Char) {
			return ((Char) o).USERNAME.equals(USERNAME);
		}
		return false;
	}

	@Override
	public int getSize() {
		return PLAYER_SIZE;
	}

}
