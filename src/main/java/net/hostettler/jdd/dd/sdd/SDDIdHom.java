package net.hostettler.jdd.dd.sdd;

import java.util.Map;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.Hom;
import net.hostettler.jdd.dd.ValSet;

public class SDDIdHom<VAR, VAL> extends SDDHomImpl<VAR, VAL> implements Hom<VAR, ValSet<VAL>>{
	
	public SDDIdHom() {
		super(false);
	}

	protected DD<VAR, ValSet<VAL>> phi(VAR e, ValSet<VAL> x, Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha,
			Object... parameters) {
		return SDDImpl.create(e, x, (DD) id(alpha, x));
	}

	public boolean isLocallyInvariant(DD<VAR, ValSet<VAL>> dd) {
		return true;
	}
	
	protected DD<VAR, ValSet<VAL>> phi1(Object... parameters) {
		return this.getTrue();
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
