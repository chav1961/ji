package chav1961.ji.screen;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

public class MainCanvas extends JComponent {
	private static final long serialVersionUID = 1L;

	public MainCanvas() {
		setLayout(null);
	}
	
	@Override
	protected void paintComponent(final Graphics g) {
		final Graphics2D		g2d = (Graphics2D)g;
		final AffineTransform	oldAt = g2d.getTransform();
		
		peekComponents(g2d); 
		fillBackground(g2d);
		drawPersons(g2d);
		g2d.setTransform(oldAt);
	}
	
	private void peekComponents(final Graphics2D g2d) {
		
	}
	
	private void fillBackground(final Graphics2D g2d) {
		
	}

	private void drawPersons(final Graphics2D g2d) {
		
	}
}
