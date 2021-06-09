package chav1961.ji.models.items;

import chav1961.ji.models.AncestorSourceImpl;
import chav1961.ji.models.interfaces.Actor;
import chav1961.ji.models.interfaces.AncestorSource;
import chav1961.ji.models.interfaces.GoodsType;

public class FactoryGazolineProducer implements Actor {
	private static final AncestorSource[]	ancestors = {new AncestorSourceImpl(GoodsType.OIL, 2),
														new AncestorSourceImpl(GoodsType.HUMAN, 1),
														};
	private int	awaited = 0;
	
	@Override
	public SourceType getSourceType() {
		return SourceType.FACTORY;
	}

	@Override
	public GoodsType getContentType() {
		return GoodsType.GAZOLINE;
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
		return Restrictions.RESTRICT;
	}

	@Override
	public void setAmountAwaited(int amount) {
		awaited = amount;
	}

	@Override
	public String toString() {
		return "FactoryGazolineProducer [awaited=" + awaited + "]";
	}
}
