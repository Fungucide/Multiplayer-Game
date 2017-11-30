package GUI;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JTextField;

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
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					command = getText();
					setText("");
					if (command.startsWith("/") && !command.startsWith("//")) {
						String[] seg = command.split(" ");
						Connection c;
						switch (seg[0].toLowerCase()) {
						case "/close":
							c = server.get(seg[1]);
							if (c != null) {
								try {
									c.rps.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
								logArea.log(LogMessageType.COMMAND, "User " + seg[1] + " connection closed successful");
							} else {
								logArea.log(new LogMessageType[] { LogMessageType.COMMAND, LogMessageType.ERROR }, "User " + seg[1] + " connection closed unsuccessful");
							}
							break;

						default:
							break;
						}
					} else {
						logArea.log(LogMessageType.MESSAGE, command);
					}
				}
			}
		});
	}
}
