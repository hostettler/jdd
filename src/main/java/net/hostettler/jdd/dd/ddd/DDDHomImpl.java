package net.hostettler.jdd.dd.ddd;

import java.util.Map;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.Hom;
import net.hostettler.jdd.dd.HomImpl;

public  abstract class DDDHomImpl<Var, Val> extends HomImpl<Var, Val> implements Hom<Var, Val> {
	public DDDHomImpl(boolean activateCache) {
		super(activateCache);
	}

	public DDDHomImpl() {
		super(true);
	}

	protected DD getDDAny() {
		return DDDImpl.DDD_ANY;
	}

	protected DD getDDFalse() {
		return DDDImpl.DDD_FALSE;
	}

	protected DD getDDTrue() {
		return DDDImpl.DDD_TRUE;
	}

	protected DD<?, ?> phi1(Object... parameters) {
		return DDDImpl.DDD_TRUE;
	}
	
	@Override
	protected DD<Var, Val> phiX(Var e, Val x, Map<Val, DD<Var, Val>> alpha, Object... parameters) {
		return phi(e, x, (Map) alpha, parameters);
	}

	public Hom<Var, Val> compose(Hom<Var, Val> subHom) {
		return compose(subHom, true);
	}

	public Hom<Var, Val> compose(Hom<Var, Val> subHom, boolean cache) {
		return new ComposeHom((Hom<Var, Val>) subHom, cache);
	}

	public Hom<Var, Val> union(Hom<Var, Val> subHom) {
		return union(subHom, true);
	}

	public Hom<Var, Val> union(Hom<Var, Val> subHom, boolean cache) {
		return  new UnionHom((Hom<Var, Val>) subHom, cache);
	}

	public Hom<Var, Val> fixpoint() {
		return fixpoint(true);
	}

	public Hom<Var, Val> fixpoint(boolean cache) {
		return  new FixPointHom(cache);
	}

	public Hom<Var, Val>  saturate() {
		Hom<Var, Val> t = union( new DDDIdHom<Var, Val>());
		return t.fixpoint(true);
	}

	public Hom<Var, Val>  saturate(boolean cache) {
		Hom<Var, Val> t = union((Hom) new DDDIdHom<Object, Object>());
		return  t.fixpoint(cache);
	}

	protected abstract DD<Var, Val> phi(Var paramVar, Val paramVal, Map<Val, DD<Var, Val>> paramMap,
			Object... paramVarArgs);

	private class ComposeHom extends DDDHomImpl<Var, Val> {
		private HomImpl<Var, Val> mHomOp1;
		private HomImpl<Var, Val> mHomOp2;

		public ComposeHom(Hom<Var, Val> subHom, boolean cache) {
			super(cache);
			this.mHomOp1 = (HomImpl<Var, Val>) subHom;
			this.mHomOp2 = (HomImpl<Var, Val>) DDDHomImpl.this;
		}

		protected DD<Var, Val> phi(Var e, Val x, Map<Val, DD<Var, Val>> alpha, Object... parameters) {
			DD<Var, Val> ddd = DDDImpl.create(e, x, (DD<Var, Val>) id(alpha, x));
			ddd = this.mHomOp2.phi(ddd, parameters);
			ddd = this.mHomOp1.phi(ddd, parameters);
			return ddd;
		}

		protected DD<?, ?> phi1(Object... parameters) {
			DD<Var, Val> ddd = (DD<Var, Val>) ((DDDHomImpl)this.mHomOp2).phi1(parameters);
			ddd = this.mHomOp1.phi(ddd, parameters);
			return ddd;
		}

		public boolean isLocallyInvariant(DD<Var, Val> dd) {
			return this.mHomOp1.isLocallyInvariant(dd) & this.mHomOp2.isLocallyInvariant(dd);
		}

		protected int computeHashCode() {
			return getClass().hashCode() * 2161 + this.mHomOp1.hashCode() * 7481 + this.mHomOp2.hashCode() * 4973;
		}

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

	private class UnionHom extends DDDHomImpl<Var, Val> {
		private HomImpl<Var, Val> mHomOp1;

		private HomImpl<Var, Val> mHomOp2;

		private boolean mHom1isId;

		private boolean mHom2isId;

		public UnionHom(Hom<Var, Val> subHom, boolean cache) {
			this.mHomOp1 = (HomImpl<Var, Val>) subHom;
			this.mHomOp2 =  DDDHomImpl.this;
			if (this.mHomOp1 instanceof DDDIdHom) {
				this.mHom1isId = true;
			} else if (this.mHomOp2 instanceof DDDIdHom) {
				this.mHom2isId = true;
			}
		}

		protected DD<Var, Val> phi(Var e, Val x, Map<Val, DD<Var, Val>> alpha, Object... parameters) {
			DD<Var, Val> d1, d2, ddd = DDDImpl.create(e, x, (DD<Var, Val>) id(alpha, x));
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
			ddd = (DD<Var, Val>) d1.union(d2);

			return ddd;
		}

		protected DD<?, ?> phi1(Object... parameters) {
			DD<Var, Val> d1 = ((DDDHomImpl)this.mHomOp1).phi1(parameters);
			DD<Var, Val> d2 = ((DDDHomImpl)this.mHomOp2).phi1(parameters);
			return (DD<?, ?>) d1.union(d2);
		}

		public boolean isLocallyInvariant(DD<Var, Val> dd) {
			return this.mHomOp1.isLocallyInvariant(dd) & this.mHomOp2.isLocallyInvariant(dd);
		}

		protected int computeHashCode() {
			return getClass().hashCode() * 5003 + this.mHomOp1.hashCode() * 3581 + this.mHomOp2.hashCode() * 2741;
		}

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

	private class FixPointHom extends DDDHomImpl<Var, Val> {
		private Hom<Var, Val> mHom;

		public FixPointHom(boolean cache) {
			super(cache);
			this.mHom = DDDHomImpl.this;
		}

		protected DD<Var, Val> phi(Var e, Val x, Map<Val, DD<Var, Val>> alpha, Object... parameters) {
			DD<Var, Val> oldDDD = null;

			DD<Var, Val> newDDD = DDDImpl.create(e, x, (DD<Var, Val>) id(alpha, x));
			do {
				oldDDD = newDDD;
				newDDD = this.mHom.phi(newDDD, parameters);
			} while (newDDD != oldDDD);

			return newDDD;
		}

		public boolean isLocallyInvariant(DD<Var, Val> dd) {
			return DDDHomImpl.this.isLocallyInvariant(dd);
		}

		protected DD<?, ?> phi1(Object... parameters) {
			return this.mHom.phi((DD) DDDImpl.DDD_TRUE, parameters);
		}

		protected int computeHashCode() {
			return getClass().hashCode() * 3881 + this.mHom.hashCode() * 1733;
		}

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
