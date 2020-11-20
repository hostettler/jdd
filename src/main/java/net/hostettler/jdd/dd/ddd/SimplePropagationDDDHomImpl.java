package net.hostettler.jdd.dd.ddd;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.Hom;

public  abstract class SimplePropagationDDDHomImpl<Var, Val> extends DDDHomImpl<Var, Val> implements Hom<Var, Val> {
	
	protected DD<?, ?> phi1(Object... param1VarArgs) {
		return DDDImpl.DDD_TRUE;
	}

	public int computeHashCode() {
		return getClass().hashCode() * 4289;
	}

	public boolean isEqual(Object param1Object) {
		return (this == param1Object);
	}
}
