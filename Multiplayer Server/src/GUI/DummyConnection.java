package GUI;

import java.net.Socket;

public class DummyConnection extends Connection {

	public DummyConnection(int id, String address, String username, long refreshRate, String characterClass, Socket socket) {
		super(id, address, username, refreshRate, characterClass, socket);
	}

}
