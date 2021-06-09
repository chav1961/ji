package chav1961.ji.models.interfaces;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import chav1961.ji.interfaces.IconKeeper;

public enum GoodsType implements IconKeeper {
	BREAD("icon.png",""),
	FRUITS("icon.png",""),
	MEAT("icon.png",""),
	FOOD("icon.png",""),
	COTTON("icon.png",""),
	CLOTH("icon.png",""),
	CLOTHES("icon.png",""),
	TREE("icon.png",""),
	PAPER("icon.png",""),
	WOOD("icon.png",""),
	FURNITURE("icon.png",""),
	COAL("icon.png",""),
	ORE("icon.png",""),
	IRON("icon.png",""),
	TOOLS("icon.png",""),
	WEAPON("icon.png",""),
	HORSE("icon.png",""),
	OIL("icon.png",""),
	GAZOLINE("icon.png",""),
	SHIP("icon.png",""),
	HUMAN("icon.png",""),
	SPECIALIST("icon.png",""),
	ENERGY("icon.png",""),
	RAILWAY("icon.png","");
	
	private final Icon	icon; 
	
	GoodsType(final String iconURI, final String tooltipId) {
		this.icon = new ImageIcon(this.getClass().getResource("icon.png"));
	}
	
	@Override
	public Icon getIcon() {
		return icon;
	}
}