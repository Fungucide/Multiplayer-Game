package GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Framework.Char;
import Framework.Displayable;

public class Render extends JPanel {

	BufferedImage bi;

	private int TILE_SIZE;
	private int COMPRESSION;
	private Char CHARACTER;
	private int HALF_PLAYER_SIZE;

	public ArrayList<Displayable> display;
	public int x, y;
	private int middleX, middleY;
	private BufferedImage[] worldResources, charResources;
	private BufferedImage[][] resources=new BufferedImage[4][];

	public Render() {
		super();
		try {
			bi = ImageIO.read(new File("C:/Users/William/Desktop/Multiplayer/Multiplayer-Game/Multiplayer Server/Data/Server/Resources/Projectile1.png"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		setDoubleBuffered(true);
		display = new ArrayList<Displayable>();
	}

	public void setData(int tileSize, int compression) {
		TILE_SIZE = tileSize;
		COMPRESSION = compression;
		middleX = getWidth() / 2;
		middleY = getHeight() / 2;
	}

	public void setResources(BufferedImage[][] resources) {
		this.resources = resources;
	}

	public void setResources(BufferedImage[] resources, int type) {
		this.resources[type] = resources;
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

		Graphics2D g2d = (Graphics2D) g;

		if (COMPRESSION == 0 || TILE_SIZE == 0)
			return;

		int xOff = -(x % COMPRESSION);
		int yOff = -(y % COMPRESSION);
		if (worldResources != null && charResources != null && CHARACTER != null) {

			for (int i = 0; i * TILE_SIZE <= getWidth() + TILE_SIZE; i++) {
				for (int j = 0; j * TILE_SIZE <= getHeight() + TILE_SIZE; j++) {
					g2d.drawImage(worldResources[0], xOff + i * TILE_SIZE, yOff + j * TILE_SIZE, this);
				}
			}

			display.add(new Displayable(0, x, y, Char.playerSize(), Char.playerSize(), 0, CHARACTER.getGraphics(), CHARACTER.getFrame()));
			Collections.sort(display);
			for (Displayable d : display) {
				if (d.getType() == 0)// Player
					g2d.drawImage(charResources[d.getGraphics()[0]], d.getX() - Char.playerSize() - x + middleX, d.getY() - Char.playerSize() - y + middleY, this);
				else if (d.getType() == 1) {// Terrain
					g2d.drawImage(worldResources[d.getGraphics()[0]], d.getX() - d.getHalfWidth() - x + middleX + 3, d.getY() - d.getHalfHeight() - y + middleY - 5, this);
				} // else if (d.getType() == 2) {// Projectile... This is going to be fun
				AffineTransform at = new AffineTransform();
				at.translate(getWidth() / 2, getHeight() / 2);
				at.rotate(1);
				at.translate(-bi.getWidth() / 2, -bi.getHeight() / 2);

				g2d.drawImage(bi, at, this);
				// }
			}
		}

		g2d.drawOval(getWidth() / 2, getHeight() / 2, 1, 1);
		g2d.setColor(Color.DARK_GRAY);
		g2d.fillOval(0, 0, 75, 75);
		g2d.fillRoundRect(37, 37, 150, 38, 5, 5);
	}

	public int getCompression() {
		return COMPRESSION;
	}

}
