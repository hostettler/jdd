package ch.unige.cui.smv.dd;

public class Terminator {
	private LEAF mValue;

	public enum LEAF {
		ONE(3),

		ZERO(5),

		ANY(7);

		private int mHashCode;

		public int getmHashCode() {
			return this.mHashCode;
		}

		LEAF(int hash) {
			this.mHashCode = hash;
		}
	}

	protected Terminator() {
		this.mValue = LEAF.ONE;
	}

	protected Terminator(LEAF leaf) {
		this.mValue = leaf;
	}

	protected LEAF getLeaf() {
		return this.mValue;
	}

	public int getLevel() {
		return 1;
	}

	@Override
	public String toString() {
		return this.mValue.toString();
	}

	@Override
	public int hashCode() {
		return this.mValue.mHashCode;
	}

	@Override
	public boolean equals(Object that) {
		boolean eq = (this == that);
		if (!eq && that instanceof Terminator) {
			eq = (this.mValue.mHashCode == ((Terminator) that).mValue.mHashCode);
		}
		return eq;
	}

	public static final Terminator ANY = new Terminator(LEAF.ANY);

	public static final Terminator TRUE = new Terminator(LEAF.ONE);

	public static final Terminator FALSE = new Terminator(LEAF.ZERO);
}
