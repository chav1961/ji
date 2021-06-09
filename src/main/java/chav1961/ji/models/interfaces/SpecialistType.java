package chav1961.ji.models.interfaces;

import chav1961.ji.models.AncestorSourceImpl;

public enum SpecialistType {
	ENGINEER("icon.png","",KindOfSpecialist.REFINER, new AncestorSourceImpl(GoodsType.PAPER,4));
	
	private final String 			icon;
	private final String 			descriptor;
	private final KindOfSpecialist	kind;
	private final AncestorSource[]	ancestors;
	
	SpecialistType(final String icon, final String descriptor, final KindOfSpecialist kind, final AncestorSource... ancestors) {
		this.icon = icon;
		this.descriptor = descriptor;
		this.kind = kind;
		this.ancestors = ancestors;
	}
	
	public String getIcon() {
		return icon;
	}
	
	public String getDescriptor() {
		return descriptor;
	}
	
	public KindOfSpecialist getKind() {
		return kind;
	}
	
	public AncestorSource[] getAncestors() {
		return ancestors;
	}
}
