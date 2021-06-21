package chav1961.ji.interfaces;

public enum CountryType {
	COLONY(4), METROPOLY(8);
	
	private final int	initialSize;
	
	CountryType(final int initialSize) {
		this.initialSize = initialSize;
	}
	
	public int getInitialSize() {
		return initialSize;
	}
}