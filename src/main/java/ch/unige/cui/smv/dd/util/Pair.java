package ch.unige.cui.smv.dd.util;

import java.io.Serializable;

public class Pair<A, B> implements Serializable {
	private static final long serialVersionUID = -6400359206287322210L;
	private final A mFirst;
	private final B mSecond;

	public Pair(A first, B second) {
		this.mFirst = first;
		this.mSecond = second;
	}

	public String toString() {
		return "Pair[" + getFirst() + "," + getSecond() + "]";
	}

	private static boolean equals(Object x, Object y) {
		return ((x == null && y == null) || (x != null && x.equals(y)));
	}

	public boolean equals(Object other) {
		return (other instanceof Pair && equals(getFirst(), ((Pair<?, ?>) other).getFirst())
				&& equals(getSecond(), ((Pair<?, ?>) other).getSecond()));
	}

	public int hashCode() {
		if (getFirst() == null)
			return (getSecond() == null) ? 0 : (getSecond().hashCode() + 1);
		if (getSecond() == null) {
			return getFirst().hashCode() + 2;
		}
		return getFirst().hashCode() * 17 + getSecond().hashCode();
	}

	public static <A, B> Pair<A, B> of(A a, B b) {
		return new Pair<A, B>(a, b);
	}

	public A getFirst() {
		return this.mFirst;
	}

	public B getSecond() {
		return this.mSecond;
	}
}
