package Server;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

import Framework.Char;
import Framework.Terrain;
import Framework.World;
import GUI.Connection;
import GUI.Server;

public class RemoteProcessServer implements Runnable {

	private Handle h;
	private final int PROTOCOL_VERSION, TILE_SIZE, COMPRESSION;
	public Connection connection;
	public final Server SERVER;

	/**
	 * Temporary Code For Testing Char Object will most likely be moved to another
	 * Location World Code also for testing and will be moved later
	 */
	private final int WORLD_X = 3000;
	private final int WORLD_Y = 3000;
	public Char c = new Char(0, 0, 100, 100, 10, 100, 100, 10, 5);
	public Terrain t;
	public World w;

	public RemoteProcessServer(Socket socket, Server s, Connection connection, String t, int pv, int c, int ts) {
		SERVER=s;
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

			// Temporary Stuff
			t = new Terrain(WORLD_X / COMPRESSION, WORLD_Y / COMPRESSION, COMPRESSION);
			w = new World(WORLD_X, WORLD_Y, t);
			Random r = new Random();
			for (int i = 0; i < WORLD_X / COMPRESSION; i++) {
				for (int j = 0; j < WORLD_Y / COMPRESSION; j++) {
					int num = r.nextInt() % 5;
					if (num == 4) {
						w.changeTerrain(i, j, 1);
					}
				}
			}

			c.setWorld(w);
			while (true) {
				h.writeCharacter();
				h.getCharacterMove();
				h.terraintRequest();
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			h.close();
			System.out.println("Connection Terminated from client side");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
