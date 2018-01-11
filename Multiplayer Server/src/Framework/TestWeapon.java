package Framework;

import java.util.ArrayList;

public class TestWeapon extends Weapon {

	public TestWeapon(int fireRate) {
		super(fireRate);
	}

	public ArrayList<Projectile> attack(int x, int y, double direction) {
		if (cooldown >= fireRate) {
			ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
			projectiles.add(new Projectile(x, y, 10, 10, direction, 10, 7, 100, 1, 1, 1));
			cooldown = 0;
			return projectiles;
		}
		return null;
	}

}
