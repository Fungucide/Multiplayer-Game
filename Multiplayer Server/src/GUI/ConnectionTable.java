package GUI;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

public class ConnectionTable extends JTable {
	private ConnectionTableModel tm;

	public ConnectionTable(ConnectionTableModel tm) {
		super(tm);
		this.tm = tm;
		addMouseListener(new ConnectionTableButtonMouseListener(this));
	}

	public ConnectionTableModel getConnectionTableModel() {
		return tm;
	}

	public void setButtonRenderer() {
		getColumn("Details").setCellRenderer(new ConnectionTableButtonRenderer());
	}

}

class ConnectionTableButtonRenderer implements TableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		JButton button = (JButton) value;
		if (isSelected) {
			button.setForeground(table.getSelectionForeground());
			button.setBackground(table.getSelectionBackground());
		} else {
			button.setForeground(table.getForeground());
			button.setBackground(UIManager.getColor("Button.background"));
		}
		return button;
	}
}

class ConnectionTableButtonMouseListener extends MouseAdapter {
	private final ConnectionTable table;

	public ConnectionTableButtonMouseListener(ConnectionTable table) {
		this.table = table;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int column = table.getColumnModel().getColumnIndexAtX(e.getX());
		int row = e.getY() / table.getRowHeight();

		if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
			Object value = table.getValueAt(row, column);
			if (value instanceof JButton) {
				((JButton) value).doClick();
			}
		}
	}
}
