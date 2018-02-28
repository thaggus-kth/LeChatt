package view;

import java.awt.event.*;
import javax.swing.*;
import session.*;

public class RequestPopupWindow extends JDialog
								implements Request.RequestObserver,
											ActionListener {
	private Request myRequest;
	private SessionWindow myParent;
	private JOptionPane optionPane;
	private final String yesOption = "Acceptera";
	private final String noOption = "Neka";
	private final String cancelOption = "Avbryt";
	private JTextField input = new TextFieldWithHint(
			"Skriv ett svarsmeddelande...", 28);

	/**
	 * Creates a popup window representing the passed request.
	 * @param toShow The request to represent.
	 */
	RequestPopupWindow(SessionWindow parent, Request toShow) {
		super(parent, "Request");
		String info = getRequestInfo(toShow);
		JLabel hint = new JLabel("Svarsmeddelande (valfritt):");
		JButton yesButton = new JButton(yesOption);
		JButton noButton = new JButton(noOption);
		JButton cancelButton = new JButton(cancelOption);
		Object[] components = {info, hint, input};
		Object[] options = {yesButton, noButton, cancelButton};
		myRequest = toShow;
		
		optionPane = new JOptionPane(components, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.YES_NO_OPTION, null, options);
		setContentPane(optionPane);
		yesButton.addActionListener(this);
		noButton.addActionListener(this);
		cancelButton.addActionListener(this);
		//optionPane.addPropertyChangeListener(this);
		pack();
	}

	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case yesOption:
			myRequest.accept(input.getText());
			dispose();
			break;
		case noOption:
			myRequest.deny(input.getText());
			dispose();
			break;
		case cancelOption:
			dispose();
			break;
		}
	}
	private String getRequestInfo(Request r) {
		StringBuilder sb = new StringBuilder();
		if (r instanceof ConnectionRequest) {
			sb.append("Användaren ");
			sb.append(r.getUsername());
			sb.append(" vill ansluta och hälsar:\n\"");
			sb.append(r.getMessage());
			sb.append("\"\n");
		//} else if (r instanceof KeyRequest) {
			//lägg till saker
		} else {
			sb.append("Okänd request\nFrån: ");
			try {
				sb.append(r.getUsername());
			} catch (NullPointerException e) {
				sb.append("{null}");
			}
			sb.append("\nMeddelande: ");
			sb.append(r.getMessage());
		}
		return sb.toString();
	}
	@Override
	public void timedOut() {
		if (isVisible()) {
			JOptionPane.showMessageDialog(this, "Request timed out!",
					"Timeout", JOptionPane.WARNING_MESSAGE);
			dispose();
		}
	}

	@Override
	public void killed() {
		if (isVisible()) {
			dispose();
		}
	}
}
