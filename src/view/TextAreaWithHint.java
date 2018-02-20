package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextArea;

/**
 * A small extension of JTextArea which displays a hint message when the
 * text field is empty.
 * @author thaggus
 *
 */
public class TextAreaWithHint extends JTextArea implements FocusListener {
	private String hintText;
	private Font defaultFont = super.getFont();
	private Color defaultForeground = super.getForeground();
	private Font hintFont;
	private Color hintForeground = Color.GRAY;
	private boolean hinting = true;
	
	/**
	 * Constructor.
	 * @param hint - The hint which is to be displayed when the text area
	 * is empty.
	 */
	public TextAreaWithHint(String hint) {
		hintFont = new Font(defaultFont.getFontName(),Font.ITALIC,
				defaultFont.getSize());
		hintText = hint;
		addFocusListener(this);
		updateHint();
	}
	
	/**
	 * Displays or hides the hint according to the state of the hinting
	 * field.
	 */
	private void updateHint() {
		if (hinting) {
			setText(hintText);
			setFont(hintFont);
			setForeground(hintForeground);
		} else {
			setText("");
			setFont(defaultFont);
			setForeground(defaultForeground);
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (hinting) {
			hinting = false;
			updateHint();
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (super.getText().isEmpty()) {
			hinting = true;
			updateHint();
		}
	}
	
	@Override
	public String getText()	{
		String retval = "";
		if (!hinting) {
			retval = super.getText();
		}
		return retval;
	}
}