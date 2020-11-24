package net.hostettler.jdd.dd;

public interface ValSet<T> extends Iterable<T> {
	
	ValSet<T> difference(ValSet<T> operand);
	ValSet<T> intersection(ValSet<T> operand);
	ValSet<T> union(ValSet<T> operand);

	ValSet<T> copy();
	int getSize();
	boolean isEmpty();
	T get(int index);
	int getLevel();
}
