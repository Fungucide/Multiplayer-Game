package Framework;

public class Terrain implements Damage {
	private final int X, Y, GRAPHICS, OFFSET, WIDTH, HEIGHT;
	private boolean passable, breakable;
	private int frame = 0, health;

	public Terrain(int x, int y, boolean passable, boolean breakable, int graphics, int width, int height) {
		this(x, y, passable, breakable, graphics, width, height, 0, 0);
	}

	public Terrain(int x, int y, boolean passable, boolean breakable, int graphics, int width, int height, int offset, int health) {
		this.X = x;
		this.Y = y;
		this.passable = passable;
		this.breakable = breakable;
		this.GRAPHICS = graphics;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.OFFSET = offset;
		this.health = health;
	}

	public void setPassable(boolean p) {
		passable = p;
	}

	public boolean isBreakable() {
		return breakable;
	}

	public int getX() {
		return X;
	}

	public int getY() {
		return Y;
	}

	public int getHealth() {
		return health;
	}

	public void doDamage(int damage) {
		if (!breakable)
			return;
		health -= damage;
		if (health <= 0)
			passable = true;//Will most likely add sprite change latter
	}

	public int getOffSet() {
		return OFFSET;
	}

	public boolean isPassable() {
		return passable;
	}

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}

	public boolean validBound(int width, int height) {
		boolean xBound = X >= 0 && X < width;
		boolean yBound = Y >= 0 && Y < height;
		return xBound && yBound;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Terrain)
			return getX() == ((Terrain) o).getX() && getY() == ((Terrain) o).getY();
		return false;
	}

	public int[] getGraphics() {
		return new int[] { GRAPHICS, frame };
	}

	public int getType() {
		return 1;
	}

}
