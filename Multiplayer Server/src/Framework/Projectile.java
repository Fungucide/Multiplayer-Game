package Framework;

public class Projectile implements Displayable {

	private int x, y, damage, speed, pierce, graphics, frame = 0, lifeTime;
	private final int WDITH, HEIGHT, TYPE;
	private double d_sin, d_cos;

	public Projectile(int x, int y, int width, int height, double direction, int damage, int speed, int pierce, int type, int graphics) {
		this.x = x;
		this.y = y;
		this.WDITH = width;
		this.HEIGHT = height;
		this.damage = damage;
		this.speed = speed;
		this.pierce = pierce;
		this.TYPE = type;
		this.graphics = graphics;
		d_sin = Math.sin(direction);//Radians
		d_cos = Math.cos(direction);//Radians
	}

	public void move() {// Remove projectile by lifeTime
		x += (int) (d_sin * speed);
		y += (int) (d_cos * speed);
		lifeTime--;
	}

	public int getLifeTime() {
		return lifeTime;
	}

	public int getType() {
		return 2;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return WDITH;
	}

	public int getHeight() {
		return HEIGHT;
	}

	public int getPierce() {
		return pierce;
	}

	public int[] getGraphics() {
		return new int[] { graphics, frame };
	}

	public boolean collide(Displayable d) {
		int cx = (int) (x + d_sin * getWidth());
		int cy = (int) (y + d_cos * getWidth());
		boolean xBound = d.getX() + d.getWidth() >= cx && d.getX() - d.getWidth() <= cx;
		boolean yBound = d.getY() + d.getHeight() >= cy && d.getY() - d.getHeight() <= cy;
		return xBound && yBound;
	}

	public void interact(Displayable d) {// Remove projectile based on pierce
		if (collide(d)) {
			if (d instanceof Terrain) {
				d = (Terrain) d;
				if (pierce != 0) {
					((Terrain) d).doDamage(damage);
					if (pierce != -1)
						pierce--;
				}
			} else if (TYPE > 0 && d instanceof Enemy) {
				d = (Enemy) d;
				if (pierce != 0) {
					((Enemy) d).doDamage(damage);
					if (pierce != -1)
						pierce--;
				}
			} else if (TYPE < 0 && d instanceof Char) {
				d = (Char) d;
				if (pierce != 0) {
					((Char) d).doDamage(damage);
					if (pierce != -1)
						pierce--;
				}
			}
		}
	}

}