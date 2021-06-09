package chav1961.ji.models.interfaces;

import chav1961.ji.models.AncestorSourceImpl;

public enum ShipType {
	TYPE1("", new AncestorSourceImpl(GoodsType.WOOD,4), new AncestorSourceImpl(GoodsType.CLOTH,3));
	
	private final String			typeName;
	private final AncestorSource[]	ancestors;
	
	ShipType(final String typeName, AncestorSource... ancestors) {
		this.typeName = typeName;
		this.ancestors = ancestors;
	}
	
	public String getTypeName() {
		return typeName;
	}
	
	public AncestorSource[] getAncestors() {
		return ancestors;
	}
}
