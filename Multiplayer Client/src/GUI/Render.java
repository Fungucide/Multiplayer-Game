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
		rx = x;
		ry = y;

		rCharData = charData;
		int xOff = -(rx % COMPRESSION);
		int yOff = -(ry % COMPRESSION);
		if (worldResources != null) {
			for (int i = 0; i * TILE_SIZE <= getWidth() + TILE_SIZE; i++) {
				for (int j = 0; j * TILE_SIZE <= getHeight() + TILE_SIZE; j++) {
					g.drawImage(worldResources[0], xOff + i * TILE_SIZE, yOff + j * TILE_SIZE, this);
				}
			}
			for(Displayable d:display) {
				if(d.getType()==0)//Player
					g.drawImage(charResources[d.getGraphics()[0]], d.getX(), d.getY(),this);
				else if(d.getType()==1)
					g.drawImage(worldResources, xOff, yOff, observer)
			}
		}
		g.setColor(Color.BLACK);
		for (int i = 1; i * COMPRESSION <= getWidth(); i++) {
			g.drawLine(i * COMPRESSION + xOff, 0, i * COMPRESSION + xOff, getHeight());
		}

		for (int i = 1; i * COMPRESSION <= getHeight(); i++) {
			g.drawLine(0, i * COMPRESSION + yOff, getWidth(), i * COMPRESSION + yOff);
		}

		if (charResources != null && CHARACTER != null) {

			g.drawImage(charResources[CHARACTER.getGraphics()], drawX, drawY, this);

			if (rdata[MIDDLE_Q_X + 1][MIDDLE_Q_Y + 2] != 0 && ry % COMPRESSION > COMPRESSION / 2)// Below Player
				g.drawImage(worldResources[rdata[MIDDLE_Q_X + 1][MIDDLE_Q_Y + 2]], xOff + MIDDLE_Q_X * COMPRESSION, yOff + (MIDDLE_Q_Y + 1) * COMPRESSION + Y_OFF, this);
			if (rdata[MIDDLE_Q_X + 2][MIDDLE_Q_Y + 2] != 0 && ry % COMPRESSION > COMPRESSION / 2)// To the right? of the player
				g.drawImage(worldResources[rdata[MIDDLE_Q_X + 2][MIDDLE_Q_Y + 2]], xOff + (MIDDLE_Q_X + 1) * COMPRESSION, yOff + (MIDDLE_Q_Y + 1) * COMPRESSION + Y_OFF, this);
			if (rdata[MIDDLE_Q_X][MIDDLE_Q_Y + 2] != 0 && ry % COMPRESSION > COMPRESSION / 2)// To the left? of the player
				g.drawImage(worldResources[rdata[MIDDLE_Q_X][MIDDLE_Q_Y + 2]], xOff + (MIDDLE_Q_X - 1) * COMPRESSION, yOff + (MIDDLE_Q_Y + 1) * COMPRESSION + Y_OFF, this);

			for (int i = 0; rCharData != null && i < rCharData.length; i++) {
				int dX = rCharData[i][0] - rx + drawX+5;
				int dY = rCharData[i][1] - ry + drawY+5;
				dX=dX-dX%COMPRESSION+xOff;
				dY=dY-dY%COMPRESSION+yOff;
				int qX = (dX) / COMPRESSION + 1;
				int qY = (dY) / COMPRESSION + 1;
				g.drawImage(charResources[rCharData[i][2]], dX, dY, this);

				if (qX != lXq || qY != lYq) {
					lXq = qX;
					lYq = qY;
					//System.out.println("Cord: " + dX + "," + dY);
					//System.out.println("Quad:" + qX + " " + qY + "\n");
				}

				g.drawImage(worldResources[1], xOff + qX * COMPRESSION, yOff + (qY + 1) * COMPRESSION + Y_OFF, this);

				/*
				 * int thing = rCharData[i][1]; //System.out.println(rdata[qX + 1][qY + 2] + " "
				 * + (thing % COMPRESSION) + " " + (COMPRESSION / 2)); if (rdata[qX + 1][qY + 2]
				 * != 0 && (thing) % COMPRESSION > COMPRESSION / 2)// Below Player
				 * g.drawImage(worldResources[rdata[qX + 1][qY + 2]], xOff + qX * COMPRESSION,
				 * yOff + (qY + 1) * COMPRESSION + Y_OFF, this); if (rdata[qX + 2][qY + 2] != 0
				 * && (thing) % COMPRESSION > COMPRESSION / 2)// To the right? of the player
				 * g.drawImage(worldResources[rdata[qX + 2][qY + 2]], xOff + (qX + 1) *
				 * COMPRESSION, yOff + (qY + 1) * COMPRESSION + Y_OFF, this); if (rdata[qX][qY +
				 * 2] != 0 && (thing) % COMPRESSION > COMPRESSION / 2)// To the left? of the
				 * player g.drawImage(worldResources[rdata[qX][qY + 2]], xOff + (qX - 1) *
				 * COMPRESSION, yOff + (qY + 1) * COMPRESSION + Y_OFF, this);
				 */
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
