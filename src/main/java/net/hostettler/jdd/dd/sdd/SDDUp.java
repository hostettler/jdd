package net.hostettler.jdd.dd.sdd;

import java.util.Map;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.ValSet;

public class SDDUp<VAR, VAL> extends SDDHomImpl<VAR, VAL> {
	private VAR mVar;
	private ValSet<VAL> mVal;

	public SDDUp(VAR var, ValSet<VAL> val) {
		this.mVar = var;
		this.mVal = val;
	}

	protected DD<VAR, ValSet<VAL>> phi(VAR e, ValSet<VAL> x, Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha,
			Object... parameters) {
		return SDDImpl.create(e, x, SDDImpl.create(this.mVar, (ValSet) this.mVal, (DD<VAR, ValSet<VAL>>) id(alpha, x)));
	}

	protected DD<VAR, ValSet<VAL>> phi1(Object... parameters) {
		return this.getAny();
	}

	public int computeHashCode() {
		return getClass().hashCode() * 631 + this.mVal.hashCode() * 281 + this.mVar.hashCode() * 3581;
	}

	public boolean isEqual(Object that) {
		boolean eq = (this == that);
		if (!eq && that instanceof SDDUp) {
			SDDUp thatUp = (SDDUp) that;
			eq = (this.mVar.equals(thatUp.mVar) && this.mVal.equals(thatUp.mVal));
		}

		return eq;
	}
}
