package ch.unige.cui.smv.dd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class ObjSet<T> implements ValSet<T>, Iterable<T> {
	private static Map<ObjSet<? extends Object>, ObjSet<? extends Object>> mUniqueTable = new HashMap<>();

	private Set<T> mValues;

	private T[] mArray;

	private int mHashCode;

	private static ObjSet<Object> mEmptySet = new ObjSet<>();

	private ObjSet() {
		this.mValues = new HashSet<T>();
	}

	private ObjSet(T value) {
		this.mValues = new HashSet<T>();
		this.mValues.add(value);
	}

	private ObjSet(ObjSet<T> objectSet) {
		this.mValues = new HashSet<T>();
		this.mValues.addAll(objectSet.mValues);
	}

	public static <T> ObjSet<T> create(Set<T> values) {
		ObjSet<T> set = new ObjSet<T>();
		for (T t : values) {
			set.add(t);
		}
		set = canonical(set);
		return set;
	}

	public static <T> ObjSet<T> create(T... values) {
		ObjSet<T> set = new ObjSet<T>();
		for (T t : values) {
			set.add(t);
		}
		set = canonical(set);
		return set;
	}

	public static <T> ObjSet<T> create(T value) {
		ObjSet<T> set = new ObjSet<T>(value);
		set = canonical(set);
		return set;
	}

	public static <T> ObjSet<T> createEmtpy() {
		return (ObjSet<T>) mEmptySet;
	}

	public boolean equals(Object that) {
		boolean eq = (this == that);
		if (!eq && hashCode() == that.hashCode() && that instanceof ObjSet) {
			ObjSet<T> thatSet = (ObjSet<T>) that;
			if (this.mValues.size() == thatSet.mValues.size()) {
				eq = true;
				for (T v : this.mValues) {
					if (!thatSet.mValues.contains(v)) {
						eq = false;

						break;
					}
				}
			}
		}
		return eq;
	}

	public int hashCode() {
		return this.mHashCode;
	}

	public int computHashCode() {
		int hash = 0;
		for (T v : this.mValues) {
			hash += v.hashCode();
		}
		return hash;
	}

	public String toString() {
		String string = "{";
		int i = 0;
		for (T v : this.mValues) {
			string = string + v.toString();
			if (++i < this.mValues.size()) {
				string = string + ", ";
			}
		}
		string = string + "}";
		return string;
	}

	public ValSet<T> difference(ValSet<T> op) {
		if (this == op)
			return (ValSet<T>) mEmptySet;
		return (ValSet<T>) canonical(removeAll((ObjSet<T>) op));
	}

	public  ValSet<T> intersection(ValSet<T> op) {
		if (this == op)
			return (ValSet<T>) this;
		ObjSet<T> set = new ObjSet<>();
		ObjSet<T> operand = (ObjSet<T>) op;
		for (T v : operand) {
			if (contains(v)) {
				set.add(v);
			}
		}
		return (ValSet<T>) canonical(set);
	}

	public ValSet<T> union(ValSet<T> op) {
		if (this == op)
			return (ValSet<T>) this;
		return (ValSet<T>) canonical(addAll((ObjSet<T>) op));
	}

	private ObjSet<T> removeAll(ObjSet<T> op) {
		if (this == op)
			return this;
		ObjSet<T> set = new ObjSet<>(this);
		for (T v : op) {
			set.remove(v);
		}
		return canonical(set);
	}

	private static <T> ObjSet<T> canonical(ObjSet<T> set) {
		if (set.getSize() == 0)
			return (ObjSet) mEmptySet;
		set.mHashCode = set.computHashCode();
		ObjSet<T> cache = (ObjSet<T>) mUniqueTable.get(set);
		if (cache == null) {
			cache = set;
			set.mArray = (T[]) set.mValues.toArray();
			mUniqueTable.put(set, set);
		}
		return cache;
	}

	private void add(T v) {
		this.mValues.add(v);
	}

	private void remove(T v) {
		this.mValues.remove(v);
	}

	public boolean contains(T v) {
		return this.mValues.contains(v);
	}

	private ObjSet<T> addAll(ObjSet<T> op) {
		ObjSet<T> set = new ObjSet<>(this);
		for (T v : op) {
			set.add(v);
		}
		return canonical(set);
	}

	public Iterator<T> iterator() {
		return this.mValues.iterator();
	}

	
	public int getSize() {
		return this.mValues.size();
	}

	public boolean isEmpty() {
		return (getSize() == 0);
	}

	public T get(int i) {
		return this.mArray[i];
	}

	public ObjSet<T> copy() {
		return new ObjSet<>(this);
	}

	public int getLevel() {
		return 1;
	}
}
