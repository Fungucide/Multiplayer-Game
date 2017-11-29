package GUI;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import java.awt.Canvas;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main {

	private JFrame frame;
	private JTextField textFieldServerAdress;
	private JTextField textFieldUsername;
	private JPasswordField textFieldPassword;
	private ServerInteractions si;
	private ArrayList<Component> login;
	private JLabel lblServerAdress;
	private JLabel lblUsername;
	private JLabel lblPassowrd;
	private Render render;
	private SpringLayout springLayout;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
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

		si = new ServerInteractions();

		frame = new JFrame();
		frame.setBounds(100, 100, 750, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);

		lblServerAdress = new JLabel("Server Adress:");
		springLayout.putConstraint(SpringLayout.WEST, lblServerAdress, 100, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(lblServerAdress);

		textFieldServerAdress = new JTextField("localhost");
		springLayout.putConstraint(SpringLayout.WEST, textFieldServerAdress, 200, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, lblServerAdress, 0, SpringLayout.NORTH, textFieldServerAdress);
		springLayout.putConstraint(SpringLayout.SOUTH, lblServerAdress, 0, SpringLayout.SOUTH, textFieldServerAdress);
		springLayout.putConstraint(SpringLayout.EAST, lblServerAdress, -10, SpringLayout.WEST, textFieldServerAdress);
		springLayout.putConstraint(SpringLayout.EAST, textFieldServerAdress, -100, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, textFieldServerAdress, 140, SpringLayout.NORTH, frame.getContentPane());
		frame.getContentPane().add(textFieldServerAdress);
		textFieldServerAdress.setColumns(10);

		lblUsername = new JLabel("Username:");
		springLayout.putConstraint(SpringLayout.NORTH, lblUsername, 10, SpringLayout.SOUTH, lblServerAdress);
		springLayout.putConstraint(SpringLayout.WEST, lblUsername, 0, SpringLayout.WEST, lblServerAdress);
		springLayout.putConstraint(SpringLayout.EAST, lblUsername, 0, SpringLayout.EAST, lblServerAdress);
		frame.getContentPane().add(lblUsername);

		textFieldUsername = new JTextField("Admin");
		springLayout.putConstraint(SpringLayout.SOUTH, lblUsername, 0, SpringLayout.SOUTH, textFieldUsername);
		springLayout.putConstraint(SpringLayout.NORTH, textFieldUsername, 10, SpringLayout.SOUTH, textFieldServerAdress);
		springLayout.putConstraint(SpringLayout.WEST, textFieldUsername, 0, SpringLayout.WEST, textFieldServerAdress);
		springLayout.putConstraint(SpringLayout.EAST, textFieldUsername, 0, SpringLayout.EAST, textFieldServerAdress);
		textFieldUsername.setColumns(10);
		frame.getContentPane().add(textFieldUsername);

		lblPassowrd = new JLabel("Password:");
		springLayout.putConstraint(SpringLayout.NORTH, lblPassowrd, 10, SpringLayout.SOUTH, lblUsername);
		springLayout.putConstraint(SpringLayout.WEST, lblPassowrd, 0, SpringLayout.WEST, lblUsername);
		springLayout.putConstraint(SpringLayout.EAST, lblPassowrd, 0, SpringLayout.EAST, lblUsername);
		frame.getContentPane().add(lblPassowrd);

		textFieldPassword = new JPasswordField();
		textFieldPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					login();
				}
			}
		});
		springLayout.putConstraint(SpringLayout.SOUTH, lblPassowrd, 0, SpringLayout.SOUTH, textFieldPassword);
		springLayout.putConstraint(SpringLayout.NORTH, textFieldPassword, 10, SpringLayout.SOUTH, textFieldUsername);
		springLayout.putConstraint(SpringLayout.WEST, textFieldPassword, 0, SpringLayout.WEST, textFieldUsername);
		springLayout.putConstraint(SpringLayout.EAST, textFieldPassword, 0, SpringLayout.EAST, textFieldUsername);
		frame.getContentPane().add(textFieldPassword);
		textFieldPassword.setColumns(10);

		JButton btnLogin = new JButton("Login");
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				login();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnLogin, 50, SpringLayout.SOUTH, textFieldPassword);
		springLayout.putConstraint(SpringLayout.EAST, btnLogin, 0, SpringLayout.EAST, textFieldServerAdress);
		frame.getContentPane().add(btnLogin);

		login = new ArrayList<Component>();
		login.add(lblServerAdress);
		login.add(textFieldServerAdress);
		login.add(lblUsername);
		login.add(textFieldUsername);
		login.add(lblPassowrd);
		login.add(textFieldPassword);
		login.add(btnLogin);

		addRender();
	}

	private void hide(ArrayList<Component> al) {
		for (Component c : al)
			c.setVisible(false);
	}

	private void show(ArrayList<Component> al) {
		for (Component c : al)
			c.setVisible(true);
	}

	private void login() {
		try {
			if (si.attemptLogin(textFieldServerAdress.getText(), 31001, textFieldUsername.getText(), textFieldPassword.getPassword())) {
				hide(login);
				render.setVisible(true);
				render.repaint();
				run();
			} else {
				JOptionPane.showMessageDialog(frame, "Login Failed", "Login Error", JOptionPane.ERROR_MESSAGE);
			}
		} catch (NoSuchAlgorithmException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void run() {

		/*RenderUpdate cu = new RenderUpdate(render, 50);
		Thread cut = new Thread(cu);
		cut.start();*/

		si.setRender(render);
		Thread t = new Thread(si);
		t.start();

		render.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_W) {
					si.yMove = -1;
				} else if (e.getKeyCode() == KeyEvent.VK_S) {
					si.yMove = 1;
				}
				if (e.getKeyCode() == KeyEvent.VK_D) {
					si.xMove = 1;
				} else if (e.getKeyCode() == KeyEvent.VK_A) {
					si.xMove = -1;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_S) {
					si.yMove = 0;
				}
				if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D) {
					si.xMove = 0;
				}
			}
		});
		render.setFocusable(true);
	}

	private void addRender() {
		render = new Render();
		render.setVisible(false);
		springLayout.putConstraint(SpringLayout.NORTH, render, 0, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, render, 0, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, render, 0, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, render, 0, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(render);
	}
}
