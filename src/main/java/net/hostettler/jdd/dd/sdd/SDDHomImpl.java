package net.hostettler.jdd.dd.sdd;

import java.util.Map;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.Hom;
import net.hostettler.jdd.dd.HomImpl;
import net.hostettler.jdd.dd.ValSet;

public abstract class SDDHomImpl<VAR, VAL> extends HomImpl<VAR, ValSet<VAL>> implements Hom<VAR, ValSet<VAL>> {
	public SDDHomImpl(boolean activateCache) {
		super(activateCache);
	}

	public SDDHomImpl() {
		super(true);
	}

	@SuppressWarnings("unchecked")
	protected DD<VAR, ValSet<VAL>> getAny() {
		return (DD<VAR, ValSet<VAL>>) SDDImpl.SDD_ANY;
	}

	@SuppressWarnings("unchecked")
	protected DD<VAR, ValSet<VAL>> getFalse() {
		return (DD<VAR, ValSet<VAL>>) SDDImpl.SDD_FALSE;
	}

	@SuppressWarnings("unchecked")
	protected DD<VAR, ValSet<VAL>> getTrue() {
		return (DD<VAR, ValSet<VAL>>) SDDImpl.SDD_TRUE;
	}

	@SuppressWarnings("unchecked")
	public static <TVar, TVal> DD<TVar, TVal> getTrue(Class<TVar> varClass, Class<TVal> valClass) {
		return (DD<TVar, TVal>) SDDImpl.SDD_TRUE;
	}
	@SuppressWarnings("unchecked")
	public static <TVar, TVal> DD<TVar, TVal> getFalse(Class<TVar> varClass, Class<TVal> valClass) {
		return (DD<TVar, TVal>) SDDImpl.SDD_FALSE;
	}
	@SuppressWarnings("unchecked")
	public static <TVar, TVal> DD<TVar, TVal> getAny(Class<TVar> varClass, Class<TVal> valClass) {
		return (DD<TVar, TVal>) SDDImpl.SDD_ANY;
	}
	
	
	protected  DD<VAR, ValSet<VAL>> phiX(VAR e, ValSet<VAL> x, Map<ValSet<VAL>,  DD<VAR, ValSet<VAL>> > alpha,
			Object... parameters) {
		return phi(e, x, alpha, parameters);
	}


	public Hom<VAR, ValSet<VAL>> compose(Hom<VAR, ValSet<VAL>>  subHom) {
		return compose(subHom, true);
	}

	public Hom<VAR, ValSet<VAL>> compose(Hom<VAR, ValSet<VAL>> subHom, boolean cache) {
		return new ComposeHom(subHom, cache);
	}

	public Hom<VAR, ValSet<VAL>>  union(Hom<VAR, ValSet<VAL>> subHom) {
		return union(subHom, true);
	}

	public Hom<VAR, ValSet<VAL>>  union(Hom<VAR, ValSet<VAL>> subHom, boolean cache) {
		return new UnionHom(subHom, cache);
	}

	public Hom<VAR, ValSet<VAL>> fixpoint() {
		return fixpoint(true);
	}

	public Hom<VAR, ValSet<VAL>> fixpoint(boolean cache) {
		return new FixPointHom(cache);
	}

	public Hom<VAR, ValSet<VAL>> saturate() {
		Hom<VAR, ValSet<VAL>> t = union(new SDDIdHom<VAR, VAL>());
		return t.fixpoint(true);
	}

	public Hom<VAR, ValSet<VAL>> saturate(boolean cache) {
		 Hom<VAR, ValSet<VAL>>  t = union(new SDDIdHom<VAR, VAL>());
		return t.fixpoint(cache);
	}

	protected abstract DD<VAR, ValSet<VAL>> phi(VAR var, ValSet<VAL> values,
			Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha, Object... parameters);

	private class ComposeHom extends SDDHomImpl<VAR, VAL> {
		private SDDHomImpl<VAR, VAL> mHomOp1;
		private SDDHomImpl<VAR, VAL> mHomOp2;

		public ComposeHom(Hom<VAR, ValSet<VAL>> subHom, boolean cache) {
			super(cache);
			this.mHomOp1 = (SDDHomImpl<VAR, VAL>) subHom;
			this.mHomOp2 = SDDHomImpl.this;
		}

		protected DD<VAR, ValSet<VAL>> phi(VAR e, ValSet<VAL> x, Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha,
				Object... parameters) {
			DD<VAR, ValSet<VAL>> sdd = SDDImpl.create(e, x, id(alpha, x));
			sdd = SDDHomImpl.this.phi(sdd, parameters);
			if (sdd != getFalse()) {
				sdd = this.mHomOp1.phi(sdd, parameters);
			}
			return sdd;
		}

		public boolean isLocallyInvariant(DD<VAR, ValSet<VAL>> dd) {
			return this.mHomOp1.isLocallyInvariant(dd) & this.mHomOp2.isLocallyInvariant(dd);
		}

		protected DD<VAR, ValSet<VAL>> phi1(Object... parameters) {
			@SuppressWarnings("unchecked")
			DD<VAR, ValSet<VAL>> sdd = (DD<VAR, ValSet<VAL>>) this.mHomOp2.phi1(parameters);
			sdd = this.mHomOp1.phi(sdd, parameters);
			return sdd;
		}

		protected int computeHashCode() {
			return getClass().hashCode() * 593 + this.mHomOp1.hashCode() * 5387 + this.mHomOp2.hashCode() * 2801;
		}

		@SuppressWarnings("unchecked")
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

	private class UnionHom extends SDDHomImpl<VAR, VAL> {
		private SDDHomImpl<VAR, VAL> mHomOp1;

		private SDDHomImpl<VAR, VAL> mHomOp2;

		private boolean mHom1isId;

		private boolean mHom2isId;

		public UnionHom(Hom<VAR, ValSet<VAL>> subHom, boolean cache) {
			super(cache);
			this.mHomOp1 = (SDDHomImpl<VAR, VAL>) subHom;
			this.mHomOp2 = SDDHomImpl.this;
			if (this.mHomOp1 instanceof SDDIdHom) {
				this.mHom1isId = true;
			} else if (this.mHomOp2 instanceof SDDIdHom) {
				this.mHom2isId = true;
			}
		}

		protected DD<VAR, ValSet<VAL>> phi(VAR e, ValSet<VAL> x, Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha,
				Object... parameters) {
			DD<VAR, ValSet<VAL>> sdd = SDDImpl.create(e, x, id(alpha, x));
			if (this.mHom1isId || this.mHom2isId) {
				DD<VAR, ValSet<VAL>> sDD1, sDD2;
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
				sdd = (DD<VAR, ValSet<VAL>>) sDD1.union(sDD2);
				return sdd;
			}
			DD<VAR, ValSet<VAL>> d1 = this.mHomOp1.phi(sdd, parameters);
			DD<VAR, ValSet<VAL>> d2 = this.mHomOp2.phi(sdd, parameters);
			sdd = d1.union(d2);

			return sdd;
		}

		protected DD<VAR, ValSet<VAL>> phi1(Object... parameters) {
			DD<VAR, ValSet<VAL>> d1 = (DD<VAR, ValSet<VAL>>) this.mHomOp1.phi1(parameters);
			DD<VAR, ValSet<VAL>> d2 = (DD<VAR, ValSet<VAL>>) this.mHomOp2.phi1(parameters);
			return d1.union(d2);
		}

		public boolean isLocallyInvariant(DD<VAR, ValSet<VAL>> dd) {
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

	private class FixPointHom extends SDDHomImpl<VAR, VAL> {
		private Hom<VAR, ValSet<VAL>> mHom;

		public FixPointHom(boolean cache) {
			super(cache);
			this.mHom = SDDHomImpl.this;
		}

		protected DD<VAR, ValSet<VAL>> phi(VAR e, ValSet<VAL> x, Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha,
				Object... parameters) {
			DD<VAR, ValSet<VAL>> oldSDD = null;
			DD<VAR, ValSet<VAL>> newSDD = SDDImpl.create(e,  x,  id(alpha, x));

			do {
				oldSDD = newSDD;
				newSDD = this.mHom.phi(newSDD, parameters);
			} while (newSDD != oldSDD);

			return newSDD;
		}

		public boolean isLocallyInvariant(DD<VAR, ValSet<VAL>> dd) {
			return SDDHomImpl.this.isLocallyInvariant(dd);
		}

		protected DD<VAR, ValSet<VAL>>phi1(Object... parameters) {
			return this.mHom.phi(this.getTrue(), parameters);
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
