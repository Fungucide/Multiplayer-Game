package Client;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.JOptionPane;

import GUI.Render;

public class ServerInteractions implements Runnable {

	private final String TOKEN = "Token";
	private int[] info;
	private Render r;

	private Terrain terrain;
	private Functions f;
	public int xMove, yMove;

	public boolean attemptLogin(String adress, int port, String username, char[] password) throws IOException, NoSuchAlgorithmException {
		boolean result;
		try {
			f = new Functions(adress, port);
			f.writeTokenMessage(TOKEN);
			f.writeProtocolVersionMessage();
			info = f.getGraphic();
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
		this.r.setData(info[0], info[1]);
	}

	public void setChar() {
		r.setChar(f.c);
	}

	public void update() {
		try {
			r.setCharResources(f.charGraphics());
			while (true) {
				if (f.dataUpdate())
					r.setWorldResources(f.getResources());
				f.getCharacter();
				f.moveCharacter(xMove, yMove, 0, false);
				terrain = f.requestTerrain(r.getWidth() / r.getCompression() + 2, r.getHeight() / r.getCompression() + 1);
				r.x = getX();
				r.y = getY();
				r.data = terrain.data;
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
