package Framework;

public interface Displayable {

	public int getType();

	public int getX();

	public int getY();

	public int getSize();

	public int[] getGraphics();

	public default boolean isWithin(int x, int y, int width, int height) {
		boolean xBound = getX() + getSize() >= x && getX() - getSize() <= x + width;
		boolean yBound = getY() + getSize() >= y && getY() - getSize() <= y + width;
		return xBound && yBound;
	}

}
