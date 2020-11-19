package ch.unige.cui.smv.dd.sdd;

import java.util.Map;

import ch.unige.cui.smv.dd.DD;
import ch.unige.cui.smv.dd.Hom;
import ch.unige.cui.smv.dd.ValSet;
import ch.unige.cui.smv.dd.ddd.DDDImpl;

public class SDDLocalHom<Var, Val, DDDTVar, DDDTVal> extends SDDHomImpl<Var, Val> {
	
	private Hom<DDDTVar, DDDTVal> mHom;
	private Var mVariable;

	public SDDLocalHom(Hom<DDDTVar, DDDTVal> hom, Var variable) {
		this.mHom = hom;
		this.mVariable = variable;
	}

	protected DD<Var, ValSet<Val>> phi(Var e, ValSet<Val> x, Map<ValSet<Val>, DD<Var, ValSet<Val>>> alpha,
			Object... parameters) {
		ValSet<Val> newX = this.mHom.phi((DD) x, new Object[0]);
		if (newX != DDDImpl.DDD_FALSE) {
			return SDDImpl.create(e,  newX,  id(alpha, x));
		}
		return (DD)SDDImpl.SDD_FALSE;
	}

	public boolean isLocallyInvariant(DD<Var, ValSet<Val>> dd) {
		return (this.mVariable != null && !dd.getVariable().equals(this.mVariable));
	}

	protected DD<?, ?> phi1(Object... parameters) {
		return  getDDFalse();
	}

	protected int computeHashCode() {
		return getClass().hashCode() * 1559 + this.mHom.hashCode() * 719
				+ ((this.mVariable == null) ? 0 : (this.mVariable.hashCode() * 1373));
	}

	protected boolean isEqual(Object that) {
		boolean eq = (this == that);
		if (!eq && that instanceof SDDLocalHom) {
			SDDLocalHom thatMove = (SDDLocalHom) that;
			eq = (this.mHom.equals(thatMove.mHom) && this.mVariable != null
					&& this.mVariable.equals(thatMove.mVariable));
		}

		return eq;
	}

	public String toString() {
		return "local(" + this.mHom.toString() + ", " + this.mVariable.toString() + ")";
	}

	public Hom<DDDTVar, DDDTVal> getHom() {
		return this.mHom;
	}

	public Var getVariable() {
		return this.mVariable;
	}
}
