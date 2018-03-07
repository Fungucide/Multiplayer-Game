package Client;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

import Framework.Displayable;
import GUI.Render;

public class ClientActions implements Runnable {

	private final String TOKEN = "Token";
	private int[] info;
	private Render r;
	private double angle = 0;

	private ArrayList<Displayable> display;
	private ClientFunctions f;
	public int xMove, yMove;
	public boolean mouseDown = false;

	public boolean attemptLogin(String adress, int port, String username, char[] password) throws IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		boolean result;
		try {
			f = new ClientFunctions(adress, port);
			f.writeTokenMessage(TOKEN);
			f.writeProtocolVersionMessage();
			byte[] bytes = new String(password).getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(bytes);
			f.loginRequest(username, hash);
			result = f.loginStatus();
			System.out.println(result);
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
