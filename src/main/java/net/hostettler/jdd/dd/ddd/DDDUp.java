package net.hostettler.jdd.dd.ddd;

import java.util.Map;

import net.hostettler.jdd.dd.DD;

public class DDDUp<Var, Val> extends DDDHomImpl<Var, Val> {
	private Var mVar;
	private Val mVal;

	public DDDUp(Var var, Val val) {
		super(true);
		this.mVar = var;
		this.mVal = val;
	}

	protected DD<Var, Val> phi(Var e, Val x, Map<Val, DD<Var, Val>> alpha, Object... parameters) {
		return DDDImpl.create(e, x, DDDImpl.create(this.mVar, this.mVal, id(alpha, x)));
	}

	protected DD<?, ?> phi1(Object... parameters) {
		return DDDImpl.DDD_ANY;
	}

	public int computeHashCode() {
		return getClass().hashCode() * 4019 + this.mVal.hashCode() * 3727 + this.mVar.hashCode() * 3823;
	}

	public boolean isEqual(Object that) {
		boolean eq = (this == that);
		if (!eq && that instanceof DDDUp) {
			DDDUp thatUp = (DDDUp) that;
			eq = (this.mVar.equals(thatUp.mVar) && this.mVal.equals(thatUp.mVal));
		}

		return eq;
	}

	public String toString() {
		return "DDDUp(" + this.mVar + ", " + this.mVal + ")";
	}
}
