package net.hostettler.jdd.dd.sdd;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.ValSet;

public  abstract class SimplePropagationSDDHomImpl<VAR, VAL> extends SDDHomImpl<VAR, VAL> {
	
	protected DD<VAR, ValSet<VAL>> phi1(Object... param1VarArgs) {
		return this.getTrue();
	}

	public int computeHashCode() {
		return getClass().hashCode() * 4289;
	}

	public boolean isEqual(Object param1Object) {
		return (this == param1Object);
	}
}
