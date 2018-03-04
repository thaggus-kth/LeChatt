package view;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import javax.swing.*;
import file.ProgressObserver;
import file.Progressor;

public class FileProgressBarDialog extends JDialog implements ProgressObserver,
												ActionListener, WindowListener {

	private JProgressBar progressBar;
	private JOptionPane optionPane;
	private JButton okCancelButton = new JButton("Avbryt");
	private JLabel statusLabel = new JLabel();
	private boolean done = false;
	private Progressor progressor;
	
	
	public FileProgressBarDialog(JFrame parent, String remoteUsersName,
			String filename, long fileSize,	Progressor p, boolean isSender) {
		super(parent, String.format("Filöverförng av %s", filename));
		Object[] options = {okCancelButton};
		ArrayList<Object> messages = new ArrayList<Object>();
		progressBar = new JProgressBar();
		progressor = p;
		
		if (isSender) {
			messages.add(String.format("Skickar %s till %s", filename,
					remoteUsersName));
		} else {
			messages.add(String.format("Tar emot %s från %s", filename,
					remoteUsersName));
		}
		messages.add(String.format("Storlek: %s",
				RequestPopupWindow.readableFileSize(fileSize)));
		messages.add(statusLabel);
		messages.add(progressBar);
		optionPane = new JOptionPane(messages.toArray(), JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_OPTION, null, options);
		
		progressBar.setStringPainted(true);
		if (!isSender) {
			statusLabel.setText("Väntar på anslutning...");
			progressBar.setIndeterminate(true);
		} else {
			statusLabel.setText("Skickar...");
		}
		
		okCancelButton.addActionListener(this);
		p.addObserver(this);
		setContentPane(optionPane);

		
		pack();
		setVisible(!isSender);
	}
	
	
	@Override
	public void incrementPercentProgress(int currentPercent) {
		if (progressBar.isIndeterminate()) {
			progressBar.setIndeterminate(false);
			statusLabel.setText("Tar emot...");
		}
		if (!isVisible()) {
			setVisible(true);
		}
		progressBar.setValue(currentPercent);
	}

	@Override
	public void progressTerminatedDueToError(Exception e) {
		if (e != null) {
			/* null indicates it was the proxy who called us */
			JOptionPane.showMessageDialog(this, "Filöverföringen avbröts på grund"
					+ " av ett fel: " + e.getMessage() + e.getCause().getMessage(),
					"Fel i filöverföring", JOptionPane.ERROR_MESSAGE);
			progressBar.setString("Avbrutet");
			progressBar.setForeground(Color.RED);
			progressBar.setBackground(Color.DARK_GRAY);
			progressBar.setValue(100);
			statusLabel.setText("Överföringen avbruten på grund av fel");
			statusLabel.setForeground(Color.RED);
		}
		done = true;
	}

	@Override
	public void processFinished() {
		progressBar.setValue(100);
		statusLabel.setText("Klart");
		okCancelButton.setText("Ok");
		done = true;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okCancelButton) {
			dispose();
		}
	}
	
	@Override
	public void dispose() {
		if (!done) {
			progressor.abort();
		}
		super.dispose();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (!done) {
			progressor.abort();
		}		
	}

	/* The following methods do nothing */
	@Override
	public void windowActivated(WindowEvent e) {}


	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}


	@Override
	public void windowDeiconified(WindowEvent e) {}


	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}
	
	

}
