package ch.unige.cui.smv.dd.sdd;

import java.util.Map;

import ch.unige.cui.smv.dd.DD;
import ch.unige.cui.smv.dd.ValSet;

public class SDDRelocationHom<TVar, TVal> extends SDDHomImpl<TVar, TVal> {
	private TVar mVar;

	public SDDRelocationHom(TVar var) {
		this.mVar = var;
	}

	protected DD<TVar, ValSet<TVal>> phi(TVar e, ValSet<TVal> x, Map<ValSet<TVal>, DD<TVar, ValSet<TVal>>> alpha,
			Object... parameters) {
		if (this.mVar.equals(e)) {
			return SDDImpl.create(e, x, (DD) id(alpha, x));
		}
		return (DD<TVar, ValSet<TVal>>) (new SDDUp(e, x)).phi( phi(id(alpha, x), parameters), parameters);
	}

	protected DD<?, ?> phi1(Object... parameters) {
		return SDDImpl.SDD_ANY;
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
