package chav1961.ji.models.items;

import chav1961.ji.models.AncestorSourceImpl;
import chav1961.ji.models.interfaces.Actor;
import chav1961.ji.models.interfaces.AncestorSource;
import chav1961.ji.models.interfaces.GoodsType;

public class ToolsCollector implements Actor {
	private static AncestorSource[]	ancestors = {new AncestorSourceImpl(GoodsType.TOOLS, 1),
												 new AncestorSourceImpl(GoodsType.RAILWAY, 1)
												};

	private int	awaited = 0;

	public ToolsCollector() {
	}

	@Override
	public SourceType getSourceType() {
		return SourceType.COLLECTOR;
	}

	@Override
	public GoodsType getContentType() {
		return GoodsType.TOOLS;
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
	public void setAmountAwaited(final int amount) {
		awaited = amount;
	}

	@Override
	public String toString() {
		return "ToolsCollector [awaited=" + awaited + "]";
	}
}
