package GUI;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextField;

import Framework.World;

public class CommandLine extends JTextField {

	private String command;
	private final JLogArea logArea;
	private final Server server;

	public CommandLine(JLogArea logArea, Server server) {
		super();
		this.logArea = logArea;
		this.server = server;
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				ArrayList<String> seg = new ArrayList<String>();
				try {
					if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
						command = getText();
						setText("");
						if (command.startsWith("/") && !command.startsWith("//")) {
							Matcher m = Pattern.compile("\"([^\"]*)\"|(\\S+)").matcher(command);
							while (m.find()) {
								if (m.group(1) != null) {
									seg.add(m.group(1));
								} else {
									seg.add(m.group(2));
								}
							}
							switch (seg.get(0).toLowerCase()) {
							case "/disconnect":
								disconnect(seg.get(1));
								break;
							case "/close":
								server.close();
								break;

							case "/loadworld":
								loadWorld(seg.get(1), seg.get(2));
								break;
							case "/setworld":
								setWorld(seg.get(1), seg.get(2));
								break;
							default:
								logArea.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.ERROR }, "No Such Command \"" + seg.get(0).substring(1) + "\" Exists");
								break;
							}
						} else {
							logArea.log(LogMessageType.MESSAGE, command);
						}
					}
				} catch (IndexOutOfBoundsException e) {
					logArea.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.ERROR }, "No enought arguments for command \"" + seg.get(0).substring(1) + "\"");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void disconnect(String username) {
		Connection c = server.get(username);
		if (c != null) {
			try {
				c.rps.close();
				logArea.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.DISCONNECT }, "User " + username + " connection closed successful");
			} catch (IOException e) {
				logArea.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.DISCONNECT, LogMessageType.ERROR }, "User " + username + " not found");
			}
		} else
			logArea.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.DISCONNECT, LogMessageType.ERROR }, "User " + username + " connection closed unsuccessful");
	}

	public void loadWorld(String name, String path) {
		File f = new File(path);
		if (!f.exists()) {
			logArea.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.LOAD_WORLD, LogMessageType.ERROR }, " File " + path + " not found");
			return;
		} else if (server.WORLDS.containsKey(name)) {
			logArea.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.LOAD_WORLD, LogMessageType.ERROR }, " World " + name + " exists already");
			return;
		}
		World w;
		try {
			w = new World(path, server.MAX_WORLD_UPDATE);
		} catch (IOException e) {
			logArea.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.LOAD_WORLD, LogMessageType.ERROR }, " Error reading file " + path);
			return;
		}
		server.WORLDS.put(name, w);
		logArea.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.LOAD_WORLD }, " World " + name + " loaded sucessfully");
	}

	public void setWorld(String user, String world) {
		Connection c = server.get(user);
		World w = server.WORLDS.get(world);
		if (c == null) {
			logArea.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.SET_WORLD, LogMessageType.ERROR }, " User with username " + user + " not found");
			return;
		} else if (w == null) {
			logArea.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.SET_WORLD, LogMessageType.ERROR }, " World " + world + " does not exist");
			return;
		}

		c.c.setWorld(w);
		c.rps.updateResources = true;
		logArea.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.SET_WORLD }, " User " + user + " successfully moved to world " + world);

	}
}
