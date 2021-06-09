package chav1961.ji.models.items;


import chav1961.ji.models.interfaces.TypedActor;
import chav1961.ji.models.interfaces.GoodsType;
import chav1961.ji.models.interfaces.ShipType;

import java.util.HashMap;
import java.util.Map;

import chav1961.ji.models.interfaces.Actor;
import chav1961.ji.models.interfaces.AncestorSource;

public class ShipProducer implements TypedActor<ShipType> {
	private final Map<ShipType, Actor>	types = new HashMap<>();
	
	public ShipProducer() {
	}
	
	@Override
	public SourceType getSourceType() {
		return SourceType.PRODUCER;
	}

	@Override
	public GoodsType getContentType() {
		return GoodsType.SHIP;
	}

	@Override
	public AncestorSource[] getAncestors() {
		throw new IllegalStateException("This method is not applicable for typed actor"); 
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAmountAwaited() {
		throw new IllegalStateException("This method is not applicable for typed actor"); 
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
		throw new IllegalStateException("This method is not applicable for typed actor"); 
	}

	@Override
	public ShipType[] getItemTypes() {
		return ShipType.values();
	}

	@Override
	public Actor getForType(final ShipType type) {
		if (type == null) {
			throw new NullPointerException("Actor type can't be null");
		}
		else {
			if (!types.containsKey(type)) {
				types.put(type, new InternalActor(type));
			}
			return types.get(type);
		}
	}

	@Override
	public String toString() {
		return "ShipProducer [types=" + types + "]";
	}


	private static class InternalActor implements Actor {
		private final ShipType	type;
		
		private int	awaited = 0;
		
		InternalActor(final ShipType type) {
			this.type = type;
		}

		@Override
		public SourceType getSourceType() {
			return SourceType.PRODUCER;
		}

		@Override
		public GoodsType getContentType() {
			return GoodsType.SHIP;
		}

		@Override
		public AncestorSource[] getAncestors() {
			return type.getAncestors();
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
			return true;
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
			return "InternalActor [type=" + type + ", awaited=" + awaited + "]";
		}
	}
}
