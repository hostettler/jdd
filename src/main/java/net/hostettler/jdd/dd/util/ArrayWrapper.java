package net.hostettler.jdd.dd.util;

public class ArrayWrapper {
	private Object[] mParameterArray;
	private int mHashCode;

	public ArrayWrapper(Object... parameters) {
		this.mParameterArray = parameters;
		this.mHashCode = computeArrayHashCode(this.mParameterArray);
	}

	public boolean equals(Object that) {
		boolean eq = (this == that);
		if (!eq && that instanceof ArrayWrapper) {
			ArrayWrapper thatParam = (ArrayWrapper) that;
			if (thatParam != null) {
				eq = equalsArray(this.mParameterArray, thatParam.mParameterArray);
			}
		}
		return eq;
	}

	public int hashCode() {
		return this.mHashCode;
	}

	private boolean equalsArray(Object[] array1, Object[] array2) {
		boolean eq = (array1 == array2);
		if (!eq && array1.length == array2.length) {
			eq = true;
			for (int i = 0; i < array1.length; i++) {
				Object first = array1[i];
				Object second = array2[i];

				if (first instanceof Object[]) {
					if (!equalsArray((Object[]) first, (Object[]) second)) {

						eq = false;

						break;
					}
				} else if (!first.equals(second)) {
					eq = false;

					break;
				}
			}
		}

		return eq;
	}

	private int computeArrayHashCode(Object[] array) {
		int hash = 0;
		for (Object o : array) {
			if (o != null) {
				if (o instanceof Object[]) {
					hash += 0x71 ^ computeArrayHashCode((Object[]) o);
				} else {
					hash += o.hashCode();
				}
			}
		}
		return hash;
	}
}
