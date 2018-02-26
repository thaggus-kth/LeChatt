package view;

import javax.swing.*;
import session.*;

public class RequestPopupWindow extends JDialog implements Request.RequestObserver {
	private Request myRequest;
	private SessionWindow myParent;
	private JOptionPane optionPane;
	/**
	 * Creates a popup window representing the passed request.
	 * @param toShow The request to represent.
	 */
	RequestPopupWindow(SessionWindow parent, Request toShow) {
		super(parent, "Request");
		String info = "information om requesten";
		JTextField input = new JTextField("Skriv ett svarsmeddelande...", 28);
		Object[] components = {info, input};
		String[] options = {"Acceptera", "Neka", "Avbryt"};
		optionPane = new JOptionPane(info, JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.YES_NO_OPTION, null, options);
		
		setContentPane(optionPane);
		pack();
	}

	@Override
	public void timedOut() {
		JOptionPane.showMessageDialog(this, "Request timed out!", "Timeout",
				JOptionPane.WARNING_MESSAGE);
		dispose();
	}

	@Override
	public void killed() {
		dispose();
	}
}
