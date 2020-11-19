package ch.unige.cui.smv.dd.sdd;

import ch.unige.cui.smv.dd.DD;
import ch.unige.cui.smv.dd.Hom;
import ch.unige.cui.smv.dd.ValSet;
import java.util.Map;

public class SDDIdHom<Var, Val> extends SDDHomImpl<Var, Val> implements Hom<Var, ValSet<Val>>{
	
	public SDDIdHom() {
		super(false);
	}

	protected DD<Var, ValSet<Val>> phi(Var e, ValSet<Val> x, Map<ValSet<Val>, DD<Var, ValSet<Val>>> alpha,
			Object... parameters) {
		return SDDImpl.create(e, x, (DD) id(alpha, x));
	}

	public boolean isLocallyInvariant(DD<Var, ValSet<Val>> dd) {
		return true;
	}
	
	protected DD<?, ?> phi1(Object... parameters) {
		return SDDImpl.SDD_TRUE;
	}

	public String toString() {
		return "Id";
	}

	protected int computeHashCode() {
		return getClass().hashCode() * 6329;
	}

	protected boolean isEqual(Object that) {
		boolean eq = (this == that);
		if (!eq && that instanceof SDDIdHom) {
			eq = true;
		}
		return eq;
	}

}
