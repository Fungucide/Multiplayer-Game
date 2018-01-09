package Framework;

public class Displayable implements Comparable<Displayable> {

	private final int TYPE, X, Y, HALF_WIDTH, HALF_HEIGHT, OFF_SET, GRAPHICS, FRAME;

	public Displayable(int type, int x, int y, int width, int height, int offSet, int graphics, int frame) {
		TYPE = type;
		X = x;
		Y = y;
		HALF_WIDTH = width / 2;
		HALF_HEIGHT = height / 2;
		OFF_SET = offSet;
		GRAPHICS = graphics;
		FRAME = frame;
	}

	public int getType() {
		return TYPE;
	}

	public int getX() {
		return X;
	}

	public int getY() {
		return Y;
	}

	public int getHalfWidth() {
		return HALF_WIDTH;
	}

	public int getHalfHeight() {
		return HALF_HEIGHT;
	}

	public int getOffset() {
		return OFF_SET;
	}

	public int[] getGraphics() {
		return new int[] { GRAPHICS, FRAME };
	}

	@Override
	public int compareTo(Displayable o) {
		if (Y + OFF_SET > o.getY() + o.getOffset())
			return 1;
		if (Y + OFF_SET < o.getY() + o.getOffset())
			return -1;
		return 0;
	}

}
