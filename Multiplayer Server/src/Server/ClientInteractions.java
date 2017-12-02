package Server;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import Framework.Char;
import GUI.Connection;
import GUI.LogMessageType;
import GUI.Server;

public class ClientInteractions implements Runnable, Closeable {

	public boolean updateResources = true;
	private Functions f;
	private Socket socket;
	private final int PROTOCOL_VERSION, TILE_SIZE, COMPRESSION, MAX_REFRESH_RATE;
	public Connection connection;
	public final Server SERVER;
	private boolean serverStop = false;

	/**
	 * Temporary Code For Testing Char Object will most likely be moved to another
	 * Location World Code also for testing and will be moved later
	 */
	public Char CHARACTER;

	public ClientInteractions(Socket socket, Server s, Connection connection, int mrr, String t, int pv, int c, int ts) {
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

			f = new Functions(this, socket, t, PROTOCOL_VERSION, COMPRESSION, TILE_SIZE);
			// Make sure everything is up to date
			if (!f.verifyToken())
				f.close();
			if (!f.verifyProtocolVersion())
				f.close();
			f.writeGraphic();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		try {

			f.waitForLogin();
			CHARACTER = new Char(connection.USERNAME, 0);// Index value will be inputed later
			CHARACTER.setWorld(SERVER.STARTING_WORLD);
			connection.setChar(CHARACTER);
			long time;
			while (true) {
				f.dataUpdate(updateResources);
				if (updateResources)
					f.writeResources(connection.c.w.getResources(), connection.c.w.getType());
				time = System.currentTimeMillis();
				f.writeCharacter();
				f.getCharacterMove();
				f.terraintRequest();
				while (System.currentTimeMillis() - time < MAX_REFRESH_RATE)
					;
				connection.REFRESH_RATE = System.currentTimeMillis() - time;
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			f.close();
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			SERVER.remove(connection);
			if (!serverStop)
				SERVER.log.log(LogMessageType.SERVER, "Connection to " + socket.getInetAddress().getHostAddress() + " Terminated from client side");
			connection.STATUS = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		serverStop = true;
		f.close();
		socket.close();
		SERVER.remove(connection);
		connection.STATUS = false;
	}
}
