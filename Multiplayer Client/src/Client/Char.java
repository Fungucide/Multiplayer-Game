package Client;

public class Char {
	private int x, y, maxHealth = 100, health = 100, attack = 10, maxMana = 100, mana = 100, power = 10, speed = 10;

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

	public void setStats(int x, int y, int maxHealth, int health, int attack, int maxMana, int mana, int power, int speed) {
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

}
