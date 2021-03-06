package view;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import session.*;

public class SessionWindow extends JFrame implements ChatObserver,
												ActionListener, KeyListener, HyperlinkListener {
	
	private SessionController mySession;
	private JButton sendButton;
	private JButton sendFileButton;
	private AbstractUserInfo userInfo;
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
		sendButton = new JButton("Skicka");
		sendFileButton = new JButton("Skicka fil...");
		writeArea = new TextAreaWithHint("Skriv ett meddelande här...");
		fileChooser = new JFileChooser("Välj vilken fil du vill skicka...");
		
		if (sc instanceof Server) { 
			userInfo = new ServerUserInfo(this, (Server) sc);
		} else {
			userInfo = new ClientUserInfo(this, sc);
		}
		
		userInfo.setPreferredSize(new Dimension(200, 400));
		userInfo.setMinimumSize(new Dimension(200, 400));
		setLayout(new BorderLayout());
		view.setPreferredSize(new Dimension(600,400));
		view.setEditable(false);
		view.setContentType("text/html");
		view.setDocument(sc.getChatLog());
		view.addHyperlinkListener(this);
		add(new JScrollPane(view,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER),
				BorderLayout.CENTER);
		add(userInfo, BorderLayout.EAST);
		southPanel.setLayout(new BorderLayout());
		southPanel.add(new JScrollPane(writeArea, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER),
				BorderLayout.CENTER);
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
		sendFileButton.addActionListener(this);
		
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
		sendFileButton.setEnabled(
				userInfo.getSelectedID() != AbstractUserInfo.NO_SELECTION 
				&& mySession.userAvailableForFileRequest(
						userInfo.getSelectedID()));
		/* Scroll to bottom */
		view.setCaretPosition(view.getDocument().getLength());
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
			new SendRequestPopup(this, mySession,
					SendRequestPopup.SupportedRequests.FILE_REQUEST,
					userInfo.getSelectedID());
			break;
		case "EXIT":
			dispose();
			break;
		default:
			System.err.println("SessionWindow: unknown Action Command: "
								+ e.getActionCommand());
		}
	}
	
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			if (e.getDescription().matches("request:[0-9]+")) {
				/* This is a request */
				int requestID = Integer.valueOf(e.getDescription().
														split(":")[1]);
				Request r = mySession.getRequest(requestID);
				RequestPopupWindow popup = new RequestPopupWindow(this, r);
				popup.setVisible(true);
			} else if (e.getURL() != null) {
				/* This is a web link. Open the system's browser */
				URL clickedLink = e.getURL();
				try {
					boolean canBrowse = true;
					if (Desktop.isDesktopSupported()) {
						Desktop desktop = Desktop.getDesktop();
						if (desktop.isSupported(Desktop.Action.BROWSE)) {
							desktop.browse(clickedLink.toURI());
						} else {
							canBrowse = false;
						}
					} else {
						canBrowse = false;
					}
					if (!canBrowse) {
						//TODO: show error dialog
						System.out.println("Could not open your web browser.");
					}
				} catch (IOException | URISyntaxException e1) {
					//TODO: show error dialog
				}	
			}
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
