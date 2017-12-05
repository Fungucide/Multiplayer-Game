package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import Server.Server;

public class Main {

	private JFrame frmConnectionManager;
	private JTabbedPane tabbedPane;
	private JPanel logPanel;
	private JPanel connectionPanel;
	private CommandLine commandLine;
	private JScrollPane logScrollPane;
	private JLogArea logTextArea;
	private Server s;
	private JScrollPane connectionScrollPane;
	private ConnectionTable connectionTable;
	private ConnectionTableModel tm;
	private JMenuBar menuBar;
	private FilterCheckBox[] filters;
	private FilterTypeCheckBox typeBox;
	private boolean[] filter;
	private JMenu filterMenu;
	private int type = 0;

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

		connectionScrollPane = new JScrollPane();
		logScrollPane = new JScrollPane();
		frmConnectionManager = new JFrame();
		tm = new ConnectionTableModel();
		connectionTable = new ConnectionTable(tm);
		logTextArea = new JLogArea();
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		logPanel = new JPanel();
		connectionPanel = new JPanel();
		menuBar = new JMenuBar();
		filterMenu = new JMenu("Filter");
		filters = new FilterCheckBox[LogMessageType.values().length];
		filter = new boolean[filters.length];
		logTextArea.setFilter(filter);

		try {
			s = new Server("Data/Server/Server.dat", connectionTable,logTextArea);
			Thread t = new Thread(s);
			t.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		commandLine = new CommandLine(logTextArea, s);

		frmConnectionManager.setTitle("Connection Manager");
		frmConnectionManager.setBounds(100, 100, 450, 300);
		frmConnectionManager.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmConnectionManager.getContentPane().setLayout(new BorderLayout(0, 0));

		frmConnectionManager.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		tabbedPane.addTab("Log", null, logPanel, null);
		SpringLayout sl_logPanel = new SpringLayout();
		logPanel.setLayout(sl_logPanel);

		sl_logPanel.putConstraint(SpringLayout.WEST, commandLine, 10, SpringLayout.WEST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.SOUTH, commandLine, -10, SpringLayout.SOUTH, logPanel);
		sl_logPanel.putConstraint(SpringLayout.EAST, commandLine, -10, SpringLayout.EAST, logPanel);
		logPanel.add(commandLine);
		commandLine.setColumns(10);

		sl_logPanel.putConstraint(SpringLayout.NORTH, logScrollPane, 10, SpringLayout.NORTH, logPanel);
		sl_logPanel.putConstraint(SpringLayout.WEST, logScrollPane, 10, SpringLayout.WEST, logPanel);
		sl_logPanel.putConstraint(SpringLayout.SOUTH, logScrollPane, -10, SpringLayout.NORTH, commandLine);
		sl_logPanel.putConstraint(SpringLayout.EAST, logScrollPane, -10, SpringLayout.EAST, logPanel);
		logPanel.add(logScrollPane);

		logTextArea.setEditable(false);
		logScrollPane.setViewportView(logTextArea);
		
		tabbedPane.addTab("Connections", null, connectionPanel, null);
		connectionPanel.setLayout(new BorderLayout(0, 0));

		connectionPanel.add(connectionScrollPane, BorderLayout.CENTER);

		connectionScrollPane.setViewportView(connectionTable);
		connectionTable.getTableHeader().getColumnModel().getColumn(0).setHeaderValue("ID");
		connectionTable.getTableHeader().getColumnModel().getColumn(1).setHeaderValue("Adress");
		connectionTable.getTableHeader().getColumnModel().getColumn(2).setHeaderValue("Username");
		connectionTable.getTableHeader().getColumnModel().getColumn(3).setHeaderValue("Refresh Rate");
		connectionTable.getTableHeader().getColumnModel().getColumn(4).setHeaderValue("Character Class");
		connectionTable.getTableHeader().getColumnModel().getColumn(5).setHeaderValue("Details");
		connectionTable.setButtonRenderer();

		for (int i = 0; i < filters.length; i++) {
			filter[i] = true;
			filters[i] = new FilterCheckBox(LogMessageType.values()[i].toString(), filter, logTextArea);
			filters[i].setSelected(true);
			filterMenu.add(filters[i]);
		}

		logTextArea.updateLog(true);
		
		typeBox = new FilterTypeCheckBox("Exact", logTextArea, type);
		filterMenu.addSeparator();
		filterMenu.add(typeBox);

		menuBar.add(filterMenu);
		logScrollPane.setColumnHeaderView(menuBar);

	}
}
