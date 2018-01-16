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
import Framework.Projectile;
import Framework.World;
import GUI.ConnectionTable;
import Log.JLogArea;
import Log.LogMessageType;

public class Server implements Runnable, Closeable {
	private int MAXCONNECTIONS, PORT, PROTOCOL_VERSION, MAX_REFRESH_RATE;
	private ServerSocket serverSocket;
	protected long MAX_WORLD_UPDATE;
	private String TOKEN, CHAR_RESOURCES, PROJECTILE_RESOURCES;
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
			case "playerSize":
				Char.setCharSize(Integer.parseInt(in[1]));
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
			case "projectileResources":
				PROJECTILE_RESOURCES = in[1];
				projectileResources();
				break;
			case "charData":
				Char.PATH = in[1];
				break;
			}
		}
		br.close();
	}

	private void charResources() throws IOException {
		CHAR_RESOURCES = CHAR_RESOURCES.replace("\\", "/");
		if (!CHAR_RESOURCES.endsWith("/"))
			CHAR_RESOURCES += "/";
		BufferedReader charReader = new BufferedReader(new FileReader(new File(CHAR_RESOURCES + "Sprites.dat")));
		int numOfSprites = Integer.parseInt(charReader.readLine());
		Char.CHAR_PIC = new String[numOfSprites][];
		String[] temp;
		for (int i = 0; i < numOfSprites; i++) {
			temp = charReader.readLine().split(" ");
			String name = temp[0];
			Char.CHAR_PIC[i] = new String[temp.length - 1];
			for (int j = 1; j < temp.length; j++) {
				Char.CHAR_PIC[i][j - 1] = CHAR_RESOURCES + temp[j];
				Char.CHAR_PIC_StoI.put(name, i);
				Char.CHAR_PIC_ItoS.put(i, name);
			}
		}
	}

	private void projectileResources() throws IOException {
		PROJECTILE_RESOURCES = PROJECTILE_RESOURCES.replace("\\", "/");
		if (!PROJECTILE_RESOURCES.endsWith("/"))
			PROJECTILE_RESOURCES += "/";
		BufferedReader projectileReader = new BufferedReader(new FileReader(new File(PROJECTILE_RESOURCES + "Sprites.dat")));
		int numOfSprites = Integer.parseInt(projectileReader.readLine());
		Projectile.PROJECTILE_PATHS = new String[numOfSprites][];
		String[] temp;
		for (int i = 0; i < numOfSprites; i++) {
			temp = projectileReader.readLine().split(" ");
			Projectile.PROJECTILE_PATHS[i] = new String[temp.length];
			for (int j = 0; j < temp.length; j++)
				Projectile.PROJECTILE_PATHS[i][j] = PROJECTILE_RESOURCES + temp[j];
		}
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
					ClientInteractions ci = new ClientInteractions(sock, this, c, MAX_REFRESH_RATE, TOKEN, PROTOCOL_VERSION);
					c.setCI(ci);
					connectionUpdate();
					log.log(LogMessageType.SERVER, "Handle object created for " + sock.getInetAddress().getHostAddress());
					Thread t = new Thread(ci);
					t.start();
					log.log(LogMessageType.SERVER, "Connection to " + sock.getInetAddress().getHostAddress() + " passed on");
				}
		} catch (SocketException e) {
			log.log(LogMessageType.SERVER, "Server Socket Successfully Closed");
		}
	}

	public boolean remove(Connection c) {
		if (clients.remove(c)) {
			active.remove(c.USERNAME);
			idStack.push(c.ID);
			connectionUpdate();
			return true;
		}
		return false;
	}

	public Connection getByID(int ID) {
		return clients.get(ID);
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
				c.close();
				remove(c);
				connectionUpdate();
				log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.DISCONNECT }, "User " + c.USERNAME + " connection closed successful");
			} catch (IOException e) {
				log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.DISCONNECT, LogMessageType.ERROR }, "User " + c.USERNAME + " connection closed unsuccessful");
			}
		} else
			log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.DISCONNECT, LogMessageType.ERROR }, "User with ID " + ID + " not found");
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

	public void setWorld(int ID, String world) {
		Connection c = getByID(ID);
		World w = WORLDS.get(world);
		if (c == null) {
			log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.SET_WORLD, LogMessageType.ERROR }, " User with ID " + ID + " not found");
			return;
		} else if (w == null) {
			log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.SET_WORLD, LogMessageType.ERROR }, " World " + world + " does not exist");
			return;
		}

		c.CHAR.setWorld(w);
		if (c.ci != null)
			c.ci.updateResources = true;
		log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.SET_WORLD }, " User " + c.USERNAME + " successfully moved to world " + world);
		connectionUpdate();

	}

	public void listUsers() {
		String message = " " + clients.getConnections().size() + " Users:";
		for (Connection c : clients.getConnections()) {
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

	public void tp(int ID, int x, int y) {
		Connection c = getByID(ID);
		if (c != null) {
			c.CHAR.tp(x, y);
			log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.TP }, "User " + c.USERNAME + " successfully tp to (" + x + "," + y + ")");
		} else {
			log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.TP, LogMessageType.ERROR }, "User with ID" + ID + " not found");
		}
	}

	public void addDummy(String name) {
		Char c = new Char("Dummy:" + name, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		c.setWorld(WORLDS.get("STARTING WORLD"));
		Connection con = new Connection(idStack.pop(), "N/A", "Dummy:" + name, 0, "Dummy", null);
		con.setChar(c);
		clients.add(con);
		connectionUpdate();
		log.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.DUMMY }, "Dummy:" + name + " successfully created");
	}

	public void removeDummy(String name) {
		disconnect(clients.usernaeToID("Dummy:" + name));
	}

	@Override
	public void close() throws IOException {
		log.log(LogMessageType.SERVER, "Begining to disconnect " + clients.getConnections().size() + " users");
		ArrayList<Connection> al = clients.getConnections();
		for (Connection c : al) {
			c.close();
			log.log(LogMessageType.SERVER, "User " + c.USERNAME + " disconnected");
		}
		clients.clear();
		log.log(LogMessageType.SERVER, "User List Cleared");
		serverSocket.close();
		while (!idStack.empty())
			idStack.pop();
	}

}
