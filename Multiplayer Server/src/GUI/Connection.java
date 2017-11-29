package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import Framework.Char;

public class Connection {
	public final int ID;
	public final String ADDRESS;
	public long REFRESH_RATE;
	public String CHARACTER_CLASS, USERNAME;
	public final JButton BUTTON;
	public Char c;
	public boolean STATUS= true;

	public Connection(int id, String address, String username, long refreshRate, String characterClass) {
		ID = id;
		ADDRESS = address;
		USERNAME = username;
		REFRESH_RATE = refreshRate;
		CHARACTER_CLASS = characterClass;
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

	public void setChar(Char c) {
		this.c = c;
	}
}
