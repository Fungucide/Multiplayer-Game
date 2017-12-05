package Framework;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Char implements Closeable {
	public static String PATH = "";
	public static int PLAYER_SIZE = 100;
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

	public void update() {
		if (xMove != 0 && yMove != 0) {
			moveX((int) Math.floor(DIAGONAL_MOD * (double) speed * (double) xMove));
			moveY((int) Math.floor(DIAGONAL_MOD * (double) speed * (double) yMove));
		} else {
			moveX(xMove * speed);
			moveY(yMove * speed);
		}
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

}
