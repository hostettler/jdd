package net.hostettler.jdd.dd.ddd;

import java.util.Map;

import net.hostettler.jdd.dd.DD;

public class DDDRelocationHom<VAR, VAL> extends DDDHomImpl<VAR, VAL> {
	private VAR mVar;

	public DDDRelocationHom(VAR var) {
		super(true);
		this.mVar = var;
	}

	protected DD<VAR, VAL> phi(VAR e, VAL x, Map<VAL, DD<VAR, VAL>> alpha, Object... parameters) {
		if (this.mVar.equals(e)) {
			return DDDImpl.create(e, x, id(alpha, x));
		}
		return (new DDDUp<VAR, VAL>(e, x)).phi( phi(id(alpha, x), parameters), parameters);
	}

	protected DD<VAR, VAL> phi1(Object... parameters) {
		return this.getAny();
	}

	public int computeHashCode() {
		return getClass().hashCode() * 4889 + this.mVar.hashCode() * 4153;
	}

	@SuppressWarnings("unchecked")
	public boolean isEqual(Object that) {
		boolean eq = (this == that);
		if (!eq && that instanceof DDDRelocationHom) {
			DDDRelocationHom<VAR, VAL> thatReloc = (DDDRelocationHom<VAR, VAL>) that;
			eq = this.mVar.equals(thatReloc.mVar);
		}

		return eq;
	}

}