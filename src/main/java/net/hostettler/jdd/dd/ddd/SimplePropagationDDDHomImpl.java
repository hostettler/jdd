package net.hostettler.jdd.dd.ddd;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.Hom;

public  abstract class SimplePropagationDDDHomImpl<VAR, VAL> extends DDDHomImpl<VAR, VAL> implements Hom<VAR, VAL> {
	
	protected DD<VAR, VAL> phi1(Object... param1VarArgs) {
		return this.getTrue();
	}

	public int computeHashCode() {
		return getClass().hashCode() * 4289;
	}

	public boolean isEqual(Object param1Object) {
		return (this == param1Object);
	}
}
