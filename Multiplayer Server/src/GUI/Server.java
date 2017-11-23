package GUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Stack;

import Framework.World;
import Server.RemoteProcessServer;

public class Server implements Runnable {
	private int MAXCONNECTIONS, PORT, PROTOCOL_VERSION, TILE_SIZE, COMPRESSION, MAX_REFRESH_RATE;
	private String TOKEN;
	public JLogArea log;
	public ConnectionTable clients;
	private final Stack<Integer> idStack;
	public final HashSet<String> active;
	public final World STARTING_WORLD;

	public Server(String path, ConnectionTable clients, String worldPath) throws IOException {
		BufferedReader worldRead = new BufferedReader(new FileReader(new File(worldPath)));
		int width = Integer.parseInt(worldRead.readLine());
		int height = Integer.parseInt(worldRead.readLine());
		int compress = Integer.parseInt(worldRead.readLine());
		int[][] data = new int[width][height];
		String[] input;
		for (int i = 0; i < height / compress; i++) {
			input = worldRead.readLine().split(" ");
			for (int j = 0; j < width / compress; j++) {
				data[j][i] = Integer.parseInt(input[j]);
			}
		}
		worldRead.close();
		STARTING_WORLD = new World(width, height, data, compress);
		this.clients = clients;
		idStack = new Stack<Integer>();
		active = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		String[] in;
		while (br.ready()) {
			in = br.readLine().split("=");
			switch (in[0]) {
			case "maxConnections":
				MAXCONNECTIONS = Integer.parseInt(in[1]);
				break;
			case "port":
				PORT = Integer.parseInt(in[1]);
				break;
			case "token":
				TOKEN = in[1];
				break;
			case "protocolVersion":
				PROTOCOL_VERSION = Integer.parseInt(in[1]);
				break;
			case "tileSize":
				TILE_SIZE = Integer.parseInt(in[1]);
				break;
			case "compression":
				COMPRESSION = Integer.parseInt(in[1]);
				break;
			case "maxRefreshRate":
				MAX_REFRESH_RATE = Integer.parseInt(in[1]);
				break;
			}
		}
		br.close();
	}

	public void setLog(JLogArea log) {
		this.log = log;
	}

	public void open() throws IOException {
		ServerSocket serverSocket = new ServerSocket(PORT);
		for (int i = MAXCONNECTIONS - 1; i >= 0; i--) {
			idStack.push(i);
		}
		while (true)
			while (!idStack.isEmpty()) {
				log.log(LogMessageType.SERVER, "Waiting for connection....");
				Socket sock = serverSocket.accept();
				log.log(LogMessageType.SERVER, "Begining Conection to:" + sock.getInetAddress().getHostAddress());
				Connection c = new Connection(idStack.pop(), sock.getInetAddress().getHostAddress(), "", 0, "");
				RemoteProcessServer rps = new RemoteProcessServer(sock, this, c, MAX_REFRESH_RATE, TOKEN, PROTOCOL_VERSION, COMPRESSION, TILE_SIZE);
				clients.getConnectionTableModel().c.add(c);
				connectionUpdate();
				log.log(LogMessageType.SERVER, "Handle object created for " + sock.getInetAddress().getHostAddress());
				Thread t = new Thread(rps);
				t.start();
				log.log(LogMessageType.SERVER, "Connection to " + sock.getInetAddress().getHostAddress() + " passed on");
			}

	}

	public void remove(Connection c) {
		for (int i = 0; i < clients.getConnectionTableModel().c.size(); i++) {
			if (clients.getConnectionTableModel().c.get(i).ID == c.ID) {
				clients.getConnectionTableModel().c.remove(i);
				break;
			}
		}
		active.remove(c.USERNAME);
		idStack.push(c.ID);
		connectionUpdate();
	}

	public void connectionUpdate() {
		clients.getConnectionTableModel().fireTableDataChanged();
	}

	@Override
	public void run() {
		try {
			open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
