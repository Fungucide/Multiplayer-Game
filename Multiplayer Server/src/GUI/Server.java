package GUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import javax.swing.JTextArea;

import Server.RemoteProcessServer;

public class Server implements Runnable {
	private int MAXCONNECTIONS, PORT, PROTOCOL_VERSION, TILE_SIZE, COMPRESSION;
	private String TOKEN;
	private JTextArea log;
	private ConnectionTable clients;
	private final Stack<Integer> idStack;
	public final HashSet<String> active;

	public ArrayList<RemoteProcessServer> connections = new ArrayList<RemoteProcessServer>();

	public Server(String path, ConnectionTable clients) throws IOException {
		this.clients = clients;
		idStack = new Stack<Integer>();
		active = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		String[] in;
		while (br.ready()) {
			in = br.readLine().split("=");
			switch (in[0]) {
			case "maxconnections":
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

			}
		}
	}

	public void setLog(JTextArea log) {
		this.log = log;
	}

	public void open() throws IOException {
		ServerSocket serverSocket = new ServerSocket(PORT);
		for (int i = MAXCONNECTIONS - 1; i >= 0; i--) {
			idStack.push(i);
		}
		while (true)
			while (connections.size() < MAXCONNECTIONS) {
				log.append("Waiting for connection....\n");
				Socket sock = serverSocket.accept();
				log.append("Begining Conection to:" + sock.getInetAddress().getHostAddress());
				Connection c = new Connection(idStack.pop(), sock.getInetAddress().getHostAddress(), "", 0, "");
				RemoteProcessServer rps = new RemoteProcessServer(sock, this, c, TOKEN, PROTOCOL_VERSION, COMPRESSION, TILE_SIZE);
				clients.getConnectionTableModel().c.add(c);
				clients.getConnectionTableModel().fireTableDataChanged();
				log.append("Handle object created for " + sock.getInetAddress().getHostAddress() + "\n");
				Thread t = new Thread(rps);
				t.start();
				log.append("Connection to " + sock.getInetAddress().getHostAddress() + " passed on\n");
			}

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
