package GUI;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class ConnectionTableModel extends AbstractTableModel {

	public ArrayList<Connection> c;

	public ConnectionTableModel() {
		c = new ArrayList<Connection>();
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
