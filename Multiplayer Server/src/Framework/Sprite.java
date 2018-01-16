package Framework;

public class Sprite {
	private final String[] PATH;
	private final int WIDTH, HEIGHT;

	public Sprite(String[] path, int width, int height) {
		PATH = path;
		WIDTH = width;
		HEIGHT = height;
	}

	public String[] getPath() {
		return PATH;
	}

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}
}
