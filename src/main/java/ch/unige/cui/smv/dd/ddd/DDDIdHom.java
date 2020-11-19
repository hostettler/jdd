package ch.unige.cui.smv.dd.ddd;

import ch.unige.cui.smv.dd.DD;
import java.util.Map;

public class DDDIdHom<Var, Val> extends DDDHomImpl<Var, Val> {
	public DDDIdHom() {
		super(false);
	}

	protected DD<Var, Val> phi(Var e, Val x, Map<Val, DD<Var, Val>> alpha, Object... parameters) {
		return DDDImpl.create(e, x, (DD<Var, Val>) id(alpha, x));
	}

	public boolean isLocallyInvariant(DD<Var, Val> dd) {
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
