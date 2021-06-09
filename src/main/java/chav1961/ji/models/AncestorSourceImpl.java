package chav1961.ji.models;

import chav1961.ji.models.interfaces.AncestorSource;
import chav1961.ji.models.interfaces.GoodsType;

public class AncestorSourceImpl implements AncestorSource {
	private final GoodsType	source;
	private final int		amount;
	
	public AncestorSourceImpl(final GoodsType source, final int amount) {
		super();
		this.source = source;
		this.amount = amount;
	}

	@Override
	public GoodsType getSource() {
		return source;
	}

	@Override
	public int getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return "AncestorSourceImpl [source=" + source + ", amount=" + amount + "]";
	}
}
