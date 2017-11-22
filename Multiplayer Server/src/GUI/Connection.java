package GUI;

import javax.swing.JButton;

public class Connection {
	public final int ID;
	public final String ADDRESS, USERNAME;
	public int REFRESH_RATE;
	public String CHARACTER_CLASS;
	public final JButton BUTTON;

	public Connection(int id, String address, String username, int refreshRate, String characterClass) {
		ID = id;
		ADDRESS = address;
		USERNAME = username;
		REFRESH_RATE = refreshRate;
		CHARACTER_CLASS = characterClass;
		BUTTON = new JButton();
		BUTTON.setText(USERNAME);
	}
}
