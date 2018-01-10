package GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

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
	private int HALF_PLAYER_SIZE;

	public ArrayList<Displayable> display;
	public int x, y;
	private int middleX, middleY;
	private BufferedImage[] worldResources, charResources;

	public void setData(int tileSize, int compression) {
		TILE_SIZE = tileSize;
		COMPRESSION = compression;
		middleX = getWidth() / 2;
		middleY = getHeight() / 2;
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

	public void paint(Graphics g) {
		if (COMPRESSION == 0 || TILE_SIZE == 0)
			return;

		int xOff = -(x % COMPRESSION);
		int yOff = -(y % COMPRESSION);
		if (worldResources != null && charResources != null && CHARACTER != null) {

			for (int i = 0; i * TILE_SIZE <= getWidth() + TILE_SIZE; i++) {
				for (int j = 0; j * TILE_SIZE <= getHeight() + TILE_SIZE; j++) {
					g.drawImage(worldResources[0], xOff + i * TILE_SIZE, yOff + j * TILE_SIZE, this);
				}
			}

			display.add(new Displayable(0, x, y, Char.playerSize(), Char.playerSize(), 0, CHARACTER.getGraphics(), CHARACTER.getFrame()));
			Collections.sort(display);
			for (Displayable d : display) {
				if (d.getType() == 0)// Player
					g.drawImage(charResources[d.getGraphics()[0]], d.getX() - Char.playerSize() - x + middleX, d.getY() - Char.playerSize() - y + middleY, this);
				else if (d.getType() == 1) {// Terrain
					g.drawImage(worldResources[d.getGraphics()[0]], d.getX() - d.getHalfWidth() - x + middleX + 3, d.getY() - d.getHalfHeight() - y + middleY - 5, this);
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
