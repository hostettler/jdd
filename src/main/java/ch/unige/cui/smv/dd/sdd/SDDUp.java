package ch.unige.cui.smv.dd.sdd;

import ch.unige.cui.smv.dd.DD;
import ch.unige.cui.smv.dd.ValSet;
import java.util.Map;

public class SDDUp<TVar, TVal> extends SDDHomImpl<TVar, TVal> {
	private TVar mVar;
	private ValSet<TVal> mVal;

	public SDDUp(TVar var, ValSet<TVal> val) {
		this.mVar = var;
		this.mVal = val;
	}

	protected DD<TVar, ValSet<TVal>> phi(TVar e, ValSet<TVal> x, Map<ValSet<TVal>, DD<TVar, ValSet<TVal>>> alpha,
			Object... parameters) {
		return SDDImpl.create(e, x, SDDImpl.create(this.mVar, (ValSet) this.mVal, (DD<TVar, ValSet<TVal>>) id(alpha, x)));
	}

	protected DD<?, ?> phi1(Object... parameters) {
		return SDDImpl.SDD_ANY;
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
