package chav1961.ji.models.interfaces;

public interface Actor {
	public enum SourceType {
		FACTORY, CONSUMER, PRODUCER, COLLECTOR 
	}

	public enum Restrictions {
		NONE, RESTRICT, PROCESS_OVERFLOW
	}
	
	SourceType getSourceType();
	GoodsType getContentType();
	AncestorSource[] getAncestors();
	int getPriority();
	int getAmountAwaited();
	boolean needClearAfterTick();
	Restrictions getRestrictions();
	void setAmountAwaited(int amount);

}
