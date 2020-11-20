package net.hostettler.jdd.dd;

public interface ValSet<T> extends Iterable<T> {
	
	ValSet<T> difference(ValSet<T> paramTDataSet);
	ValSet<T> intersection(ValSet<T> paramTDataSet);
	ValSet<T> union(ValSet<T> paramTDataSet);

	ValSet<T> copy();
	int getSize();
	boolean isEmpty();
	T get(int paramInt);
	int getLevel();
}
