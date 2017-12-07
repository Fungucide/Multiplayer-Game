package Server;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import Framework.Char;
import Framework.World;
import GUI.Connection;
import GUI.ConnectionTable;
import Log.JLogArea;
import Log.LogMessageType;

public class Server implements Runnable, Closeable {
	private int MAXCONNECTIONS, PORT, PROTOCOL_VERSION, TILE_SIZE, COMPRESSION, MAX_REFRESH_RATE;
	private ServerSocket serverSocket;
	protected long MAX_WORLD_UPDATE;
	private String TOKEN, CHAR_RESOURCES;
	public JLogArea log;
	public ConnectionTable clients;
	private final Stack<Integer> idStack;
	public final HashSet<String> active;
	public final HashMap<String, World> WORLDS;
	public final ArrayList<String> WORLD_NAMES;

	public Server(String path, ConnectionTable clients, JLogArea log) throws IOException {
		this.clients = clients;
		this.log = log;
		idStack = new Stack<Integer>();
		active = new HashSet<String>();
		WORLDS = new HashMap<String, World>();
		WORLD_NAMES = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));
		String worldPath = "";
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
			case "playerSize":
				Char.PLAYER_SIZE = Integer.parseInt(in[1]);
				break;
			case "maxRefreshRate":
				MAX_REFRESH_RATE = Integer.parseInt(in[1]);
				break;
			case "maxWorldUpdate":
				MAX_WORLD_UPDATE = Long.parseLong(in[1]);
				break;
			case "startWorld":
				worldPath = in[1];
				WORLDS.put("STARTING WORLD", new World(worldPath, MAX_WORLD_UPDATE));
				WORLD_NAMES.add("STARTING WORLD");
				break;
			case "loadWorld":
				loadWorld(in[1], in[2]);
				break;
			case "charResources":
				CHAR_RESOURCES = in[1];
				charResources();
				break;
			case "charData":
				Char.PATH = in[1];
				break;
			}
		}
		br.close();
	}

	private void charResources() throws IOException {
		Files.walk(Paths.get(CHAR_RESOURCES)).forEach(filePath -> {
			if (Files.isRegularFile(filePath)) {
				String name = filePath.getFileName().toString();
				Char.CHAR_PIC.put(name.substring(0, name.indexOf('.')), Char.CHAR_PIC_AL.size());
				Char.CHAR_PIC_AL.add(filePath.toString());
			}
		});
	}

	public void open() throws IOException {
		try {
		serverSocket = new ServerSocket(PORT);
		for (int i = MAXCONNECTIONS - 1; i >= 0; i--) {
			idStack.push(i);
		}
		while (true)
			while (!idStack.isEmpty()) {
				log.log(LogMessageType.SERVER, "Waiting for connection....");
				Socket sock = serverSocket.accept();
				log.log(LogMessageType.SERVER, "Begining Conection to:" + sock.getInetAddress().getHostAddress());
				Connection c = new Connection(idStack.pop(), sock.getInetAddress().getHostAddress(), "", 0, "", sock);
				ClientInteractions ci = new ClientInteractions(sock, this, c, MAX_REFRESH_RATE, TOKEN, PROTOCOL_VERSION, COMPRESSION, TILE_SIZE);
				c.setCI(ci);
				clients.getConnectionTableModel().c.add(c);
				connectionUpdate();
				log.log(LogMessageType.SERVER, "Handle object created for " + sock.getInetAddress().getHostAddress());
				Thread t = new Thread(ci);
				t.start();
				log.log(LogMessageType.SERVER, "Connection to " + sock.getInetAddress().getHostAddress() + " passed on");
			}
		}catch(SocketException e) {
			log.log(LogMessageType.SERVER, "Server Socket Successfully Closed");
		}
	}

	public boolean remove(Connection c) {
		boolean res = false;
		for (int i = 0; i < clients.getConnectionTableModel().c.size(); i++) {
			if (clients.getConnectionTableModel().c.get(i).ID == c.ID) {
				clients.getConnectionTableModel().c.remove(i);
				res = true;
				break;
			}
		}
		active.remove(c.USERNAME);
		idStack.push(c.ID);
		connectionUpdate();
		return res;
	}

	public Connection getByUsername(String username) {
		for (int i = 0; i < clients.getConnectionTableModel().c.size(); i++) {
			if (clients.getConnectionTableModel().c.get(i).USERNAME.equals(username)) {
				return clients.getConnectionTableModel().c.get(i);
			}
		}
		return null;
	}

	public Connection getByID(int ID) {
		for (int i = 0; i < clients.getConnectionTableModel().c.size(); i++) {
			if (clients.getConnectionTableModel().c.get(i).ID == ID) {
				return clients.getConnectionTableModel().c.get(i);
			}
		}
		return null;
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

	public void disconnect(int ID) {
		Connection c = getByID(ID);
		if (c != null) {
			try {
				c.ci.close();
				remove(c);
				log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.DISCONNECT }, "User " + c.USERNAME + " connection closed successful");
			} catch (IOException e) {
				log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.DISCONNECT, LogMessageType.ERROR }, "User " + c.USERNAME + " connection closed unsuccessful");
			}
		} else {
			log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.DISCONNECT, LogMessageType.ERROR }, "User with ID " + ID + " not found");
		}
	}

	public void disconnect(String username) {
		Connection c = getByUsername(username);
		if (c != null) {
			try {
				c.close();
				remove(c);
				log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.DISCONNECT }, "User " + username + " connection closed successful");
			} catch (IOException e) {
				log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.DISCONNECT, LogMessageType.ERROR }, "User " + username + " not found");
			}
		} else
			log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.DISCONNECT, LogMessageType.ERROR }, "User " + username + " connection closed unsuccessful");
	}

	public void loadWorld(String name, String path) {
		File f = new File(path);
		if (!f.exists()) {
			log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.LOAD_WORLD, LogMessageType.ERROR }, " File " + path + " not found");
			return;
		} else if (WORLDS.containsKey(name)) {
			log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.LOAD_WORLD, LogMessageType.ERROR }, " World " + name + " exists already");
			return;
		}
		World w;
		try {
			w = new World(path, MAX_WORLD_UPDATE);
		} catch (IOException e) {
			log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.LOAD_WORLD, LogMessageType.ERROR }, " Error reading file " + path);
			return;
		}
		WORLDS.put(name, w);
		WORLD_NAMES.add(name);
		log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.LOAD_WORLD }, " World " + name + " loaded sucessfully");
	}

	public void setWorld(String user, String world) {
		Connection c = getByUsername(user);
		World w = WORLDS.get(world);
		if (c == null) {
			log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.SET_WORLD, LogMessageType.ERROR }, " User with username " + user + " not found");
			return;
		} else if (w == null) {
			log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.SET_WORLD, LogMessageType.ERROR }, " World " + world + " does not exist");
			return;
		}

		c.c.setWorld(w);
		c.ci.updateResources = true;
		log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.SET_WORLD }, " User " + user + " successfully moved to world " + world);

	}

	public void listUsers() {
		String message = " " + clients.getConnectionTableModel().c.size() + " Users:";
		for (Connection c : clients.getConnectionTableModel().c) {
			message += "\n\tID: " + c.ID + "\t Username: " + c.USERNAME;
		}
		log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.LIST_USERS }, message);
	}

	public void listWorlds() {
		String message = " " + WORLD_NAMES.size() + " Worlds:";
		for (String name : WORLD_NAMES) {
			message += "\n\t" + name;
		}
		log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.LIST_WORLDS }, message);
	}

	@Override
	public void close() throws IOException {
		log.log(LogMessageType.SERVER, "Begining to disconnect " + clients.getConnectionTableModel().c.size() + " users");
		for (Connection c : clients.getConnectionTableModel().c) {
			c.close();
			log.log(LogMessageType.SERVER, "User " + c.USERNAME + " disconnected");
		}
		clients.getConnectionTableModel().c.clear();
		log.log(LogMessageType.SERVER, "User List Cleared");
		serverSocket.close();
		while (!idStack.empty())
			idStack.pop();
	}

}
