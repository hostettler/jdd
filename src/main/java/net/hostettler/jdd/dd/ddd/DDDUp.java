package net.hostettler.jdd.dd.ddd;

import java.util.Map;

import net.hostettler.jdd.dd.DD;

public class DDDUp<VAR, VAL> extends DDDHomImpl<VAR, VAL> {
	private VAR mVar;
	private VAL mVal;

	public DDDUp(VAR var, VAL val) {
		super(true);
		this.mVar = var;
		this.mVal = val;
	}

	protected DD<VAR, VAL> phi(VAR e, VAL x, Map<VAL, DD<VAR, VAL>> alpha, Object... parameters) {
		return DDDImpl.create(e, x, DDDImpl.create(this.mVar, this.mVal, id(alpha, x)));
	}

	protected DD<VAR, VAL> phi1(Object... parameters) {
		return this.getAny();
	}

	public int computeHashCode() {
		return getClass().hashCode() * 4019 + this.mVal.hashCode() * 3727 + this.mVar.hashCode() * 3823;
	}

	public boolean isEqual(Object that) {
		boolean eq = (this == that);
		if (!eq && that instanceof DDDUp) {
			DDDUp<VAR, VAL> thatUp = (DDDUp<VAR, VAL>) that;
			eq = (this.mVar.equals(thatUp.mVar) && this.mVal.equals(thatUp.mVal));
		}

		return eq;
	}

	public String toString() {
		return "DDDUp(" + this.mVar + ", " + this.mVal + ")";
	}
}
