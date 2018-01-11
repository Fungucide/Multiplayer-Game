package Framework;

import java.util.ArrayList;

public abstract class Weapon {

	protected int fireRate, cooldown;

	public Weapon(int fireRate) {
		this.fireRate = fireRate;
		cooldown = 0;
	}

	public void update() {
		if (cooldown < fireRate)
			cooldown++;
	}

	public abstract ArrayList<Projectile> attack(int x, int y, double direction);

}
