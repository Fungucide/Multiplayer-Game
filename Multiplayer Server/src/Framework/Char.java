package Framework;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Char implements Closeable, Damage {
	public static String PATH = "";
	private static int PLAYER_SIZE = 100;
	public static HashMap<String, Integer> CHAR_PIC = new HashMap<String, Integer>();
	public static ArrayList<String> CHAR_PIC_AL = new ArrayList<String>();
	private final String USERNAME;
	private final int INDEX;
	private double DIAGONAL_MOD = Math.sqrt(.5d);
	private int xMove = 0, yMove = 0;
	private int x = 0, y = 0, maxHealth = 100, health = 100, attack = 10, maxMana = 100, mana = 100, power = 10, speed = 5, graphics = 0, frame = 0;
	private double direction = 0;
	private ArrayList<Projectile> projectiles;
	private Queue<Projectile> removeProjectile;
	private World WORLD;
	private Weapon WEAPON;
	private boolean attacking = false;

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
		projectiles = new ArrayList<Projectile>();
		removeProjectile = new LinkedList<Projectile>();
	}

	public Char(String userName, int index) throws IOException {
		USERNAME = userName;
		INDEX = index;
		projectiles = new ArrayList<Projectile>();
		removeProjectile = new LinkedList<Projectile>();
		WEAPON = new TestWeapon(10);
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

	public int[] getGraphics() {
		return new int[] { graphics, frame };
	}

	public World getWorld() {
		return WORLD;
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

	public void setAttack(boolean s, double direction) {
		attacking = s;
		this.direction = direction;
	}

	private int[] correction(int sx, int sy, int ex, int ey) {
		int xQuad = ex / WORLD.COMPRESSION;
		int yQuad = ey / WORLD.COMPRESSION;
		int rx = ex;
		int ry = ey;
		if (WORLD.isBlocked(xQuad, yQuad)) {
			if (WORLD.isBlocked(xQuad, sy / WORLD.COMPRESSION)) {
				rx = sx;
			}
			if (WORLD.isBlocked(sx / WORLD.COMPRESSION, yQuad)) {
				ry = sy;
			}
		}

		return new int[] { rx, ry };
	}

	public ArrayList<Projectile> getProjectiles() {
		return projectiles;
	}

	public void updateProjectiles(ArrayList<Displayable> objects) {
		for (Projectile p : projectiles) {
			for (Displayable d : objects) {
				if (!equals(d))
					p.interact(d);
				if (p.getLifeTime() <= 0 || p.getPierce() == 0) {
					removeProjectile.add(p);
					break;
				}
			}
			p.move();
			if (p.getLifeTime() <= 0 || p.getPierce() == 0) {
				removeProjectile.add(p);
				continue;
			}
		}
		while (!removeProjectile.isEmpty())
			projectiles.remove(removeProjectile.remove());
	}

	public void update() {
		if (WEAPON != null) {
			WEAPON.update();
			if (attacking) {
				ArrayList<Projectile> p = WEAPON.attack(x, y, direction);
				if (p != null)
					projectiles.addAll(p);
			}
		}
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
		else if (WORLD != null && x > WORLD.getWidth())
			x = WORLD.getWidth();
	}

	private void moveY(int dis) {
		y += dis;
		if (y < 0)
			y = 0;
		else if (WORLD != null && y > WORLD.getHeight())
			y = WORLD.getHeight();
	}

	public void setWorld(World w) {
		if (this.WORLD != null)
			this.WORLD.removePlayer(this);
		this.WORLD = w;
		this.WORLD.addPlayer(this);
	}

	public static void setCharSize(int size) {
		PLAYER_SIZE = size;
	}

	public void tp(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void close() throws IOException {
		WORLD.removePlayer(this);
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

	public boolean equals(Object o) {
		if (o instanceof Char) {
			return ((Char) o).USERNAME.equals(USERNAME);
		}
		return false;
	}

	public int getWidth() {
		return PLAYER_SIZE;
	}

	public int getHeight() {
		return PLAYER_SIZE;
	}

	public int getType() {
		return 0;
	}

	public void doDamage(int damage) {
		health -= damage;
	}

}
