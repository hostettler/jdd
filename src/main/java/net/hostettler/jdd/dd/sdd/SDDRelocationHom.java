package net.hostettler.jdd.dd.sdd;

import java.util.Map;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.ValSet;

public class SDDRelocationHom<VAR, VAL> extends SDDHomImpl<VAR, VAL> {
	private VAR mVar;

	public SDDRelocationHom(VAR var) {
		this.mVar = var;
	}

	protected DD<VAR, ValSet<VAL>> phi(VAR e, ValSet<VAL> x, Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha,
			Object... parameters) {
		if (this.mVar.equals(e)) {
			return SDDImpl.create(e, x, (DD) id(alpha, x));
		}
		return (DD<VAR, ValSet<VAL>>) (new SDDUp(e, x)).phi( phi(id(alpha, x), parameters), parameters);
	}

	protected DD<VAR, ValSet<VAL>> phi1(Object... parameters) {
		return this.getAny();
	}

	public int computeHashCode() {
		return getClass().hashCode() * 601 + this.mVar.hashCode() * 1019;
	}

	public boolean isEqual(Object that) {
		boolean eq = (this == that);
		if (!eq && that instanceof SDDRelocationHom) {
			SDDRelocationHom thatReloc = (SDDRelocationHom) that;
			eq = this.mVar.equals(thatReloc.mVar);
		}

		return eq;
	}
}
