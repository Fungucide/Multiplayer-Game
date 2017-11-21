package Framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Char {
	private double DIAGONAL_MOD = Math.sqrt(.5d);
	private int x, y, health, attack, mana, power, speed;
	public World w;

	public Char(int x, int y, int health, int attack, int mana, int power, int speed) {
		this.x = x;
		this.y = y;
		this.health = health;
		this.attack = attack;
		this.mana = mana;
		this.power = power;
		this.speed = speed;
	}

	public Char(String userName) throws IOException {
		File f = new File("Data/Player/" + userName + "/data.dat");
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

	public int getHealth() {
		return health;
	}

	public int getAttack() {
		return attack;
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
		if (x != 0 && y != 0) {
			moveX((int) Math.floor(DIAGONAL_MOD * (double) speed * (double) x));
			moveY((int) Math.floor(DIAGONAL_MOD * (double) speed * (double) y));
		} else {
			moveX(x * speed);
			moveY(y * speed);
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
		this.w = w;
	}

}
