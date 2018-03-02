package view;

import javax.swing.*;

import crypto.CryptoType;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import session.SessionController;
import session.ChatObserver;

public class ClientUserInfo extends AbstractUserInfo implements ChatObserver {

	private String myNameString = "";
	
	public ClientUserInfo(SessionWindow sw, SessionController sc) {
		super(sw, sc);
		myNameString = "Du heter: " + session.getMyUsername();
		updateView();
	}
	
	public String getSessionInfoText() {
		StringBuilder infoString = new StringBuilder(myNameString);
		if (usernameList.getSize() > 0) {
			int serverID = usernameList.get(0).getValue();
			String serversName = usernameList.get(0).getName();
			infoString.append("\nDu är uppkopplad mot ");
			if (!serversName.isEmpty()) {
				infoString.append(serversName);
				infoString.append('@');
			}
			infoString.append(session.getUserByID(serverID).getInetAdress(true));
		}
		return infoString.toString();
	}
}
