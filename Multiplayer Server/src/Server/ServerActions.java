package Server;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import Framework.Char;
import Log.LogMessageType;

public class ServerActions implements Runnable, Closeable {

	public boolean updateResources = true;
	private ServerFunctions f;
	private Socket socket;
	private final int PROTOCOL_VERSION, MAX_REFRESH_RATE;
	public Connection connection;
	public final Server SERVER;
	private boolean serverStop = false;

	/**
	 * Temporary Code For Testing Char Object will most likely be moved to another
	 * Location World Code also for testing and will be moved later
	 */
	public Char CHARACTER;

	public ServerActions(Socket socket, Server s, Connection connection, int mrr, String t, int pv) {
		/**
		 * Temporary Stuff
		 */
		MAX_REFRESH_RATE = mrr;
		this.socket = socket;
		SERVER = s;
		this.connection = connection;
		PROTOCOL_VERSION = pv;
		try {

			f = new ServerFunctions(this, socket, t, PROTOCOL_VERSION);
			// Make sure everything is up to date
			if (!f.verifyToken())
				f.close();
			if (!f.verifyProtocolVersion())
				f.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		try {

			f.waitForLogin();
			SERVER.clients.add(connection);
			CHARACTER = new Char(connection.USERNAME, 0);// Index value will be inputed later
			CHARACTER.setWorld(SERVER.WORLDS.get("STARTING WORLD"));
			connection.setChar(CHARACTER);
			long time;
			f.writeGraphic();
			f.charGraphics(Char.CHAR_PIC);
			f.projectileGraphics();
			while (true) {
				f.dataUpdate(updateResources);
				if (updateResources) {
					f.writeResources(connection.CHAR.getWorld().getResources());
					updateResources = false;
				}
				time = System.currentTimeMillis();
				f.writeCharacter();
				f.getCharacterMove();
				f.terraintRequest();
				while (System.currentTimeMillis() - time < MAX_REFRESH_RATE);
				connection.REFRESH_RATE = System.currentTimeMillis() - time;
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			if (!serverStop)
				SERVER.log.log(LogMessageType.SERVER, "Connection to " + socket.getInetAddress().getHostAddress() + " Terminated from client side");
			try {
				close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		SERVER.remove(connection);
		serverStop = true;
		f.close();
		connection.STATUS = false;
	}
}
