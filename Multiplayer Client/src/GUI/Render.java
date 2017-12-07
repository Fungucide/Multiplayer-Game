package GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import Client.Char;

public class Render extends JPanel {

	public Render() {
		super();
		setDoubleBuffered(true);
	}

	private int TILE_SIZE;
	private int COMPRESSION;
	private Char CHARACTER;

	public int x, y;
	private int rx, ry;
	private int drawX, drawY;
	public int[][] data;
	private int[][] rdata;
	public int[][] charData;
	private int[][] rCharData;
	private BufferedImage[] worldResources, charResources;

	public void setData(int tileSize, int compression) {
		TILE_SIZE = tileSize;
		COMPRESSION = compression;
		drawX = getWidth() / 2 - Char.PLAYER_SIZE / 2;
		drawY = getHeight() / 2 - Char.PLAYER_SIZE / 2;
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
		rx = x;
		ry = y;

		rdata = data;
		rCharData = charData;
		int xOff = -(rx % COMPRESSION);
		int yOff = -(ry % COMPRESSION);
		if (rdata != null && worldResources != null) {
			for (int i = 0; i * TILE_SIZE <= getWidth() + TILE_SIZE; i++) {
				for (int j = 0; j * TILE_SIZE <= getHeight() + TILE_SIZE; j++) {
					g.drawImage(worldResources[0], xOff + i * TILE_SIZE, yOff + j * TILE_SIZE, this);
				}
			}
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data[0].length; j++) {
					if (rdata[i][j] == 1) {
						g.drawImage(worldResources[1], xOff + COMPRESSION * (i - 1), yOff + COMPRESSION * (j - 1) + 5, this);
					}
				}
			}
		}

		if (charResources != null && CHARACTER != null) {
			g.drawImage(charResources[CHARACTER.getGraphics()], drawX, drawY, this);
			for (int i = 0; rCharData != null && i < rCharData.length; i++) {
				int dx = rCharData[i][0] - rx + drawX;
				int dy = rCharData[i][1] - ry + drawY;
				int qX = dx / COMPRESSION;
				int qY = dy / COMPRESSION;
				if (rdata[qX][qY] != 0 && dx % COMPRESSION > COMPRESSION / 2)
					g.drawImage(worldResources[rdata[qX][qY]], qX * COMPRESSION, qY * COMPRESSION, this);
				g.drawImage(charResources[rCharData[i][2]], dx, dy, this);
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
