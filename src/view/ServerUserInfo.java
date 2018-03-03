package view;

import java.awt.event.ActionEvent;
import javax.swing.*;
import session.Server;

public class ServerUserInfo extends AbstractUserInfo {

	JButton kickButton = new JButton("Sparka");
	private String sessionInfo;
	
	public ServerUserInfo(SessionWindow sw, Server sc) {
		super(sw, sc);
		sessionInfo = "You are running a server on:\n" + sc.getInetAdress(true);
		sessionInfo += "\nCurrently connected users:";
		buttonPanel.add(kickButton);
		kickButton.addActionListener(this);
		updateView();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == kickButton) {
			session.kickUser(selectedID);
		} else {
			kickButton.setEnabled(selectedID != NO_SELECTION);
			super.actionPerformed(e);
		}
	}

	@Override
	public String getSessionInfoText() {
		return sessionInfo;
	}

}
