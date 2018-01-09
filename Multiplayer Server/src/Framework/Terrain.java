package Framework;

public class Terrain implements Displayable {
	private final int X, Y, GRAPHICS, OFFSET, WIDTH, HEIGHT;
	private boolean passable;
	private int frame = 0;

	public Terrain(int x, int y, boolean passable, int width, int height, int graphics) {
		this(x, y, passable, graphics, width, height, 0);
	}

	public Terrain(int x, int y, boolean passable, int graphics, int width, int height, int offset) {
		this.X = x;
		this.Y = y;
		this.passable = passable;
		this.GRAPHICS = graphics;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.OFFSET = offset;
	}

	public int getX() {
		return X;
	}

	public int getY() {
		return Y;
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
