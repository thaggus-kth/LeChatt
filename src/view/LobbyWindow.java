package view;

import javax.swing.*;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class LobbyWindow extends JFrame implements ActionListener {
	
	private Color selectedColor;
	private JButton selectColorButton;
	private JButton startButton;
	private JTextField nameEntry;
	private JTextField ipEntry;
	private JTextField portEntry;
	private SessionMode mode;
	
	private enum SessionMode {
		CLIENT,
		SERVER;
	}
	
	/**
	 * A small subclass of Exception to keep track of attemptStart not
	 * having enough information to start.
	 * @author thaggus
	 *
	 */
	private class MyException extends Exception {
		MyException(String s) {
			super(s);
		}
	}
	
	public LobbyWindow() {
		super("LeChatt Lobby");
		JPanel row1 = new JPanel();
		JPanel row2 = new JPanel();
		JPanel row3 = new JPanel();
		JPanel row4 = new JPanel();
		ButtonGroup radioButtons = new ButtonGroup();
		JRadioButton clientRadioBtn = new JRadioButton("Klient");
		JRadioButton serverRadioBtn = new JRadioButton("Server");
		nameEntry = new JTextField(20);
		ipEntry = new JTextField(15);
		portEntry = new JTextField(4);
		selectColorButton = new JButton("Välj färg...");
		startButton = new JButton("Start");
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BoxLayout(getContentPane(),
											BoxLayout.PAGE_AXIS));
		
		row1.add(new JLabel("Användarnamn:"));
		nameEntry.setEditable(true);
		row1.add(nameEntry);
		selectColorButton.setActionCommand("SHOW_COLORPICKER");
		selectColorButton.addActionListener(this);
		row1.add(selectColorButton);
		add(row1);
		
		clientRadioBtn.setActionCommand("CLIENT_MODE");
		clientRadioBtn.addActionListener(this);
		serverRadioBtn.setActionCommand("SERVER_MODE");
		serverRadioBtn.addActionListener(this);
		radioButtons.add(clientRadioBtn);
		radioButtons.add(serverRadioBtn);
		clientRadioBtn.doClick();
		row2.add(clientRadioBtn);
		row2.add(serverRadioBtn);
		add(row2);
		
		row3.add(new JLabel("IP"));
		ipEntry.setEditable(true);
		row3.add(ipEntry);
		row3.add(new JLabel("Port"));
		portEntry.setEditable(true);
		row3.add(portEntry);
		add(row3);
		
		startButton.setActionCommand("START");
		startButton.addActionListener(this);
		row4.add(startButton);
		add(row4);
		
		//TODO: :) setIconImage(image);
		pack();
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "CLIENT_MODE":
			mode = SessionMode.CLIENT;
			ipEntry.setEnabled(true);
			break;
		case "SERVER_MODE":
			mode = SessionMode.SERVER;
			ipEntry.setEnabled(false);
			ipEntry.setText("");
			break;
		case "SHOW_COLORPICKER":
			selectedColor = JColorChooser.showDialog(this,
					"Välj din textfärg", Color.BLACK);
			selectColorButton.setBackground(selectedColor);
			break;
		case "START":
			try {
				attemptStart();
			} catch (MyException err) {
				System.err.println(err);
			}
			break;
		default:
			System.err.println("LobbyWindow: unknown Action Command: "
								+ e.getActionCommand());
		}
	}
	
	private void attemptStart() throws MyException {
		/* Collect information */
		String name = nameEntry.getText();
		String ip = ipEntry.getText();
		int port;
		
		/* Validate */
		try {
			 port = Integer.valueOf(portEntry.getText());
		} catch (NumberFormatException e) {
			throw new MyException("Please specify a valid port no.");
		}
		if (name.isEmpty()) {
			throw new MyException("Please enter a username.");
			/* Maybe it would be smart to use an eventlistener to
			 * keep the start button disabled until all fields are non-empty?
			 */
		}
		if (mode == SessionMode.CLIENT && ip.isEmpty()) {
			throw new MyException("Please enter an ip.");
		}
		if (selectedColor == null) {
			throw new MyException("Please select a color");
		}
		String greeting = "hej"; //TODO (long term): collect from user
		
		switch (mode) {
		case CLIENT:
			MainController.newClientSession(name, ip, port, selectedColor,
					greeting);
		case SERVER:
			try {
				MainController.newServerSession(name, port, selectedColor);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Could not open server "
					+ "socket on port " + port + ": " + e.getMessage(),
					"Error when starting server", JOptionPane.ERROR_MESSAGE);
			}
		}
		/* Hide this window */
		dispose();
	}
	
}
