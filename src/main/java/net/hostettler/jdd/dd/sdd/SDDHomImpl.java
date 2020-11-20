package net.hostettler.jdd.dd.sdd;

import java.util.Map;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.Hom;
import net.hostettler.jdd.dd.HomImpl;
import net.hostettler.jdd.dd.ValSet;

public abstract class SDDHomImpl<Var, Val> extends HomImpl<Var, ValSet<Val>> implements Hom<Var, ValSet<Val>> {
	public SDDHomImpl(boolean activateCache) {
		super(activateCache);
	}

	public SDDHomImpl() {
		super(true);
	}

	protected DD getDDAny() {
		return SDDImpl.SDD_ANY;
	}

	protected DD getDDFalse() {
		return SDDImpl.SDD_FALSE;
	}

	protected DD getDDTrue() {
		return SDDImpl.SDD_TRUE;
	}

	public static <TVar, TVal> DD<TVar, TVal> getTrue(Class<TVar> varClass, Class<TVal> valClass) {
		return (DD) SDDImpl.SDD_TRUE;
	}
	public static <TVar, TVal> DD<TVar, TVal> getFalse(Class<TVar> varClass, Class<TVal> valClass) {
		return (DD) SDDImpl.SDD_FALSE;
	}
	public static <TVar, TVal> DD<TVar, TVal> getAny(Class<TVar> varClass, Class<TVal> valClass) {
		return (DD) SDDImpl.SDD_ANY;
	}
	
	
	protected  DD<Var, ValSet<Val>> phiX(Var e, ValSet<Val> x, Map<ValSet<Val>,  DD<Var, ValSet<Val>> > alpha,
			Object... parameters) {
		return phi(e, x, (Map) alpha, parameters);
	}


	public Hom<Var, ValSet<Val>> compose(Hom<Var, ValSet<Val>>  subHom) {
		return compose(subHom, true);
	}

	public Hom<Var, ValSet<Val>> compose(Hom<Var, ValSet<Val>> subHom, boolean cache) {
		return new ComposeHom(subHom, cache);
	}

	public Hom<Var, ValSet<Val>>  union(Hom<Var, ValSet<Val>> subHom) {
		return union(subHom, true);
	}

	public Hom<Var, ValSet<Val>>  union(Hom<Var, ValSet<Val>> subHom, boolean cache) {
		return new UnionHom(subHom, cache);
	}

	public Hom<Var, ValSet<Val>> fixpoint() {
		return fixpoint(true);
	}

	public Hom<Var, ValSet<Val>> fixpoint(boolean cache) {
		return new FixPointHom(cache);
	}

	public Hom<Var, ValSet<Val>> saturate() {
		Hom<Var, ValSet<Val>> t = union(new SDDIdHom());
		return t.fixpoint(true);
	}

	public Hom<Var, ValSet<Val>> saturate(boolean cache) {
		 Hom<Var, ValSet<Val>>  t = union(new SDDIdHom());
		return t.fixpoint(cache);
	}

	protected abstract DD<Var, ValSet<Val>> phi(Var var, ValSet<Val> values,
			Map<ValSet<Val>, DD<Var, ValSet<Val>>> alpha, Object... parameters);

	private class ComposeHom extends SDDHomImpl<Var, Val> {
		private SDDHomImpl<Var, Val> mHomOp1;
		private SDDHomImpl<Var, Val> mHomOp2;

		public ComposeHom(Hom<Var, ValSet<Val>> subHom, boolean cache) {
			super(cache);
			this.mHomOp1 = (SDDHomImpl<Var, Val>) subHom;
			this.mHomOp2 = SDDHomImpl.this;
		}

		protected DD<Var, ValSet<Val>> phi(Var e, ValSet<Val> x, Map<ValSet<Val>, DD<Var, ValSet<Val>>> alpha,
				Object... parameters) {
			DD<Var, ValSet<Val>> sdd = SDDImpl.create(e, (ValSet) x,  (DD) id(alpha, x));
			sdd = SDDHomImpl.this.phi(sdd, parameters);
			if (sdd != getDDFalse()) {
				sdd = this.mHomOp1.phi(sdd, parameters);
			}
			return sdd;
		}

		public boolean isLocallyInvariant(DD<Var, ValSet<Val>> dd) {
			return this.mHomOp1.isLocallyInvariant(dd) & this.mHomOp2.isLocallyInvariant(dd);
		}

		protected DD<?, ?> phi1(Object... parameters) {
			DD<Var, ValSet<Val>> sdd = (DD<Var, ValSet<Val>>) this.mHomOp2.phi1(parameters);
			sdd = this.mHomOp1.phi(sdd, parameters);
			return sdd;
		}

		protected int computeHashCode() {
			return getClass().hashCode() * 593 + this.mHomOp1.hashCode() * 5387 + this.mHomOp2.hashCode() * 2801;
		}

		protected boolean isEqual(Object that) {
			boolean eq = (this == that);
			if (!eq && that instanceof SDDHomImpl.ComposeHom) {
				ComposeHom thatCompose = (ComposeHom) that;
				eq = (this.mHomOp1.equals(thatCompose.mHomOp1) && this.mHomOp2.equals(thatCompose.mHomOp2));
			}

			return eq;
		}

		public String toString() {
			return "o(" + SDDHomImpl.this.toString() + ", " + this.mHomOp1.toString() + ")";
		}
	}

	private class UnionHom extends SDDHomImpl<Var, Val> {
		private SDDHomImpl<Var, Val> mHomOp1;

		private SDDHomImpl<Var, Val> mHomOp2;

		private boolean mHom1isId;

		private boolean mHom2isId;

		public UnionHom(Hom<Var, ValSet<Val>> subHom, boolean cache) {
			super(cache);
			this.mHomOp1 = (SDDHomImpl<Var, Val>) subHom;
			this.mHomOp2 = SDDHomImpl.this;
			if (this.mHomOp1 instanceof SDDIdHom) {
				this.mHom1isId = true;
			} else if (this.mHomOp2 instanceof SDDIdHom) {
				this.mHom2isId = true;
			}
		}

		protected DD<Var, ValSet<Val>> phi(Var e, ValSet<Val> x, Map<ValSet<Val>, DD<Var, ValSet<Val>>> alpha,
				Object... parameters) {
			DD<Var, ValSet<Val>> sdd = SDDImpl.create(e, x, (DD) id(alpha, x));
			if (this.mHom1isId || this.mHom2isId) {
				DD<Var, ValSet<Val>> sDD1, sDD2;
				if (this.mHom1isId) {
					sDD1 = sdd;
				} else {
					sDD1 = this.mHomOp1.phi(sdd, parameters);
				}
				if (this.mHom2isId) {
					sDD2 = sdd;
				} else {
					sDD2 = this.mHomOp2.phi(sdd, parameters);
				}
				sdd = (DD<Var, ValSet<Val>>) sDD1.union(sDD2);
				return sdd;
			}
			DD<Var, ValSet<Val>> d1 = this.mHomOp1.phi(sdd, parameters);
			DD<Var, ValSet<Val>> d2 = this.mHomOp2.phi(sdd, parameters);
			sdd = d1.union(d2);

			return sdd;
		}

		protected DD<?, ?> phi1(Object... parameters) {
			DD<Var, ValSet<Val>> d1 = (DD<Var, ValSet<Val>>) this.mHomOp1.phi1(parameters);
			DD<Var, ValSet<Val>> d2 = (DD<Var, ValSet<Val>>) this.mHomOp2.phi1(parameters);
			return d1.union(d2);
		}

		public boolean isLocallyInvariant(DD<Var, ValSet<Val>> dd) {
			return this.mHomOp1.isLocallyInvariant(dd) & this.mHomOp2.isLocallyInvariant(dd);
		}

		protected int computeHashCode() {
			return getClass().hashCode() * 4513 + this.mHomOp1.hashCode() * 2003 + this.mHomOp2.hashCode() * 7411;
		}

		protected boolean isEqual(Object that) {
			boolean eq = (this == that);
			if (!eq && that instanceof SDDHomImpl.UnionHom) {
				UnionHom thatUnion = (UnionHom) that;
				eq = (this.mHomOp1.equals(thatUnion.mHomOp1) && this.mHomOp2.equals(thatUnion.mHomOp2));
			}

			return eq;
		}

		public String toString() {
			return "+(" + SDDHomImpl.this.toString() + ", " + this.mHomOp1.toString() + ")";
		}
	}

	private class FixPointHom extends SDDHomImpl<Var, Val> {
		private Hom<Var, ValSet<Val>> mHom;

		public FixPointHom(boolean cache) {
			super(cache);
			this.mHom = SDDHomImpl.this;
		}

		protected DD<Var, ValSet<Val>> phi(Var e, ValSet<Val> x, Map<ValSet<Val>, DD<Var, ValSet<Val>>> alpha,
				Object... parameters) {
			DD<Var, ValSet<Val>> oldSDD = null;
			DD<Var, ValSet<Val>> newSDD = SDDImpl.create(e, (DD) x, (DD) id(alpha, x));

			do {
				oldSDD = newSDD;
				newSDD = this.mHom.phi(newSDD, parameters);
			} while (newSDD != oldSDD);

			return newSDD;
		}

		public boolean isLocallyInvariant(DD<Var, ValSet<Val>> dd) {
			return SDDHomImpl.this.isLocallyInvariant(dd);
		}

		protected DD<?, ?> phi1(Object... parameters) {
			return this.mHom.phi((DD) SDDImpl.SDD_TRUE, parameters);
		}

		protected int computeHashCode() {
			return getClass().hashCode() * 1423 + this.mHom.hashCode() * 17;
		}

		protected boolean isEqual(Object that) {
			boolean eq = (this == that);
			if (!eq && that instanceof SDDHomImpl.FixPointHom) {
				FixPointHom f = (FixPointHom) that;
				if (this.mHom.equals(f.mHom)) {
					eq = true;
				}
			}
			return eq;
		}

		public String toString() {
			return "Fixpoint(" + SDDHomImpl.this.toString() + ")";
		}
	}
	
	@Override
	protected boolean isEqual(Object paramObject) {
		return this == paramObject;
	}
	
	@Override
	protected int computeHashCode() {
		return getClass().hashCode() * 4877;
	}
}
