package net.hostettler.jdd.dd.ddd;

import java.util.Map;

import net.hostettler.jdd.dd.DD;

public class DDDIdHom<VAR, VAL> extends DDDHomImpl<VAR, VAL> {
	public DDDIdHom() {
		super(false);
	}

	protected DD<VAR, VAL> phi(VAR e, VAL x, Map<VAL, DD<VAR, VAL>> alpha, Object... parameters) {
		return DDDImpl.create(e, x, (DD<VAR, VAL>) id(alpha, x));
	}

	public boolean isLocallyInvariant(DD<VAR, VAL> dd) {
		return true;
	}

	protected int computeHashCode() {
		return getClass().hashCode() * 5009;
	}

	protected boolean isEqual(Object that) {
		boolean eq = (this == that || that instanceof DDDIdHom);
		return eq;
	}

	public String toString() {
		return "Id";
	}

}
