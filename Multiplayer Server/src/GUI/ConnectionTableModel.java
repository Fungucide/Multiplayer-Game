package GUI;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

import Server.Connection;

public class ConnectionTableModel extends AbstractTableModel {

	private ArrayList<Connection> c;
	private HashMap<String, Integer> userToID;
	private HashMap<Integer, Connection> connections;

	public ConnectionTableModel() {
		c = new ArrayList<Connection>();
		userToID = new HashMap<String, Integer>();
		connections = new HashMap<Integer, Connection>();
	}

	protected ArrayList<Connection> getConnections() {
		return c;
	}

	protected void clear() {
		c.clear();
		userToID.clear();
		connections.clear();
	}

	protected void add(Connection con) {
		c.add(con);
		userToID.put(con.USERNAME, con.ID);
		connections.put(con.ID, con);
	}

	protected boolean remove(Connection con) {
		if (c.remove(con) && userToID.remove(con.USERNAME, con.ID) && connections.remove(con.ID, con))
			return true;
		return false;

	}

	protected int usernaeToID(String user) {
		return userToID.get(user);
	}

	protected Connection get(int ID) {
		return connections.get(ID);
	}

	@Override
	public int getColumnCount() {
		// ID, Address, Username, Refresh Rate, Character Class, Details
		return 6;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return c.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return c.get(rowIndex).ID;
		case 1:
			return c.get(rowIndex).ADDRESS;
		case 2:
			return c.get(rowIndex).USERNAME;
		case 3:
			return c.get(rowIndex).REFRESH_RATE;
		case 4:
			return c.get(rowIndex).CHARACTER_CLASS;
		case 5:
			return c.get(rowIndex).BUTTON;
		}
		return null;
	}

}
