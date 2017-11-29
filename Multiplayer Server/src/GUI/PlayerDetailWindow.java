package GUI;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

public class PlayerDetailWindow extends JFrame {
	private final Connection c;
	private JLabel lblId;
	private JLabel lblAddress;
	private JLabel lblRefreshRate;
	private JLabel lblX;
	private JLabel lblY;
	private JLabel lblStatus;

	public PlayerDetailWindow(Connection c) {
		super();
		this.c = c;
		setBounds(100, 100, 450, 300);
		setTitle(c.USERNAME + " Information Window");
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);

		lblId = new JLabel("ID:");
		springLayout.putConstraint(SpringLayout.NORTH, lblId, 10, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblId, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblId);

		lblAddress = new JLabel("Address:");
		springLayout.putConstraint(SpringLayout.NORTH, lblAddress, 6, SpringLayout.SOUTH, lblId);
		springLayout.putConstraint(SpringLayout.WEST, lblAddress, 0, SpringLayout.WEST, lblId);
		getContentPane().add(lblAddress);

		lblRefreshRate = new JLabel("Refresh Rate:");
		springLayout.putConstraint(SpringLayout.NORTH, lblRefreshRate, 6, SpringLayout.SOUTH, lblAddress);
		springLayout.putConstraint(SpringLayout.WEST, lblRefreshRate, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblRefreshRate);
		
		lblX = new JLabel("X:");
		springLayout.putConstraint(SpringLayout.NORTH, lblX, 6, SpringLayout.SOUTH, lblRefreshRate);
		springLayout.putConstraint(SpringLayout.WEST, lblX, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(lblX);
		
		lblY = new JLabel("Y:");
		springLayout.putConstraint(SpringLayout.NORTH, lblY, 6, SpringLayout.SOUTH, lblX);
		springLayout.putConstraint(SpringLayout.WEST, lblY, 0, SpringLayout.WEST, lblId);
		getContentPane().add(lblY);
		
		lblStatus = new JLabel("Status:");
		springLayout.putConstraint(SpringLayout.WEST, lblStatus, 158, SpringLayout.EAST, lblId);
		springLayout.putConstraint(SpringLayout.SOUTH, lblStatus, 0, SpringLayout.SOUTH, lblId);
		getContentPane().add(lblStatus);

	}

	protected Connection getConnection() {
		return c;
	}

	@Override
	public void setVisible(boolean arg0) {
		setTitle(c.USERNAME + " Information Window");
		lblId.setText("ID: " + c.ID);
		lblAddress.setText("Address: " + c.ADDRESS);
		Update u = new Update(this);
		Thread t = new Thread(u);
		t.start();
		super.setVisible(arg0);
	}

	@Override
	public void repaint() {
		lblRefreshRate.setText("Refresh Rate: " + c.REFRESH_RATE);
		lblX.setText("X: "+c.c.getX());
		lblY.setText("Y: "+c.c.getY());
		if(c.STATUS) {
			lblStatus.setText("Status: Connected");
		}else {
			lblStatus.setText("Status: Disconnected");
		}
		super.repaint();
	}
}

class Update implements Runnable {

	PlayerDetailWindow pdw;

	Update(PlayerDetailWindow pdw) {
		this.pdw = pdw;
	}

	@Override
	public void run() {
		long time;
		while (true) {
			time = System.currentTimeMillis();
			pdw.repaint();
			while (System.currentTimeMillis() - time < pdw.getConnection().REFRESH_RATE);
		}
	}

}
