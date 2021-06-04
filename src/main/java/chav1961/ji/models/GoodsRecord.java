package chav1961.ji.models;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import chav1961.ji.interfaces.IconKeeper;
import chav1961.ji.interfaces.ThreeStateSwitchKeeper;
import chav1961.ji.interfaces.ValueKeeper;

public class GoodsRecord {
	public enum GoogsType implements IconKeeper {
		CLOTHES("icon.png",""),
		FURNITURE("icon.png",""),
		TOOLS("icon.png",""),
		WEAPON("icon.png",""),
		FOOD("icon.png",""),
		WOOD("icon.png",""),
		IRON("icon.png",""),
		PAPER("icon.png",""),
		COTTON("icon.png",""),
		SHIPS("icon.png",""),
		COAL("icon.png",""),
		MINE("icon.png",""),
		OIL("icon.png",""),
		GAZOLINE("icon.png","");
		
		private final Icon	icon; 
		
		GoogsType(final String iconURI, final String tooltipId) {
			this.icon = new ImageIcon(this.getClass().getResource("icon.png"));
		}
		
		@Override
		public Icon getIcon() {
			return icon;
		}
	}

	public enum TradeOperationType implements ThreeStateSwitchKeeper<TradeOperationType> {
		TRADE_BID(SwitchState.LEFT_ON),
		TRADE_SELL(SwitchState.RIGHT_ON),
		TRADE_NEUTRAL(SwitchState.ALL_OFF),
		TRADE_UNAVAILABLE(SwitchState.UNAVAILABLE);

		private final SwitchState state;
		
		TradeOperationType(final SwitchState state) {
			this.state = state;
			
		}
		
		@Override
		public SwitchState getState() {
			return state;
		}
		
		public TradeOperationType getCargo() {
			return this;
		}
	}
	
	private final GoogsType		type;
	private int					total;
	private int					price;
	private int					available2Produce;
	private int					available2Get;
	private int					available2Sell;
	private TradeOperationType	currentTradeState = TradeOperationType.TRADE_NEUTRAL;
	
	public GoodsRecord(final GoogsType type) throws NullPointerException {
		if (type == null) {
			throw new NullPointerException("Goods type can't be null"); 
		}
		else {
			this.type = type;
		}
	}
	
	public GoogsType getType() {
		return type;
	}

	public ValueKeeper getTotal() {
		return new ValueKeeper() {
			@Override public ValueType getType() {return ValueType.AMOUNT;}
			@Override public int getValue() {return total;}
			@Override public boolean isHidden() {return false;}
		}; 
	}

	public ValueKeeper getAvailable2Produce() {
		return new ValueKeeper() {
			@Override public ValueType getType() {return ValueType.AMOUNT;}
			@Override public int getValue() {return available2Produce;}
			@Override public boolean isHidden() {return false;}
		}; 
	}
	
	public ValueKeeper getAvailable2Get() {
		return new ValueKeeper() {
			@Override public ValueType getType() {return ValueType.SLIDER;}
			@Override public int getMinValue() {return 0;}
			@Override public int getValue() {return available2Get;}
			@Override public int getMaxValue() {return available2Produce;}
			@Override public boolean isHidden() {return false;}
		}; 
	}

	public ValueKeeper getPrice() {
		return new ValueKeeper() {
			@Override public ValueType getType() {return ValueType.PRICE;}
			@Override public int getValue() {return price;}
			@Override public boolean isHidden() {return false;}
		}; 
	}
	
	public ValueKeeper getAvailable2Sell() {
		return new ValueKeeper() {
			@Override public ValueType getType() {return ValueType.SLIDER;}
			@Override public int getMinValue() {return 0;}
			@Override public int getValue() {return available2Sell;}
			@Override public int getMaxValue() {return total;}
			@Override public boolean isHidden() {return currentTradeState != TradeOperationType.TRADE_SELL;}
		}; 
	}

	public ThreeStateSwitchKeeper<TradeOperationType> getTradeState() {
		return currentTradeState;
	}
	
	public void setAvailable2Produce(final int amount) {
		this.available2Produce = Math.max(0, amount);
		this.available2Get = Math.min(available2Produce, available2Get);
	}
	
	public void setAvailable2Get(final int amount) {
		this.available2Get = Math.max(0, Math.min(available2Produce, amount));
	}

	public void setPrice(final int price) {
		this.price = Math.max(0, price);
	}
	
	public void setTotal(final int amount) {
		this.total = Math.max(0, amount);
		this.available2Sell = Math.min(this.available2Sell, this.total);
	}
	
	public void setAvailable2Sell(final int amount) {
		this.available2Sell = Math.max(0, Math.min(this.total, amount));
	}

	public void setTradeState(final TradeOperationType state) {
		this.currentTradeState = state;
	}

	@Override
	public String toString() {
		return "GoodsRecord [type=" + type + ", total=" + total + ", price=" + price + ", available2Produce="
				+ available2Produce + ", available2Get=" + available2Get + ", available2Sell=" + available2Sell
				+ ", currentTradeState=" + currentTradeState + "]";
	}
}
