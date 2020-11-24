package net.hostettler.jdd.dd.ddd;

import java.util.Map;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.Hom;
import net.hostettler.jdd.dd.HomImpl;

public  abstract class DDDHomImpl<VAR, VAL> extends HomImpl<VAR, VAL> implements Hom<VAR, VAL> {
	public DDDHomImpl(boolean activateCache) {
		super(activateCache);
	}

	public DDDHomImpl() {
		super(true);
	}

	@SuppressWarnings("unchecked")
	protected DD<VAR, VAL> getAny() {
		return (DD<VAR, VAL>) DDDImpl.DDD_ANY;
	}

	@SuppressWarnings("unchecked")
	protected DD<VAR, VAL> getFalse() {
		return (DD<VAR, VAL>) DDDImpl.DDD_FALSE;
	}

	@SuppressWarnings("unchecked")
	protected DD<VAR, VAL> getTrue() {
		return (DD<VAR, VAL>) DDDImpl.DDD_TRUE;
	}

	protected DD<VAR, VAL> phi1(Object... parameters) {
		return this.getTrue();
	}
	
	@Override
	protected DD<VAR, VAL> phiX(VAR e, VAL x, Map<VAL, DD<VAR, VAL>> alpha, Object... parameters) {
		return phi(e, x, (Map) alpha, parameters);
	}

	public Hom<VAR, VAL> compose(Hom<VAR, VAL> subHom) {
		return compose(subHom, true);
	}

	public Hom<VAR, VAL> compose(Hom<VAR, VAL> subHom, boolean cache) {
		return new ComposeHom((Hom<VAR, VAL>) subHom, cache);
	}

	public Hom<VAR, VAL> union(Hom<VAR, VAL> subHom) {
		return union(subHom, true);
	}

	public Hom<VAR, VAL> union(Hom<VAR, VAL> subHom, boolean cache) {
		return  new UnionHom((Hom<VAR, VAL>) subHom, cache);
	}

	public Hom<VAR, VAL> fixpoint() {
		return fixpoint(true);
	}

	public Hom<VAR, VAL> fixpoint(boolean cache) {
		return  new FixPointHom(cache);
	}

	public Hom<VAR, VAL>  saturate() {
		Hom<VAR, VAL> t = union( new DDDIdHom<VAR, VAL>());
		return t.fixpoint(true);
	}

	public Hom<VAR, VAL>  saturate(boolean cache) {
		Hom<VAR, VAL> t = union(new DDDIdHom<VAR, VAL>());
		return  t.fixpoint(cache);
	}

	protected abstract DD<VAR, VAL> phi(VAR paramVar, VAL paramVal, Map<VAL, DD<VAR, VAL>> paramMap,
			Object... paramVarArgs);

	private class ComposeHom extends DDDHomImpl<VAR, VAL> {
		private DDDHomImpl<VAR, VAL> mHomOp1;
		private DDDHomImpl<VAR, VAL> mHomOp2;

		public ComposeHom(Hom<VAR, VAL> subHom, boolean cache) {
			super(cache);
			this.mHomOp1 = (DDDHomImpl<VAR, VAL>) subHom;
			this.mHomOp2 = (DDDHomImpl<VAR, VAL>) DDDHomImpl.this;
		}

		protected DD<VAR, VAL> phi(VAR e, VAL x, Map<VAL, DD<VAR, VAL>> alpha, Object... parameters) {
			DD<VAR, VAL> ddd = DDDImpl.create(e, x, (DD<VAR, VAL>) id(alpha, x));
			ddd = this.mHomOp2.phi(ddd, parameters);
			ddd = this.mHomOp1.phi(ddd, parameters);
			return ddd;
		}

		protected DD<VAR, VAL>  phi1(Object... parameters) {
			DD<VAR, VAL>  ddd = (DD<VAR, VAL>) this.mHomOp2.phi1(parameters);
			ddd = this.mHomOp1.phi(ddd, parameters);
			return ddd;
		}

		public boolean isLocallyInvariant(DD<VAR, VAL> dd) {
			return this.mHomOp1.isLocallyInvariant(dd) & this.mHomOp2.isLocallyInvariant(dd);
		}

		protected int computeHashCode() {
			return getClass().hashCode() * 2161 + this.mHomOp1.hashCode() * 7481 + this.mHomOp2.hashCode() * 4973;
		}

		@SuppressWarnings("unchecked")
		protected boolean isEqual(Object that) {
			boolean eq = (this == that);
			if (!eq && that instanceof DDDHomImpl.ComposeHom) {
				ComposeHom thatCompose = (ComposeHom) that;
				eq = (this.mHomOp1.equals(thatCompose.mHomOp1) && this.mHomOp2.equals(thatCompose.mHomOp2));
			}

			return eq;
		}

		public String toString() {
			return "o(" + DDDHomImpl.this.toString() + ", " + this.mHomOp1.toString() + ")";
		}
	}

	private class UnionHom extends DDDHomImpl<VAR, VAL> {
		private HomImpl<VAR, VAL> mHomOp1;

		private HomImpl<VAR, VAL> mHomOp2;

		private boolean mHom1isId;

		private boolean mHom2isId;

		public UnionHom(Hom<VAR, VAL> subHom, boolean cache) {
			this.mHomOp1 = (HomImpl<VAR, VAL>) subHom;
			this.mHomOp2 =  DDDHomImpl.this;
			if (this.mHomOp1 instanceof DDDIdHom) {
				this.mHom1isId = true;
			} else if (this.mHomOp2 instanceof DDDIdHom) {
				this.mHom2isId = true;
			}
		}

		protected DD<VAR, VAL> phi(VAR e, VAL x, Map<VAL, DD<VAR, VAL>> alpha, Object... parameters) {
			DD<VAR, VAL> d1, d2, ddd = DDDImpl.create(e, x, (DD<VAR, VAL>) id(alpha, x));
			if (this.mHom1isId) {
				d1 = ddd;
			} else {
				d1 = this.mHomOp1.phi(ddd, parameters);
			}
			if (this.mHom2isId) {
				d2 = ddd;
			} else {
				d2 = this.mHomOp2.phi(ddd, parameters);
			}
			ddd = (DD<VAR, VAL>) d1.union(d2);

			return ddd;
		}

		protected DD<VAR, VAL>  phi1(Object... parameters) {
			DD<VAR, VAL> d1 = ((DDDHomImpl)this.mHomOp1).phi1(parameters);
			DD<VAR, VAL> d2 = ((DDDHomImpl)this.mHomOp2).phi1(parameters);
			return d1.union(d2);
		}

		public boolean isLocallyInvariant(DD<VAR, VAL> dd) {
			return this.mHomOp1.isLocallyInvariant(dd) & this.mHomOp2.isLocallyInvariant(dd);
		}

		protected int computeHashCode() {
			return getClass().hashCode() * 5003 + this.mHomOp1.hashCode() * 3581 + this.mHomOp2.hashCode() * 2741;
		}

		@SuppressWarnings("unchecked")
		protected boolean isEqual(Object that) {
			boolean eq = (this == that);
			if (!eq && that instanceof DDDHomImpl.UnionHom) {
				UnionHom thatUnion = (UnionHom) that;
				eq = (this.mHomOp1.equals(thatUnion.mHomOp1) && this.mHomOp2.equals(thatUnion.mHomOp2));
			}

			return eq;
		}

		public String toString() {
			return "+(" + DDDHomImpl.this.toString() + ", " + this.mHomOp1.toString() + ")";
		}
	}

	private class FixPointHom extends DDDHomImpl<VAR, VAL> {
		private Hom<VAR, VAL> mHom;

		public FixPointHom(boolean cache) {
			super(cache);
			this.mHom = DDDHomImpl.this;
		}

		protected DD<VAR, VAL> phi(VAR e, VAL x, Map<VAL, DD<VAR, VAL>> alpha, Object... parameters) {
			DD<VAR, VAL> oldDDD = null;

			DD<VAR, VAL> newDDD = DDDImpl.create(e, x, (DD<VAR, VAL>) id(alpha, x));
			do {
				oldDDD = newDDD;
				newDDD = this.mHom.phi(newDDD, parameters);
			} while (newDDD != oldDDD);

			return newDDD;
		}

		public boolean isLocallyInvariant(DD<VAR, VAL> dd) {
			return DDDHomImpl.this.isLocallyInvariant(dd);
		}

		protected DD<VAR, VAL>  phi1(Object... parameters) {
			return this.mHom.phi(this.getTrue(), parameters);
		}

		protected int computeHashCode() {
			return getClass().hashCode() * 3881 + this.mHom.hashCode() * 1733;
		}

		@SuppressWarnings("unchecked")
		protected boolean isEqual(Object that) {
			boolean eq = (this == that);
			if (!eq && that instanceof DDDHomImpl.FixPointHom) {
				FixPointHom f = (FixPointHom) that;
				if (this.mHom.equals(f.mHom)) {
					eq = true;
				}
			}
			return eq;
		}

		public String toString() {
			return "Fixpoint(" + DDDHomImpl.this.toString() + ")";
		}
	}
}
