package net.hostettler.jdd.dd.sdd;

import java.util.Map;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.Hom;
import net.hostettler.jdd.dd.ValSet;
import net.hostettler.jdd.dd.ddd.DDDImpl;

public class SDDLocalHom<VAR, VAL, DDDTVar, DDDTVal> extends SDDHomImpl<VAR, VAL> {
	
	private Hom<DDDTVar, DDDTVal> mHom;
	private VAR mVariable;

	public SDDLocalHom(Hom<DDDTVar, DDDTVal> hom, VAR variable) {
		this.mHom = hom;
		this.mVariable = variable;
	}

	protected DD<VAR, ValSet<VAL>> phi(VAR e, ValSet<VAL> x, Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha,
			Object... parameters) {
		ValSet<VAL> newX = this.mHom.phi((DD) x, new Object[0]);
		if (newX != DDDImpl.DDD_FALSE) {
			return SDDImpl.create(e,  newX,  id(alpha, x));
		}
		return (DD)SDDImpl.SDD_FALSE;
	}

	public boolean isLocallyInvariant(DD<VAR, ValSet<VAL>> dd) {
		return (this.mVariable != null && !dd.getVariable().equals(this.mVariable));
	}

	protected DD<VAR, ValSet<VAL>> phi1(Object... parameters) {
		return  getFalse();
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

	public VAR getVariable() {
		return this.mVariable;
	}
}
