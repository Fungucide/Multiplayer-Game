package Server;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class RemoteProcessServer implements Runnable {

	private Handle h;

	public RemoteProcessServer(Socket socket) {

		try {

			h = new Handle(socket);
			// Make sure everything is up to date
			h.verifyToken();
			h.verifyProtocolVersion();
			h.writeGraphic();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		try {

			h.waitForLogin();
			h.c.setWorld(h.w);
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
