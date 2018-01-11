package Client;

import java.awt.MouseInfo;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import Framework.Displayable;
import GUI.Render;

public class ServerInteractions implements Runnable {

	private final String TOKEN = "Token";
	private int[] info;
	private Render r;
	private double angle = 0;

	private ArrayList<Displayable> display;
	private Functions f;
	public int xMove, yMove;
	public boolean mouseDown = false;

	public boolean attemptLogin(String adress, int port, String username, char[] password) throws IOException, NoSuchAlgorithmException {
		boolean result;
		try {
			f = new Functions(adress, port);
			f.writeTokenMessage(TOKEN);
			f.writeProtocolVersionMessage();
			byte[] bytes = new String(password).getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(bytes);
			BigInteger foo = new BigInteger(hash);
			f.loginRequest(username, foo.toString(16));
			result = f.loginStatus();
		} finally {
			if (f == null) {
				return false;
			}
		}
		return result;
	}

	public void setRender(Render r) {
		this.r = r;
	}

	public void setChar() {
		r.setChar(f.c);
	}

	public void update() {
		try {
			info = f.getGraphic();
			r.setData(info[0], info[1]);
			r.setResources(f.charGraphics(), 0);
			r.setResources(f.projectileGraphics(), 2);
			while (true) {
				if (f.dataUpdate())
					r.setResources(f.getResources(), 1);
				f.getCharacter();
				if (r.getMousePosition() != null) {
					try {
						angle = Math.atan((r.getMiddleY() - r.getMousePosition().getY()) / (r.getMousePosition().getX() - r.getMiddleX()));
						angle = angle < 0 ? angle + Math.PI : angle;
						angle = r.getMiddleY() - r.getMousePosition().getY() < 0 ? angle + Math.PI : angle;
						angle += Math.PI / 2;
					} catch (NullPointerException e) {

					}
				}
				f.moveCharacter(xMove, yMove, angle, mouseDown);
				r.display = f.requestTerrain(r.getWidth(), r.getHeight());
				r.x = getX();
				r.y = getY();
				r.repaint();
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Connection To Server Lost", "Warning", JOptionPane.ERROR_MESSAGE);
		}
	}

	public int getX() {
		return f.c.getX();
	}

	public int getY() {
		return f.c.getY();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		update();
	}

}
