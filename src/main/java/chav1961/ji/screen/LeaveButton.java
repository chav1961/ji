package chav1961.ji.screen;

import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class LeaveButton extends JButton {
	private static final long serialVersionUID = 1L;
	private static final Icon	icon = new ImageIcon(LeaveButton.class.getResource("leave.png"));
	
	public LeaveButton(final ActionListener listener) {
		if (listener == null) {
			throw new NullPointerException("Listener can't be null");
		}
		else {
			setIcon(icon);
			addActionListener(listener);
		}
	}
	
	public int getButtonSize() {
		return Math.max(icon.getIconWidth(), icon.getIconHeight());
	}
}
