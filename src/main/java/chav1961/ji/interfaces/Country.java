package chav1961.ji.interfaces;

import java.awt.Color;

public enum Country {
	KESSEL(CountryType.COLONY, 'a', Color.LIGHT_GRAY),
	PRAM(CountryType.COLONY, 'b', Color.LIGHT_GRAY),
	LOAK(CountryType.COLONY, 'c', Color.LIGHT_GRAY),
	ZAZI(CountryType.COLONY, 'd', Color.LIGHT_GRAY),
	TWELT(CountryType.COLONY, 'e', Color.LIGHT_GRAY),
	VODAN(CountryType.COLONY, 'f', Color.LIGHT_GRAY),
	CATHSAY(CountryType.COLONY, 'g', Color.LIGHT_GRAY),
	DADGE(CountryType.COLONY, 'h', Color.LIGHT_GRAY),
	SINDEL(CountryType.COLONY, 'i', Color.LIGHT_GRAY),
	BRUCHR(CountryType.COLONY, 'j', Color.LIGHT_GRAY),
	MANK(CountryType.COLONY, 'k', Color.LIGHT_GRAY),
	PONT(CountryType.COLONY, 'l', Color.LIGHT_GRAY),
	ISSA(CountryType.COLONY, 'm', Color.LIGHT_GRAY),
	IDOLON(CountryType.COLONY, 'n', Color.LIGHT_GRAY),
	CHARSHEN(CountryType.COLONY, 'o', Color.LIGHT_GRAY),
	ZINLY(CountryType.COLONY, 'p', Color.LIGHT_GRAY),
	KEM(CountryType.METROPOLY, '1', Color.RED),
	DEVRON(CountryType.METROPOLY, '2', Color.ORANGE),
	DENEB(CountryType.METROPOLY, '3', Color.YELLOW),
	ORDUN(CountryType.METROPOLY, '4', Color.GREEN),
	HAXAC(CountryType.METROPOLY, '5', Color.CYAN),
	ZIMM(CountryType.METROPOLY, '6', Color.BLUE),
	PATAGON(CountryType.METROPOLY, '7', Color.MAGENTA);
	
	private final CountryType	type;
	private final char			symbol;
	private final Color			color;
	
	Country(final CountryType type, final char symbol, final Color color) {
		this.type = type;
		this.symbol = symbol;
		this.color = color;
	}
	
	public CountryType getType() {
		return type;
	}
	
	public char getSymbol() {
		return symbol;
	}
	
	public Color getColor() {
		return color;
	}
}