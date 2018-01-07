package Framework;

public class Terrain implements Displayable {
	private final int x, y, graphics, offset, size;
	private boolean passable;

	public Terrain(int x, int y, boolean passable, int size, int graphics) {
		this(x, y, passable, graphics, size, 0);
	}

	public Terrain(int x, int y, boolean passable, int graphics, int size, int offset) {
		this.x = x;
		this.y = y;
		this.passable = passable;
		this.graphics = graphics;
		this.size = size;
		this.offset = offset;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getOffSet() {
		return offset;
	}

	public boolean isPassable() {
		return passable;
	}
	
	public int getSize() {
		return size;
	}

	public boolean validBound(int width, int height) {
		boolean xBound = x >= 0 && x < width;
		boolean yBound = y >= 0 && y < height;
		return xBound && yBound;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Terrain)
			return getX() == ((Terrain) o).getX() && getY() == ((Terrain) o).getY();
		return false;
	}

}
