package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import session.*;

public class SessionWindow extends JFrame implements ChatObserver,
												ActionListener, KeyListener {
	
	private SessionController mySession;
	private JButton sendButton;
	private JButton sendFileButton;
	private JPanel userInfo;
	private JTextPane view;
	private JTextArea writeArea;
	private JFileChooser fileChooser;
	
	public SessionWindow(SessionController sc) {
		JPanel southPanel = new JPanel();
		JPanel sendButtons = new JPanel();
		JMenuItem menuItem;
		JMenu menu;
		JMenuBar menuBar = new JMenuBar();
		mySession = sc;
		view = new JTextPane(sc.getChatLog());
		userInfo = new JPanel();
		userInfo.setPreferredSize(new Dimension(200, 400));
		sendButton = new JButton("Skicka");
		sendFileButton = new JButton("Skicka fil...");
		writeArea = new TextAreaWithHint("Skriv ett meddelande här...");
		fileChooser = new JFileChooser("Välj vilken fil du vill skicka...");
		
		setLayout(new BorderLayout());
		view.setPreferredSize(new Dimension(600,400));
		view.setEditable(false);
		view.setContentType("text/html");
		add(view, BorderLayout.CENTER);
		add(userInfo, BorderLayout.EAST);
		southPanel.setLayout(new BorderLayout());
		southPanel.add(writeArea, BorderLayout.CENTER);
		sendButtons.add(sendButton);
		sendButtons.add(sendFileButton);
		southPanel.add(sendButtons, BorderLayout.EAST);
		add(southPanel, BorderLayout.SOUTH);
		add(menuBar, BorderLayout.NORTH);
		
		sendButton.setActionCommand("SEND_MESSAGE");
		sendButton.addActionListener(this);
		sendButton.setEnabled(false);
		writeArea.addKeyListener(this);
		sendFileButton.setActionCommand("SEND_FILE");
		
		menu = new JMenu("Anslutning");
		menuItem = new JMenuItem("Ny session...");
		menuItem.setActionCommand("NEW SESSION");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem = new JMenuItem("Avsluta session");
		menuItem.setActionCommand("EXIT");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuBar.add(menu);
		
		sc.addObserver(this);
		
		setTitle("Le Chatt");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
	}
	@Override
	public void updateView() {
		view.setDocument(mySession.getChatLog());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		switch (e.getActionCommand()) {
		case "NEW SESSION":
			MainController.showLobbyWindow();
			break;
		case "SEND_MESSAGE":
			attemptSendMessage();
			break;
		case "SEND_FILE":
			showFileChooserWindow();
			break;
		case "EXIT":
			dispose();
			break;
		default:
			System.err.println("SessionWindow: unknown Action Command: "
								+ e.getActionCommand());
		}
	}
	
	private void attemptSendMessage() {
		if (writeArea.getText() != "") {
			mySession.sendTextMessage(writeArea.getText());
			writeArea.setText("");
		} else {
			//TODO: spela fel-ljud
		}
	}
	
	private void showFileChooserWindow() {
		int usersChoice = fileChooser.showOpenDialog(this);
		if (usersChoice == JFileChooser.APPROVE_OPTION) {
			//TODO: get neccessary info. probably we want to use a whole JFrame for this, and put the
			// file chooser button in it (similar to the lobby window)
			String serverUser = mySession.getUsernameList().get(0);
			//mySession.sendFileRequest(serverUser, fileChooser.getSelectedFile(), c, message);
		}
	}
	
	@Override
	public void dispose() {
		mySession.disconnect();
		super.dispose();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		sendButton.setEnabled(!writeArea.getText().isEmpty());
	}
	@Override
	public void keyPressed(KeyEvent e) {		
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (!e.isShiftDown()) {
				attemptSendMessage();
				e.consume();
			}
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		/* Do nothing */
	}
}
