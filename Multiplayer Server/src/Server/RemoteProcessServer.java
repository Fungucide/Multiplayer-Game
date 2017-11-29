package Server;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import Framework.Char;
import GUI.Connection;
import GUI.LogMessageType;
import GUI.Server;

public class RemoteProcessServer implements Runnable {

	private Handle h;
	private Socket socket;
	private final int PROTOCOL_VERSION, TILE_SIZE, COMPRESSION, MAX_REFRESH_RATE;
	public Connection connection;
	public final Server SERVER;

	/**
	 * Temporary Code For Testing Char Object will most likely be moved to another
	 * Location World Code also for testing and will be moved later
	 */
	public Char CHARACTER;

	public RemoteProcessServer(Socket socket, Server s, Connection connection, int mrr, String t, int pv, int c, int ts) {
		/**
		 * Temporary Stuff
		 */
		MAX_REFRESH_RATE = mrr;
		this.socket = socket;
		SERVER = s;
		this.connection = connection;
		PROTOCOL_VERSION = pv;
		TILE_SIZE = ts;
		COMPRESSION = c;
		try {

			h = new Handle(this, socket, t, PROTOCOL_VERSION, COMPRESSION, TILE_SIZE);
			// Make sure everything is up to date
			if (!h.verifyToken())
				h.close();
			if (!h.verifyProtocolVersion())
				h.close();
			h.writeGraphic();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		try {

			h.waitForLogin();
			CHARACTER = new Char(connection.USERNAME, 0);// Index value will be inputed later
			CHARACTER.setWorld(SERVER.STARTING_WORLD);
			connection.setChar(CHARACTER);
			long time;
			h.writeResources(SERVER.STARTING_WORLD.getResources(),SERVER.STARTING_WORLD.getType());
			while (true) {
				time = System.currentTimeMillis();
				h.writeCharacter();
				h.getCharacterMove();
				h.terraintRequest();
				while (System.currentTimeMillis() - time < MAX_REFRESH_RATE);
				connection.REFRESH_RATE = System.currentTimeMillis() - time;
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			h.close();
			SERVER.remove(connection);
			SERVER.log.log(LogMessageType.SERVER, "Connection to " + socket.getInetAddress().getHostAddress() + " Terminated from client side\n");
			connection.STATUS=false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
