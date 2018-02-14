package view;

import javax.swing.*;
import java.awt.Color;
import java.awt.event.ActionListener;
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
			break;
		case "SERVER_MODE":
			mode = SessionMode.SERVER;
			break;
		case "SHOW_COLORPICKER":
			selectedColor = JColorChooser.showDialog(this,
					"Välj din textfärg", Color.BLACK);
			selectColorButton.setBackground(selectedColor);
			break;
		case "START":
			attemptStart();
			break;
		default:
			System.err.println("LobbyWindow: unknown Action Command: "
								+ e.getActionCommand());
		}
	}
	
	private void attemptStart() {
		/* Collect information */
		/* Create session controller */
		/* Create window */
		/* dispose */
	}
	
}
