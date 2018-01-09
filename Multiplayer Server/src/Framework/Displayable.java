package Framework;

public interface Displayable {

	public int getType();

	public int getX();

	public int getY();

	public int getWidth();
	
	public int getHeight();

	public int[] getGraphics();

	public default boolean isWithin(int x, int y, int width, int height) {
		boolean xBound = getX() + getWidth() >= x && getX() - getWidth() <= x + width;
		boolean yBound = getY() + getHeight() >= y && getY() - getHeight() <= y + width;
		return xBound && yBound;
	}

}
