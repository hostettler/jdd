package net.hostettler.jdd.dd;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.MapMaker;

import net.hostettler.jdd.dd.util.ArrayWrapper;
import net.hostettler.jdd.dd.util.OperationCacheWeak;

public abstract class DDImpl<VAR, VAL> implements ValSet<VAL>, DD<VAR, VAL> {

	private static Map<DD<?, ?>, DD<?, ?>> mDDRepository = new MapMaker().concurrencyLevel(4).makeMap();

	protected static final int PRIME1 = 1427;
	protected static final int PRIME2 = 3061;
	protected static final int PRIME3 = 1031;

	protected enum OP {
		RET_DD_FALSE, RET_DD_TRUE, RET_DD_ANY, RET_FIRST_OP, RET_SECOND_OP, RET_SUMTERM, RET_INTERTERM, RET_DIFFTERM;
	}

	private static OP[][] union = new OP[][] { { OP.RET_DD_FALSE, OP.RET_SECOND_OP, OP.RET_DD_ANY, OP.RET_SECOND_OP },
			{ OP.RET_FIRST_OP, OP.RET_SUMTERM, OP.RET_DD_ANY, OP.RET_DD_ANY },
			{ OP.RET_DD_ANY, OP.RET_DD_ANY, OP.RET_DD_ANY, OP.RET_DD_ANY },
			{ OP.RET_FIRST_OP, OP.RET_DD_ANY, OP.RET_DD_ANY, OP.RET_DD_ANY } };

	private static OP[][] intersection = new OP[][] {
			{ OP.RET_DD_FALSE, OP.RET_DD_FALSE, OP.RET_DD_FALSE, OP.RET_DD_FALSE },
			{ OP.RET_DD_FALSE, OP.RET_INTERTERM, OP.RET_DD_ANY, OP.RET_DD_FALSE },
			{ OP.RET_DD_FALSE, OP.RET_DD_ANY, OP.RET_DD_ANY, OP.RET_DD_ANY },
			{ OP.RET_DD_FALSE, OP.RET_DD_FALSE, OP.RET_DD_ANY, OP.RET_DD_FALSE } };

	private static OP[][] difference = new OP[][] {
			{ OP.RET_DD_FALSE, OP.RET_DD_FALSE, OP.RET_DD_FALSE, OP.RET_DD_FALSE },
			{ OP.RET_FIRST_OP, OP.RET_DIFFTERM, OP.RET_DD_ANY, OP.RET_FIRST_OP },
			{ OP.RET_DD_FALSE, OP.RET_DD_ANY, OP.RET_DD_ANY, OP.RET_DD_ANY },
			{ OP.RET_FIRST_OP, OP.RET_FIRST_OP, OP.RET_DD_ANY, OP.RET_FIRST_OP } };

	private static OperationCacheWeak<ArrayWrapper, DD<?, ?>> unionCache = new OperationCacheWeak<>();
	private static OperationCacheWeak<ArrayWrapper, DD<?, ?>> intersectionCache = new OperationCacheWeak<>();
	private static OperationCacheWeak<ArrayWrapper, DD<?, ?>> differenceCache = new OperationCacheWeak<>();

	private int mHashCode;

	private int mLevel;

	private double mNbStates;

	private String[] mProperties;

	protected static long collision = 0L;

	protected static long totalEqual = 0L;

	protected VAR mVariable;

	protected Map<VAL, DD<VAR, VAL>> mAlpha;

	private boolean mIgnoreSubtree = true;

	private boolean mIgnoreThisTree;

	protected DDImpl() {
		this.mAlpha = new HashMap<VAL, DD<VAR, VAL>>();
		this.mIgnoreThisTree = false;
	}

	protected DDImpl(VAR variable) {
		this();
		setVariable(variable);
	}

	protected DDImpl(VAR variable, double nbStates) {
		this(variable);
		this.mNbStates = nbStates;
	}

	protected DDImpl(VAR variable, double nbStates, boolean ignoreDD) {
		this(variable, nbStates);
		this.mIgnoreThisTree = ignoreDD;
	}

	protected DDImpl(VAR variable, VAL value, DD<VAR, VAL> sdd) {
		this(variable);
		addAlpha(value, sdd);
	}

	protected DDImpl(VAR variable, Map<VAL, DD<VAR, VAL>> alpha) {
		this(variable);
		this.mAlpha = alpha;
		this.mHashCode += computeSubtreeHashCode();
	}

	public VAR getVariable() {
		return this.mVariable;
	}

	public VAL getAlpha(int i) {
		return (VAL) this.mAlpha.keySet().toArray()[i];
	}

	public Set<VAL> getDomain() {
		return this.mAlpha.keySet();
	}

	public Map<VAL, DD<VAR, VAL>> getAlpha() {
		return this.mAlpha;
	}

	public DD<VAR, VAL> getAlpha(VAL value) {
		DD<VAR, VAL> object = this.mAlpha.get(value);
		if (object == null) {
			object = getFalse();
		}
		return object;
	}

	public int hashCode() {
		return this.mHashCode;
	}

	public boolean equals(Object that) {
		boolean eq = (this == that);
		if (that != null && getClass() == that.getClass()) {
			if (!eq) {
				DDImpl<VAR, VAL> thatDD = (DDImpl<VAR, VAL>) that;
				if (this.mVariable.equals(thatDD.mVariable)) {
					Set<VAL> thisValues = this.mAlpha.keySet();
					Set<VAL> thatValues = thatDD.mAlpha.keySet();
					if (thisValues.size() == thatValues.size()) {
						eq = true;
						for (VAL value : thatValues) {
							DD<VAR, VAL> thisSubtree = this.mAlpha.get(value);

							if (thisSubtree != null) {
								DD<VAR, VAL> thatSubtree = thatDD.mAlpha.get(value);

								if (thisSubtree != thatSubtree) {
									eq = false;
									break;
								}
								continue;
							}
							eq = false;
						}

					}
				}
			}
		} else {

			eq = false;
		}

		totalEqual++;
		if (!eq && that != null && hashCode() == that.hashCode()) {

			collision++;
		}

		assert !eq || (eq && hashCode() == that.hashCode());

		return eq;
	}

	protected DD<VAR, VAL> eval(OP operation, DD<VAR, VAL> operand1, DD<VAR, VAL> operand2) {
		DD<VAR, VAL> ret = null;
		switch (operation) {
		case RET_DD_TRUE:
			ret = (DD<VAR, VAL>) getTrue();

			return ret;
		case RET_DD_FALSE:
			ret = (DD<VAR, VAL>) getFalse();
			return ret;
		case RET_DD_ANY:
			ret = (DD<VAR, VAL>) getAny();
			return ret;
		case RET_FIRST_OP:
			ret = operand1;
			return ret;
		case RET_SECOND_OP:
			ret = operand2;
			return ret;
		case RET_SUMTERM:
			ret = (DD<VAR, VAL>) getTrue();
			return ret;
		case RET_INTERTERM:
			ret = (DD<VAR, VAL>) getTrue();
			return ret;
		case RET_DIFFTERM:
			ret = (DD<VAR, VAL>) getFalse();
			return ret;
		}

		throw new IllegalArgumentException();
	}

	public void addAlpha(VAL value, DD<VAR, VAL> subtree) {
		this.mAlpha.put(value, (DD<VAR, VAL>) subtree);
		this.mHashCode += computeArcHashCode(value, subtree);
	}

	public void setVariable(VAR variable) {
		this.mVariable = variable;
		this.mHashCode = computeVariableHashCode();
	}

	public ValSet<VAL> copy() {
		return copy(this);
	}

	protected abstract DD<VAR, VAL> copy(DD<VAR, VAL> paramTDD);

	private int computeVariableHashCode() {
		return this.mVariable.hashCode() ^ 0x407;
	}

	private int computeArcHashCode(VAL value, DD<VAR, VAL> subtree) {
		if (subtree.getLevel() + 1 > this.mLevel) {
			this.mLevel = subtree.getLevel() + 1;
		}
		this.mNbStates += subtree.getStates() * getValueStates(value);
		this.mIgnoreSubtree &= subtree.ignoreDD();
		return value.hashCode() ^ subtree.hashCode() * 3061;
	}

	protected double getValueStates(VAL value) {
		return 1.0D;
	}

	public int computeSubtreeHashCode() {
		int hashCode = 0;

		for (Map.Entry<VAL, DD<VAR, VAL>> entry : getAlpha().entrySet()) {
			hashCode += computeArcHashCode(entry.getKey(), entry.getValue());
		}
		return hashCode;
	}

	public int getLevel() {
		return this.mLevel;
	}

	public double getStates() {
		return this.mNbStates;
	}

	public DD<VAR, VAL> append(DD<VAR, VAL> operand) {
		return append(this, operand);
	}

	protected abstract DD<VAR, VAL> append(DD<VAR, VAL> paramTDD1, DD<VAR, VAL> paramTDD2);

	protected static <Variable, Value> DD<Variable, Value> canonicity(DD<Variable, Value> newDD) {
		DD<Variable, Value> cache = (DD<Variable, Value>) mDDRepository.get(newDD);

		if (cache == null) {
			mDDRepository.put(newDD, newDD);
			cache = newDD;
		}
		return cache;
	}


	
	public DD<VAR, VAL> intersection(DD<VAR, VAL> operand) {
		return intersection(operand, true);
	}

	public DD<VAR, VAL> intersection(DD<VAR, VAL> operand, boolean cache) {
		if (this == operand) {
			return this;
		}
		if (cache) {
			return setOperationWithCache(intersection, this, operand);
		}
		return setOperation(intersection, this, operand);
	}

	public DD<VAR, VAL> difference(DD<VAR, VAL> operand) {
		return difference(operand, true);
	}

	public DD<VAR, VAL> difference(DD<VAR, VAL> operand, boolean cache) {
		if (this == operand) {
			return (DD<VAR, VAL>) getFalse();
		}
		if (cache) {
			return setOperationWithCache(difference, this, operand);
		}
		return setOperation(difference, this, operand);
	}

	public DD<VAR, VAL> union(DD<VAR, VAL> operand) {
		return union(operand, true);
	}

	public DD<VAR, VAL> union(DD<VAR, VAL> operand, boolean cache) {
		if (this == operand) {
			return this;
		}
		if (cache) {
			return setOperationWithCache(union, this, operand);
		}
		return setOperation(union, this, operand);
	}

	public DD<VAR, VAL> setOperationWithCache(OP[][] operation, DD<VAR, VAL> operand1, DD<VAR, VAL> operand2) {
		ArrayWrapper param = new ArrayWrapper(new Object[] { operand1, operand2 });
		OperationCacheWeak<ArrayWrapper, DD<?, ?>> cache = null;

		switch (operation[3][3]) {

		case RET_DD_ANY:
			cache = unionCache;
			break;

		case RET_DD_FALSE:
			cache = intersectionCache;
			break;

		case RET_FIRST_OP:
			cache = differenceCache;
			break;
		default:
			throw new IllegalArgumentException();
		}
		DD<VAR, VAL> result = (DD<VAR, VAL>) cache.get(param);
		if (result == null) {
			result = setOperation(operation, (DD<VAR, VAL>) operand1, (DD<VAR, VAL>) operand2);
			cache.put(param, result);
		}
		return result;
	}

	protected abstract DD<VAR, VAL> setOperation(OP[][] paramArrayOfOP, DD<VAR, VAL> paramTDD1, DD<VAR, VAL> paramTDD2);

	public String toString() {
		String string = getVariable().toString();
		for (VAL x : getDomain()) {
			String arc;
			if (x instanceof DD) {
				arc = "--(" + x + ")-->";
			} else {
				arc = "--" + x + "-->";
			}

			String alpha = getAlpha(x).toString();
			string = string + arc + alpha;
			if (getDomain().size() > 1) {
				string = string + "\n";
			}
		}
		return string;
	}

	public void printInLibDDDStyle(OutputStream stream) throws IOException {
		stream.write(flatenizeRepresentation("").getBytes());
	}

	public String flatenizeRepresentation(String branchAsString) {
		String lineSeparator = System.getProperty("line.separator");
		if (this == getTrue())
			return "[ " + branchAsString + "]" + lineSeparator;
		if (this == getAny())
			return "[ " + branchAsString + " T ]" + lineSeparator;
		if (this == getFalse()) {
			return "[ " + branchAsString + " 0 ]" + lineSeparator;
		}
		String t = "";
		for (VAL val : getDomain()) {
			DD<VAR, VAL> dd = getAlpha(val);
			t = t + ((DDImpl) dd).flatenizeRepresentation(branchAsString + getVariable() + "(" + val + ") ");
		}

		return t;
	}

	public int getSize() {
		return this.mAlpha.size();
	}

	public boolean isEmpty() {
		return (getSize() == 0);
	}

	public Iterator<VAL> iterator() {
		return this.mAlpha.keySet().iterator();
	}

	public static int getNbDD() {
		return mDDRepository.size();
	}

	public boolean ignoreDD() {
		return this.mIgnoreSubtree & this.mIgnoreThisTree;
	}

	public void setIgnoreThisDD(boolean ignoreDD) {
		this.mIgnoreThisTree = ignoreDD;
		this.mIgnoreSubtree = ignoreDD;
	}

	public static void resetCache() {
		unionCache.clean();
		intersectionCache.clean();
		differenceCache.clean();
		mDDRepository.clear();
	}

	public VAL get(int i) {
		return (VAL) getAlpha().values().toArray()[i];
	}

	public static long getTotalEqual() {
		return totalEqual;
	}

	public static long getCollision() {
		return collision;
	}

	public String[] getProperties() {
		return this.mProperties;
	}

	public void setProperties(String[] properties) {
		this.mProperties = properties;
	}
}

/*
 * Location:
 * C:\Users\steve.hostettler\Downloads\alpina\eclipse_alpina\configuration\org.
 * eclipse.osgi\bundles\7\1\.cp\lib\jdd_1.1.2.jar!\c\\unige\cui\smv\dd\DDImpl.
 * class Java compiler version: 6 (50.0) JD-Core Version: 1.1.3
 */