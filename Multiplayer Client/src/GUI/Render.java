package GUI;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Render extends Canvas {

	private int TILE_SIZE;
	private int COMPRESSION;

	private BufferedImage background, tree;

	public int x, y;
	public int[][] data;

	public void setData(int tileSize, int compression) {
		TILE_SIZE = tileSize;
		COMPRESSION = compression;
		try {
			background = toBufferedImage(ImageIO.read(new File("Resources/Background/Background.png")).getScaledInstance(TILE_SIZE, TILE_SIZE, Image.SCALE_SMOOTH));
			tree = toBufferedImage(ImageIO.read(new File("Resources/Background/Tree.png")).getScaledInstance(COMPRESSION, COMPRESSION, Image.SCALE_SMOOTH));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void paint(Graphics g) {

		g.drawImage(background, -(x % (TILE_SIZE - getWidth())), -(y % (TILE_SIZE - getHeight())), this);
		int xOff = -(x % COMPRESSION);
		int yOff = -(y % COMPRESSION);
		if (data != null) {
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data[0].length; j++) {
					if (data[i][j] == 1) {
						g.drawImage(tree, xOff + COMPRESSION * i, yOff + COMPRESSION * j, this);
					}
				}
			}
		}

		g.setColor(Color.DARK_GRAY);
		g.fillOval(0, 0, 75, 75);
		g.fillRoundRect(37, 37, 150, 38, 5, 5);
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	public int getCompression() {
		return COMPRESSION;
	}

	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage)
			return (BufferedImage) img;
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		return bimage;
	}
}
