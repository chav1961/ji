package chav1961.ji.screen;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import chav1961.ji.ResourceRepository;

public class HyperlinkButton extends JLabel  {
	private static final long serialVersionUID = 1L;

	public HyperlinkButton(final int fontSize, final String caption, final ActionListener listener) {
		
		setFocusable(true);
		setForeground(Color.BLUE);
        setFont(ResourceRepository.ApplicationFont.FONT_9.getFont().deriveFont(Font.PLAIN, fontSize));
		setText(caption);
        addMouseListener(new MouseListener() {
			@Override public void mouseReleased(MouseEvent e) {}
			@Override public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2) {
					listener.actionPerformed(new ActionEvent(HyperlinkButton.this,0,""));
				}
			}
		});
        
	}
}
