package chav1961.ji.models.items;

import chav1961.ji.models.interfaces.Actor;
import chav1961.ji.models.interfaces.AncestorSource;
import chav1961.ji.models.interfaces.GoodsType;

public class OreProducer implements Actor {
	private static final AncestorSource[]	ancestors = new AncestorSource[0]; 

	private int	awaited = 0;
	
	public OreProducer() {
	}

	@Override
	public SourceType getSourceType() {
		return SourceType.PRODUCER;
	}

	@Override
	public GoodsType getContentType() {
		return GoodsType.ORE;
	}

	@Override
	public AncestorSource[] getAncestors() {
		return ancestors;
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAmountAwaited() {
		return awaited;
	}

	@Override
	public boolean needClearAfterTick() {
		return false;
	}

	@Override
	public Restrictions getRestrictions() {
		return Restrictions.NONE;
	}

	@Override
	public void setAmountAwaited(final int amount) {
		awaited = amount;
	}

	@Override
	public String toString() {
		return "OreProducer [awaited=" + awaited + "]";
	}
}
