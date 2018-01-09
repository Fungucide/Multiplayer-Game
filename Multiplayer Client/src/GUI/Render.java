package GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import Framework.Char;
import Framework.Displayable;

public class Render extends JPanel {

	public Render() {
		super();
		setDoubleBuffered(true);
		display = new ArrayList<Displayable>();
	}

	private int TILE_SIZE;
	private int COMPRESSION;
	private Char CHARACTER;
	private int MIDDLE_Q_X;
	private int MIDDLE_Q_Y;
	private int Y_OFF = 5;

	public int x, y;
	public ArrayList<Displayable> display;
	private int rx, ry;
	private int drawX, drawY;
	public int[][] charData;
	private int[][] rCharData;
	private BufferedImage[] worldResources, charResources;

	public void setData(int tileSize, int compression) {
		TILE_SIZE = tileSize;
		COMPRESSION = compression;
		drawX = getWidth() / 2 - Char.PLAYER_SIZE / 2;
		drawY = getHeight() / 2 - Char.PLAYER_SIZE / 2;
		MIDDLE_Q_X = drawX / COMPRESSION + 1;
		MIDDLE_Q_Y = drawY / COMPRESSION + 1;
	}

	public void setWorldResources(BufferedImage[] resources) {
		worldResources = resources;
	}

	public void setCharResources(BufferedImage[] resources) {
		charResources = resources;
	}

	public void setChar(Char c) {
		CHARACTER = c;
	}

	int lXq = -1, lYq = -1;

	public void paint(Graphics g) {
		if (COMPRESSION == 0 || TILE_SIZE == 0)
			return;
		rx = x;
		ry = y;

		rCharData = charData;
		int xOff = -(rx % COMPRESSION);
		int yOff = -(ry % COMPRESSION);
		if (worldResources != null && charResources != null && CHARACTER != null) {
			for (int i = 0; i * TILE_SIZE <= getWidth() + TILE_SIZE; i++) {
				for (int j = 0; j * TILE_SIZE <= getHeight() + TILE_SIZE; j++) {
					g.drawImage(worldResources[0], xOff + i * TILE_SIZE, yOff + j * TILE_SIZE, this);
				}
			}
			for (Displayable d : display) {
				if (d.getType() == 0)// Player
					g.drawImage(charResources[d.getGraphics()[0]], d.getX() - rx + drawX, d.getY() - ry + drawY, this);
				else if (d.getType() == 1) {// Terrain
					g.drawImage(worldResources[d.getGraphics()[0]], d.getX() - rx, d.getY() - ry, this);
				}
			}
		}
		g.drawOval(getWidth() / 2, getHeight() / 2, 1, 1);
		g.setColor(Color.DARK_GRAY);
		g.fillOval(0, 0, 75, 75);
		g.fillRoundRect(37, 37, 150, 38, 5, 5);
	}

	public int getCompression() {
		return COMPRESSION;
	}

}
