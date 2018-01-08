package Framework;

public interface Displayable {

	public int getX();

	public int getY();

	public int getSize();
	
	public int[] getGraphics();
	
	public default boolean isWithin(int x, int y, int width, int height, Displayable d) {
		boolean xBound = d.getX() + d.getSize() >= x && d.getX() - d.getSize() <= x + width;
		boolean yBound = d.getY() + d.getSize() >= y && d.getY() - d.getSize() <= y + width;
		return xBound && yBound;
	}

}
