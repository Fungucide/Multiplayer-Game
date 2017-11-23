package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import java.awt.FlowLayout;
import javax.swing.JList;
import javax.swing.JTable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main {

	private JFrame frmConnectionManager;
	private JTabbedPane tabbedPane;
	private JPanel logPanel;
	private JPanel connectionPanel;
	private JTextField commandLine;
	private JScrollPane logScrollPane;
	private JTextArea logTextArea;
	private Server s;
	private JScrollPane connectionScrollPane;
	private ConnectionTable connectionTable;
	private ConnectionTableModel tm;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frmConnectionManager.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		tm = new ConnectionTableModel();
		connectionTable = new ConnectionTable(tm);
		
		frmConnectionManager = new JFrame();
		frmConnectionManager.setTitle("Connection Manager");
		frmConnectionManager.setBounds(100, 100, 450, 300);
		frmConnectionManager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmConnectionManager.getContentPane().setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmConnectionManager.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		logPanel = new JPanel();
		tabbedPane.addTab("Log", null, logPanel, null);
		SpringLayout sl_logPanel = new SpringLayout();
		logPanel.setLayout(sl_logPanel);

		commandLine = new JTextField();
		sl_logPanel.putConstraint(SpringLayout.WEST, commandLine, 10, SpringLayout.WEST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.SOUTH, commandLine, -10, SpringLayout.SOUTH, logPanel);
		sl_logPanel.putConstraint(SpringLayout.EAST, commandLine, -10, SpringLayout.EAST, logPanel);
		logPanel.add(commandLine);
		commandLine.setColumns(10);

		logScrollPane = new JScrollPane();
		sl_logPanel.putConstraint(SpringLayout.NORTH, logScrollPane, 10, SpringLayout.NORTH, logPanel);
		sl_logPanel.putConstraint(SpringLayout.WEST, logScrollPane, 10, SpringLayout.WEST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.SOUTH, logScrollPane, -10, SpringLayout.NORTH, commandLine);
		sl_logPanel.putConstraint(SpringLayout.EAST, logScrollPane, -10, SpringLayout.EAST, logPanel);
		logPanel.add(logScrollPane);

		logTextArea = new JTextArea();
		logScrollPane.setViewportView(logTextArea);

		try {
			s = new Server("Data/Server/Server.dat",connectionTable);
			s.setLog(logTextArea);
			Thread t = new Thread(s);
			t.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		connectionPanel = new JPanel();
		tabbedPane.addTab("Connections", null, connectionPanel, null);
		connectionPanel.setLayout(new BorderLayout(0, 0));

		connectionScrollPane = new JScrollPane();
		connectionPanel.add(connectionScrollPane, BorderLayout.CENTER);

		connectionScrollPane.setViewportView(connectionTable);
		connectionTable.getTableHeader().getColumnModel().getColumn(0).setHeaderValue("ID");
		connectionTable.getTableHeader().getColumnModel().getColumn(1).setHeaderValue("Adress");
		connectionTable.getTableHeader().getColumnModel().getColumn(2).setHeaderValue("Username");
		connectionTable.getTableHeader().getColumnModel().getColumn(3).setHeaderValue("Refresh Rate");
		connectionTable.getTableHeader().getColumnModel().getColumn(4).setHeaderValue("Character Class");
		connectionTable.getTableHeader().getColumnModel().getColumn(5).setHeaderValue("Details");
		connectionTable.setButtonRenderer();

	}
}
