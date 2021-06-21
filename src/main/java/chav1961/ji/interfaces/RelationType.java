package chav1961.ji.interfaces;

import java.awt.Color;

public enum RelationType {
	NEUTRAL(Color.WHITE),
	COUNSUL(Color.LIGHT_GRAY),
	EMBASSY(Color.YELLOW),
	ALLIANCE(Color.CYAN),
	JOIN(Color.GREEN),
	WAR(Color.BLACK);
	
	private final Color	color;
	
	private RelationType(final Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
}
