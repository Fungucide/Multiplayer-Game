package Command;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextField;

import Log.JLogArea;
import Log.LogMessageType;
import Server.Server;

public class CommandLine extends JTextField {

	private String command;
	private final JLogArea logArea;
	private final Server server;
	private final ArrayList<String> prev;
	private final int MAX_MEM = 100;
	private int idx = -1;
	private String cur = "";

	public CommandLine(JLogArea logArea, Server server) {
		super();
		this.logArea = logArea;
		this.server = server;
		prev = new ArrayList<String>();
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				ArrayList<String> seg = new ArrayList<String>();
				try {
					if (arg0.getKeyCode() == KeyEvent.VK_UP) {
						if (idx == -1) {
							cur = getText();
							idx++;
							setText(prev.get(prev.size() - 1 - idx));
						} else if (idx < prev.size() - 1) {
							idx++;
							setText(prev.get(prev.size() - 1 - idx));
						}
					} else if (arg0.getKeyCode() == KeyEvent.VK_DOWN) {
						if (idx == 0) {
							setText(cur);
							idx--;
						} else if (idx >= 0) {
							idx--;
							setText(prev.get(prev.size() - 1 - idx));
						}
					} else if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
						command = getText();
						prev.add(command);
						while (prev.size() > MAX_MEM)
							prev.remove(0);
						setText("");
						idx = -1;
						cur = "";
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
								if (seg.contains("/id") || seg.contains("-id"))
									server.disconnect(Integer.parseInt(seg.get(2)));
								else
									server.disconnect(getID(seg.get(1)));
								break;
							case "/close":
								server.close();
								break;
							case "/loadworld":
								server.loadWorld(seg.get(1), seg.get(2));
								break;
							case "/setworld":
								if (seg.contains("/id") || seg.contains("-id"))
									server.setWorld(Integer.parseInt(seg.get(1)), seg.get(2));
								else
									server.setWorld(getID(seg.get(1)), seg.get(2));
								break;
							case "/clear":
								logArea.clear();
								break;
							case "/listusers":
								server.listUsers();
								break;
							case "/listworlds":
								server.listWorlds();
								break;
							case "/tp":
								if (seg.contains("-id") || seg.contains("/id"))
									server.tp(Integer.parseInt(seg.get(2)), Integer.parseInt(seg.get(3)), Integer.parseInt(seg.get(4)));
								else
									server.tp(getID(seg.get(1)), Integer.parseInt(seg.get(2)), Integer.parseInt(seg.get(3)));
								break;
							case "/dummy":
								if (seg.get(1).contains("-add") || seg.get(1).contains("/add"))
									server.addDummy(seg.get(2));
								else if (seg.get(1).equals("-remove") || seg.get(1).equals("/remove"))
									server.removeDummy(seg.get(2));
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

	private int getID(String username) {
		return server.clients.usernaeToID(username);
	}

}
