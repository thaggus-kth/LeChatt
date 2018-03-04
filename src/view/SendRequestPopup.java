package view;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.*;
import crypto.*;
import session.SessionController;
import file.Progressor;

/**
 * Popup window which collects all user info neccessary to send a 
 * file or key request, and allows the user to send them.
 * @author thaggus
 *
 */
public class SendRequestPopup extends JDialog implements ActionListener {

	private static final String NO_ENCRYPTION_STRING = "Ingen";
	private static final String CHOOSE_FILE_STRING = "Välj fil";
	private static final String FILE_LABEL_STRING = "Välj fil: ";
	private int myUserID;
	private String myUsersName;
	private SupportedRequests myType;
	private SessionController mySession;
	private JFrame myParent;
	private CryptoType selectedCryptoType = CryptoType.PLAIN;
	private JComboBox<String> cryptoSelection = new JComboBox<String>();
	private JButton sendButton = new JButton("Skicka");
	private JButton cancelButton = new JButton("Avbryt");
	private JLabel fileLabel;
	private JFileChooser fileChooser;
	private JTextField input =
			new TextFieldWithHint("Skriv ett meddelande...", 64);

	/**
	 * An enumeration of the requests supported by this class.
	 * @author thaggus
	 *
	 */
	public static enum SupportedRequests {
		FILE_REQUEST,
		KEY_REQUEST;
	}
	
	/**
	 * Use this constructor to show the user a new request dialog.
	 * @param parent the JFrame parent of this dialog
	 * @param session the active chat session
	 * @param type the type of request to send (@see {@link SupportedRequests})
	 * @param userID the ID of the user which we are considering sending
	 * the request to
	 */
	public SendRequestPopup(JFrame parent, SessionController session,
			SupportedRequests type, int userID) {
		super(parent, "Skicka ny request");
		JOptionPane optionPane;
		ArrayList<Object> components = new ArrayList<Object>();
		Object[] options = {sendButton, cancelButton};
		JPanel cryptoRow = new JPanel();
		JPanel fileRow;
		JButton fileChooserButton;
		myUserID = userID;
		myType = type;
		mySession = session;
		myParent = parent;
		myUsersName = session.getUsernamesAndIDs().get(myUserID);
		
		components.add(makeInfoText());
		switch (myType) {
		case FILE_REQUEST:
			fileLabel = new JLabel(FILE_LABEL_STRING);
			fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(false);
			fileChooserButton = new JButton(CHOOSE_FILE_STRING);
			fileChooserButton.addActionListener(this);
			fileRow = new JPanel();
			fileRow.add(fileLabel);
			fileRow.add(fileChooserButton);
			components.add(fileRow);
			cryptoRow.add(new JLabel("Använd krypto:"));
			sendButton.setEnabled(false);
			break;
		case KEY_REQUEST:
			cryptoRow.add(new JLabel("Fråga om krypto:"));
			break;
		}
		cryptoRow.add(cryptoSelection);
		components.add(cryptoRow);
		setupCryptoSelectionBox();
		components.add(input);
		cancelButton.addActionListener(this);
		sendButton.addActionListener(this);
		optionPane = new JOptionPane(components.toArray(), JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION, null, options);
		setContentPane(optionPane);
		pack();
		setVisible(true);
	}
	
	/**
	 * Creates the description to show the user based on request type.
	 * @return description
	 */
	private String makeInfoText() {
		String r = "";
		switch (myType) {
		case FILE_REQUEST:
			r = "Du vill skicka en fil till " + myUsersName;
			break;
		case KEY_REQUEST:
			r = "Du vill skicka en förfrågan om kryptering till " + myUsersName;
			break;
		}
		return r;
	}
	
	/**
	 * Performs setup for the crypto selection box.
	 */
	private void setupCryptoSelectionBox() {
		for (CryptoType ct : CryptoType.values()) {
			if (ct == CryptoType.PLAIN
					&& myType != SupportedRequests.KEY_REQUEST) {
				cryptoSelection.addItem(NO_ENCRYPTION_STRING);
			} else if (myType == SupportedRequests.FILE_REQUEST) {
				if (mySession.checkCryptoAvailable(myUserID, ct)) {
					cryptoSelection.addItem(ct.toString());
				}
			} else if (ct != CryptoType.PLAIN && 
					myType == SupportedRequests.KEY_REQUEST){
				cryptoSelection.addItem(ct.toString());
			}
		}
		selectedCryptoType = CryptoType.valueOf(
					(String) cryptoSelection.getSelectedItem());
		cryptoSelection.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (cryptoSelection.getSelectedItem() == NO_ENCRYPTION_STRING) {
					selectedCryptoType = CryptoType.PLAIN;
				} else {
					selectedCryptoType = CryptoType.valueOf(
							(String) cryptoSelection.getSelectedItem());
				}
			}
			
		});
	}
	
	@Override
	/**
	 * Monitors the buttons of this dialog. Sends request if Send button
	 * was clicked.
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == CHOOSE_FILE_STRING) {
			if (fileChooser.showOpenDialog(this) 
					== JFileChooser.APPROVE_OPTION) {
				fileLabel.setText(fileChooser.getSelectedFile().getName());
				sendButton.setEnabled(true);
			} else if (fileChooser.getSelectedFile() == null) {
				fileLabel.setText(FILE_LABEL_STRING);
				sendButton.setEnabled(false);
			}
		} else if (e.getSource() == sendButton) {
			if (myType == SupportedRequests.FILE_REQUEST) {
				if (fileChooser.getSelectedFile() == null) {
					JOptionPane.showMessageDialog(this, "Välj en fil!",
							"Fel", JOptionPane.ERROR_MESSAGE);
				} else {
					Progressor p = mySession.sendFileRequest(myUserID,
							fileChooser.getSelectedFile(),
							selectedCryptoType, input.getText());
					new FileProgressBarDialog(myParent, myUsersName,
							fileChooser.getSelectedFile().getName(),
							fileChooser.getSelectedFile().length(),
							p, true);
				}
				dispose();
			} else if (myType == SupportedRequests.KEY_REQUEST) {
				mySession.sendKeyRequest(myUserID, selectedCryptoType,
						input.getText());
				dispose();
			}
		} else if (e.getSource() == cancelButton) {
			dispose();
		}
	}
}
