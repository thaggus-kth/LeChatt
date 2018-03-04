package view;

import java.awt.event.*;
import java.text.DecimalFormat;

import javax.swing.*;
import session.*;

public class RequestPopupWindow extends JDialog
								implements Request.RequestObserver,
											ActionListener {
	private Request myRequest;
	private SessionWindow myParent;
	private JOptionPane optionPane;
	private JFileChooser fileChooser;
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
		
		toShow.addObserver(this);
		
		optionPane = new JOptionPane(components, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.YES_NO_OPTION, null, options);
		setContentPane(optionPane);
		yesButton.addActionListener(this);
		noButton.addActionListener(this);
		cancelButton.addActionListener(this);
		//optionPane.addPropertyChangeListener(this);
		pack();
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case yesOption:
			if (myRequest instanceof IncomingFileRequest) {
				fileChooser = new JFileChooser();
				int choice = fileChooser.showSaveDialog(this);
				if (choice == JFileChooser.APPROVE_OPTION) {
					IncomingFileRequest ifr = 
							((IncomingFileRequest) myRequest); 
					ifr.setFileDestination(fileChooser.getSelectedFile());
					new FileProgressBarDialog(myParent, ifr.getUsername(),
							ifr.getFileName(), ifr.getFileSize(),
							ifr.getFileSender(), false);
				} else {
					break;
				}
			}
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
			sb.append(" vill ansluta");
			if (!r.getMessage().isEmpty()) {
				sb.append(" och hälsar:\n\"");
				sb.append(r.getMessage());
				sb.append("\"\n");
			} else {
				sb.append(".\n");
			}
		//} else if (r instanceof KeyRequest) {
			//lägg till saker
		} else if (r instanceof IncomingFileRequest) {
			FileRequest fr = (FileRequest) r;
			sb.append(String.format("Användaren %s vill skicka filen %s (%s)",
					r.getUsername(), fr.getFileName(),
					readableFileSize(fr.getFileSize())));
			if (r.getMessage() != null && !r.getMessage().isEmpty()) {
				sb.append(String.format("och hälsar:\n\"%s\"",
						r.getMessage()));
			} else {
				sb.append(".");
			}
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
	public void requestTimedOut() {
		if (isVisible()) {
			JOptionPane.showMessageDialog(this, "Request timed out!",
					"Timeout", JOptionPane.WARNING_MESSAGE);
			dispose();
		}
	}

	@Override
	public void requestKilled() {
		if (isVisible()) {
			dispose();
		}
	}
	
	/**
	 * Formats a long as a file size (in kBs, MBs, GBs etc.)
	 * Not original work! From Mr Ed, Stack Exchange,
	 * https://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc
	 * @param size
	 * @return
	 */
	public static String readableFileSize(long size) {
	    if (size <= 0)
	    	return "0";
	    final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").
	    		format(size/Math.pow(1024, digitGroups)) + " " + 
	    units[digitGroups];
	}
}
