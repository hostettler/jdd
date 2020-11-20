package net.hostettler.jdd.dd.ddd;

import java.util.Map;

import net.hostettler.jdd.dd.DD;

public class DDDRelocationHom<Var, Val> extends DDDHomImpl<Var, Val> {
	private Var mVar;

	public DDDRelocationHom(Var var) {
		super(true);
		this.mVar = var;
	}

	protected DD<Var, Val> phi(Var e, Val x, Map<Val, DD<Var, Val>> alpha, Object... parameters) {
		if (this.mVar.equals(e)) {
			return DDDImpl.create(e, x, id(alpha, x));
		}
		return (new DDDUp<Var, Val>(e, x)).phi( phi(id(alpha, x), parameters), parameters);
	}

	protected DD<?, ?> phi1(Object... parameters) {
		return DDDImpl.DDD_ANY;
	}

	public int computeHashCode() {
		return getClass().hashCode() * 4889 + this.mVar.hashCode() * 4153;
	}

	public boolean isEqual(Object that) {
		boolean eq = (this == that);
		if (!eq && that instanceof DDDRelocationHom) {
			DDDRelocationHom thatReloc = (DDDRelocationHom) that;
			eq = this.mVar.equals(thatReloc.mVar);
		}

		return eq;
	}
}