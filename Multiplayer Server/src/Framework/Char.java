package Framework;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Char implements Closeable {
	private final String USERNAME;
	private final int INDEX;
	private double DIAGONAL_MOD = Math.sqrt(.5d);
	private int xMove = 0, yMove = 0;
	private int x = 0, y = 0, maxHealth = 100, health = 100, attack = 10, maxMana = 100, mana = 100, power = 10, speed = 5;
	public World w;

	public Char(String userName, int x, int y, int maxHealth, int health, int attack, int maxMana, int mana, int power, int speed) {
		USERNAME = userName;
		int i;
		for (i = 0; new File("Data/Player/" + userName + "/Characters/Character" + i + ".dat").exists(); i++)
			;
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
		String FILE_PATH = "Data/Player/" + userName + "/Characters/Character" + index + ".dat";
		File f = new File(FILE_PATH);
		f.createNewFile();
		BufferedReader br = new BufferedReader(new FileReader(f));
		String[] input;
		while (br.ready()) {
			input = br.readLine().split(" ");
			if (input[0].equals("Health:")) {
				health = Integer.parseInt(input[1]);
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
		else if (x > w.getWidth())
			x = w.getWidth();
	}

	private void moveY(int dis) {
		y += dis;
		if (y < 0)
			y = 0;
		else if (y > w.getHeight())
			y = w.getHeight();
	}

	public void setWorld(World w) {
		if(this.w!=null)
			this.w.removePlayer(this);
		this.w = w;
		this.w.addPlayer(this);
	}

	@Override
	public void close() throws IOException {
		FileWriter fw = new FileWriter("Data/Player/" + USERNAME + "/Characters/Character" + INDEX + ".dat");
		fw.write("x=" + x);
		fw.write("y=" + y);
		fw.write("maxHealth=" + maxHealth);
		fw.write("health=" + health);
		fw.write("attack=" + attack);
		fw.write("maxMana=" + maxMana);
		fw.write("mana=" + mana);
		fw.write("power=" + power);
		fw.write("speed=" + speed);
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
