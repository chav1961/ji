package chav1961.ji.models;

import java.util.Arrays;

import chav1961.ji.models.interfaces.GoodsType;

public class AncestorNode {
	private final GoodsType			type;
	private final AncestorSource[]	sources;
	
	public AncestorNode(final GoodsType type, final AncestorSource... sources) {
		if (type == null) {
			throw new NullPointerException("Type can't be null");
		}
		else if (sources == null || sources.length == 0) {
			throw new IllegalArgumentException("Ancestors can't be null or empty");
		}
		else {
			this.type = type;
			this.sources = sources;
		}
	}
	
	public GoodsType getType() {
		return type;
	}

	public AncestorSource[] getSources() {
		return sources;
	}

	@Override
	public String toString() {
		return "AncestorNode [type=" + type + ", sources=" + Arrays.toString(sources) + "]";
	}

	public static class AncestorSource {
		private final GoodsType type;
		private final int		required;
		
		public AncestorSource(final GoodsType type, final int required) {
			this.type = type;
			this.required = required;
		}

		public GoodsType getType() {
			return type;
		}

		public int getRequired() {
			return required;
		}

		@Override
		public String toString() {
			return "AncestorSource [type=" + type + ", required=" + required + "]";
		}
	}
}
