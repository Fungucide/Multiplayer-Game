package GUI;

import java.io.IOException;
import java.math.BigInteger;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.JOptionPane;

import Client.RemoteProcessClient;
import Client.Terrain;

public class ServerInteractions implements Runnable {

	private final String TOKEN = "Token";
	private int[] info;
	private Render r;

	private Terrain terrain;
	private RemoteProcessClient rpc;
	public int xMove, yMove;

	public boolean attemptLogin(String adress, int port, String username, char[] password) throws IOException, NoSuchAlgorithmException {
		boolean result;
		try {
			rpc = new RemoteProcessClient(adress, port);
			rpc.writeTokenMessage(TOKEN);
			rpc.writeProtocolVersionMessage();
			info = rpc.getGraphic();
			byte[] bytes = new String(password).getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(bytes);
			BigInteger foo = new BigInteger(hash);
			rpc.loginRequest(username, foo.toString(16));
			result = rpc.loginStatus();
		} finally {
			if (rpc == null) {
				return false;
			}
		}
		return result;
	}

	public void setRender(Render r) {
		this.r = r;
		this.r.setData(info[0], info[1]);
	}

	public void update() {
		while (true) {
			try {
				if (rpc.dataUpdate())
					r.setResources(rpc.getResources());
				rpc.getCharacter();
				rpc.moveCharacter(xMove, yMove, 0, false);
				terrain = rpc.requestTerrain(r.getWidth() / r.getCompression() + 2, r.getHeight() / r.getCompression() + 1);
				r.x = getX();
				r.y = getY();
				r.data = terrain.data;
				r.repaint();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Connection To Server Lost", "Warning", JOptionPane.ERROR_MESSAGE);
				break;
			}
		}
	}

	public int getX() {
		return rpc.c.getX();
	}

	public int getY() {
		return rpc.c.getY();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		update();
	}

}
