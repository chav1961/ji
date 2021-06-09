package chav1961.ji.models.items;


import chav1961.ji.models.AncestorSourceImpl;
import chav1961.ji.models.interfaces.Actor;
import chav1961.ji.models.interfaces.GoodsType;
import chav1961.ji.models.interfaces.AncestorSource;

public class RailwayProducer implements Actor {
	private static final AncestorSource[]	ancestors = {new AncestorSourceImpl(GoodsType.IRON, 1),
												new AncestorSourceImpl(GoodsType.WOOD, 1),
												new AncestorSourceImpl(GoodsType.HUMAN, 1),
												};

	private int	awaited = 0;
	
	public RailwayProducer() {
	}
	
	@Override
	public SourceType getSourceType() {
		return SourceType.PRODUCER;
	}

	@Override
	public GoodsType getContentType() {
		return GoodsType.RAILWAY;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean needClearAfterTick() {
		return true;
	}

	@Override
	public Restrictions getRestrictions() {
		return Restrictions.RESTRICT;
	}

	@Override
	public void setAmountAwaited(final int amount) {
		awaited = amount;
	}
}
