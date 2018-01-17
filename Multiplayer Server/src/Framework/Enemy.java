package Framework;

public class Enemy implements Damage {

	private int x, y, health, speed, frame;
	private final int WIDTH, HEIGHT, GRAPHICS;

	public Enemy(int x, int y, int health, int speed, int graphics, int frame, int width, int height) {
		this.x = x;
		this.y = y;
		GRAPHICS = graphics;
		this.frame = frame;
		WIDTH = width;
		HEIGHT = height;
	}

	public int getType() {
		return 3;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}

	public int[] getGraphics() {
		return new int[] { GRAPHICS, frame };
	}

	public int getHealth() {
		return health;
	}

	public void doDamage(int damage) {
		health -= damage;
	}

}
