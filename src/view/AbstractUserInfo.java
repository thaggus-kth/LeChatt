package view;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import crypto.CryptoType;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import session.SessionController;
import session.ChatObserver;

/**
 * Panel which displays the connected users in a list and provides options to
 * select encryption for each user.
 * @author thaggus
 *
 */
public abstract class AbstractUserInfo extends JPanel implements ChatObserver,
														ListSelectionListener,
														ActionListener {

	public static final String NO_ENCRYPTION_STRING = "Ingen";
	public static final String SEND_REQUEST_STRING = "Fler...";
	public static final int NO_SELECTION = -1;
	protected NamedIntegerListModel usernameList = new NamedIntegerListModel();
	private JList<NamedInteger> usernameListView = new JList<NamedInteger>(usernameList);
	private JComboBox<String> cryptoSelection = new JComboBox<String>();
	JTextArea infoArea = new JTextArea();
	protected JPanel buttonPanel = new JPanel();
	protected int selectedID = NO_SELECTION;
	SessionController session;
	SessionWindow window;
	
	public AbstractUserInfo(SessionWindow sw, SessionController sc) {
		session = sc;
		window = sw;
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		sc.addObserver(this);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		infoArea.setEditable(false);
		//infoArea.setEnabled(false);
		infoArea.setBackground(this.getBackground());
		infoArea.setForeground(this.getForeground());
		infoArea.setAlignmentY(BOTTOM_ALIGNMENT);
		infoArea.setLineWrap(true);
		infoArea.setPreferredSize(new Dimension(200,100));
		infoArea.setMaximumSize(new Dimension(1000,100));;
		add(infoArea);
		add(Box.createRigidArea(new Dimension(10,10)));
		add(new JScrollPane(usernameListView));
		//add(Box.createVerticalGlue());
		add(buttonPanel);
		buttonPanel.add(new JLabel("Kryptering:"));
		buttonPanel.add(Box.createRigidArea(new Dimension(10,1)));
		buttonPanel.add(cryptoSelection);
		usernameListView.addListSelectionListener(this);
		cryptoSelection.setMaximumSize(new Dimension(100, 30));
	}
	
	/**
	 * Return an info text to display to the user for this session. This
	 * info can be dynamically updated: the panel will re-read from this
	 * method at each call of updateView().
	 * @return String (possibly multi-line) containing the info.
	 */
	abstract public String getSessionInfoText();
	
	private void updateCryptoSelection() {
		cryptoSelection.removeActionListener(this);
		cryptoSelection.removeAllItems();
		if (selectedID == NO_SELECTION) {
			cryptoSelection.setEnabled(false);
		} else {
			cryptoSelection.setEnabled(true);
			cryptoSelection.addItem(NO_ENCRYPTION_STRING);
			for (CryptoType ct : session.getAvailableCryptos(selectedID)) {
				cryptoSelection.addItem(ct.toString());
			}
			cryptoSelection.addItem(SEND_REQUEST_STRING);
			selectActiveCrypto();
			cryptoSelection.addActionListener(this);
			
		}
	}
	
	@Override
	/**
	 * Monitors the crypto selection box. Calls the setCrypto method
	 * in session with the selection.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cryptoSelection) {
			Object selectedItem = cryptoSelection.getSelectedItem();
			if (selectedItem != null) {
				switch ((String) selectedItem) {
				case (NO_ENCRYPTION_STRING):
					session.setCrypto(selectedID, CryptoType.PLAIN);
					break;
				case (SEND_REQUEST_STRING):
					new SendRequestPopup((JFrame) window, session,
							SendRequestPopup.SupportedRequests.KEY_REQUEST,
							selectedID);
					selectActiveCrypto();	
					break;
				default:
					session.setCrypto(selectedID,
							CryptoType.valueOf(
									(String) cryptoSelection.getSelectedItem()));
				}
			}
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		int index = usernameListView.getSelectedIndex();
		if (index != NO_SELECTION) {
			selectedID = usernameList.getElementAt(index).getValue();
		} else {
			selectedID = NO_SELECTION;
		}
		updateCryptoSelection();
		window.updateView();
	}
	
	@Override
	public void updateView() {
		usernameList.updateRelativeTo(session.getUsernamesAndIDs());
		infoArea.setText(getSessionInfoText());
		refreshCryptoSelection();
	}
	
	public void refreshCryptoSelection() {
		if (selectedID != NO_SELECTION) {
			updateCryptoSelection();
		}
	}
	
	public void selectActiveCrypto() {
		if (selectedID != NO_SELECTION) {
			CryptoType active = session.getActiveCrypto(selectedID);
			if (active == CryptoType.PLAIN) {
				cryptoSelection.setSelectedItem(NO_ENCRYPTION_STRING);
			} else {
				cryptoSelection.setSelectedItem(active.toString());
			}
		}
	}
		
	public class NamedIntegerListModel extends DefaultListModel<NamedInteger> {
		/* I apologize... I'm crying... this code is so bad... */	
		public void updateRelativeTo(TreeMap<Integer, String> master) {
			Set<Map.Entry<Integer, String>> masterSet = master.entrySet();
			ArrayList<NamedInteger> masterList = new ArrayList<NamedInteger>();
			ArrayList<NamedInteger> toAdd = new ArrayList<NamedInteger>();
			ArrayList<NamedInteger> toRemove = new ArrayList<NamedInteger>();
			
			for (Map.Entry<Integer, String> e : masterSet) {
				NamedInteger ni = new NamedInteger(e.getKey(), e.getValue());
				masterList.add(ni);
				if (!contains(ni)) {
					toAdd.add(ni);
				}
			}
			Enumeration<NamedInteger> ourElements = elements();
			while (ourElements.hasMoreElements()) {
				NamedInteger ni = ourElements.nextElement();
				if (!masterList.contains(ni)) {
					toRemove.add(ni);
				}
			}
			for (NamedInteger ni : toAdd) {
				addElement(ni);
			}
			for (NamedInteger ni : toRemove) {
				removeElement(ni);
			}
		}
	}
	
	public class NamedInteger implements Comparable {
		/* I apologize... I'm crying... this code is so bad... */
		private String name;
		private int value;
		
		public NamedInteger(int value, String name) {
			this.name = name;
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
		
		public String getName() {
			return name;
		}
		
		public String toString() {
			return name;
		}
		
		@Override
		public int compareTo(Object o) {
			int retval;
			if (o instanceof NamedInteger) {
				retval = Integer.compare(value, ((NamedInteger) o).getValue());
			} else {
				int y = Integer.valueOf((int) o);
				retval = Integer.compare(value, y);
			}
			return retval;
		}
		
		@Override
		public boolean equals(Object o) {
			boolean retval = false;
			if (o instanceof NamedInteger) {
				NamedInteger ni = (NamedInteger) o;
				retval = ni.getValue() == value && ni.getName() == name;
			}
			return retval;
		}
	}
	
	/**
	 * Gets the currently selected ID.
	 * @return the selected user ID. May be AbstractUserInfo.NO_SELECTION
	 * if no selection is made.
	 */
	public int getSelectedID() {
		return selectedID;
	}
}
