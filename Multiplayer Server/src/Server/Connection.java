package Server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;

import Framework.Char;
import GUI.PlayerDetailWindow;

public class Connection implements Closeable {
	public final Socket SOCKET;
	public final int ID;
	public final String ADDRESS;
	public long REFRESH_RATE;
	public String CHARACTER_CLASS, USERNAME;
	public final JButton BUTTON;
	public Char CHAR;
	public boolean STATUS = true;
	public ClientInteractions ci;

	public Connection(int id, String address, String username, long refreshRate, String characterClass, Socket socket) {
		ID = id;
		ADDRESS = address;
		USERNAME = username;
		REFRESH_RATE = refreshRate;
		CHARACTER_CLASS = characterClass;
		SOCKET = socket;
		BUTTON = new JButton();
		BUTTON.setText("Details");
		PlayerDetailWindow pdw = new PlayerDetailWindow(this);
		BUTTON.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pdw.setVisible(true);
				pdw.repaint();
			}
		});
	}

	public void setCI(ClientInteractions ci) {
		this.ci = ci;
	}

	public void setChar(Char c) {
		this.CHAR = c;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		SOCKET.close();
		CHAR.close();
		ci.close();
	}
}
