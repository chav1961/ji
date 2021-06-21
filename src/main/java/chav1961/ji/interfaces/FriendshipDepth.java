package chav1961.ji.interfaces;

import java.awt.Color;

public enum FriendshipDepth {
	NEUTRAL(Color.GRAY);

	private final Color	color;
	
	private FriendshipDepth(final Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
}
