package ch.unige.cui.smv.dd.sdd;

import ch.unige.cui.smv.dd.DD;
import ch.unige.cui.smv.dd.Hom;

public  abstract class SimplePropagationSDDHomImpl<Var, Val> extends SDDHomImpl<Var, Val> {
	
	protected DD<?, ?> phi1(Object... param1VarArgs) {
		return SDDImpl.SDD_TRUE;
	}

	public int computeHashCode() {
		return getClass().hashCode() * 4289;
	}

	public boolean isEqual(Object param1Object) {
		return (this == param1Object);
	}
}
