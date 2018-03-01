package view;

import javax.swing.*;

import crypto.CryptoType;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import session.SessionController;
import session.ChatObserver;

public class ClientUserInfo extends JPanel implements ChatObserver {

	private static final String NO_ENCRYPTION_STRING = "Ingen";
	private static final String SEND_REQUEST_STRING = "Fler...";
	List<String> usernames;
	List<JPanel> rows; //TODO: we need to map usernames to rows.
	SessionController session;
	
	public ClientUserInfo(SessionController sc) {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(new JLabel("You are connected to ...")); //TODO: make this able to get the IP from sc for clients.
	}
	
	protected JPanel newRow(String username) {
		JPanel newRow = new JPanel();
		JLabel nameLabel = new JLabel(username);
		JComboBox<String> cryptoSelect = new JComboBox<String>();
		
		newRow.setLayout(new BoxLayout(newRow, BoxLayout.LINE_AXIS));
		newRow.add(new JLabel(username));
		newRow.add(cryptoSelect);
		
		cryptoSelect.addItem(NO_ENCRYPTION_STRING);
		for (CryptoType ct : session.getAvailableCryptos(username)) {
			cryptoSelect.addItem(ct.toString());
		}
		cryptoSelect.addItem(SEND_REQUEST_STRING);
		
		cryptoSelect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				switch (e.getActionCommand()) {
				case (NO_ENCRYPTION_STRING):
					session.setCrypto(username, CryptoType.PLAIN);
					break;
				case (SEND_REQUEST_STRING):
					//TODO: show RequestPopupWindow with an outgoing
					//key request
					break;
				default:
					session.setCrypto(username,
							CryptoType.valueOf(e.getActionCommand()));
				}
			}
		});
		
		return newRow;
	}
	
	private void updateUserList() {
		
		JPanel row = new JPanel();
		for (String)
			//TODO: what happens if there are multiple users with the
			// same username? Maybe we should check for this and kick
			// user if they try to connect with a taken name.
	}
	
	@Override
	public void updateView() {
		// TODO Auto-generated method stub
		
	}
	
}
