package chav1961.ji.screen;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class JPanelWithImage extends JPanel {
	private static final long serialVersionUID = 1L;

	private Image	background;
	
	public JPanelWithImage() {
		super();
	}

	public JPanelWithImage(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}

	public JPanelWithImage(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}

	public JPanelWithImage(LayoutManager layout) {
		super(layout);
	}

	public Image getBackgroundImage() {
		return background;
	}
	
	public void setBackgroundImage(final Image image) {
		background = image;
		repaint();
	}
	
	@Override
	protected void paintComponent(final Graphics g) {
		if (background != null) {
			((Graphics2D)g).drawImage(background, 0, 0, null);
		}
		else {
			super.paintComponent(g);
		}
	}
}
