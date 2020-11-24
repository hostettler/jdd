package net.hostettler.jdd.dd;

import java.util.Map;

import net.hostettler.jdd.dd.util.ArrayWrapper;
import net.hostettler.jdd.dd.util.OperationCacheWeak;

public abstract class HomImpl<VAR, VAL> implements Hom<VAR, VAL> {
	private static long overallHits = 0L;

	private int mHashCode = -1;

	private static OperationCacheWeak<ArrayWrapper, DD<?, ?>> mCache = new OperationCacheWeak<>();

	private boolean mActivateCache;

	private static long collision = 0L;

	private static long totalEqual = 0L;

	protected HomImpl(boolean activateCache) {
		this.mActivateCache = activateCache;
	}

	protected abstract DD<VAR, VAL> phi1(Object... paramVarArgs);

	protected abstract DD<VAR, VAL> phiX(VAR paramVar, VAL paramVal, Map<VAL, DD<VAR, VAL>> paramMap,
			Object... paramVarArgs);

	public final DD<VAR, VAL> phi(DD<VAR, VAL> operand, Object... parameters) {
		DD<VAR, VAL> sum = null;
		overallHits++;

		if (getAny() == operand || getFalse() == operand) {
			sum = operand;
		} else {
			ArrayWrapper params = null;

			if (this.mActivateCache) {
				if (parameters.length > 0) {
					params = new ArrayWrapper(new Object[] { this, operand, parameters });
				} else {
					params = new ArrayWrapper(new Object[] { this, operand });
				}
				sum = (DD<VAR, VAL>) mCache.get(params);
			}

			if (sum == null) {
				sum = getFalse();

				if (getTrue() == operand) {
					sum = phi1(parameters);
				} else {
					VAR variable = (VAR) operand.getVariable();
					Map<VAL, DD<VAR, VAL>> alpha = operand.getAlpha();

					if (isLocallyInvariant((DD<VAR, VAL>) operand)) {
						for (VAL x : operand.getDomain()) {
							DD<VAR, VAL> phiResult = phi(id(alpha, x), parameters);

							if (phiResult != getFalse()) {
								DD<VAR, VAL> newDD = (DD<VAR, VAL>) operand.getTop();
								newDD.setIgnoreThisDD(operand.ignoreDD());
								newDD.addAlpha(x, phiResult);
								DD<VAR, VAL> dd = newDD;
								dd = DDImpl.canonicity(dd);
								sum = sum.union(dd);
							}
						}
					} else {
						for (VAL x : operand.getDomain()) {
							DD<VAR, VAL> phiResult = phiX(variable, x, alpha, parameters);
							sum = sum.union(phiResult);
						}
					}
				}

				if (this.mActivateCache) {
					mCache.put(params, sum);
				}
			}
		}
		return sum;
	}

	protected final DD<VAR, VAL> id(Map<VAL, DD<VAR, VAL>> alpha, VAL x) {
		return alpha.get(x);
	}

	public abstract Hom<VAR, VAL> compose(Hom<VAR, VAL> paramTHom);

	public abstract Hom<VAR, VAL> compose(Hom<VAR, VAL> paramTHom, boolean paramBoolean);

	public abstract Hom<VAR, VAL> union(Hom<VAR, VAL> paramTHom);

	public abstract Hom<VAR, VAL> union(Hom<VAR, VAL> paramTHom, boolean paramBoolean);

	public abstract Hom<VAR, VAL> fixpoint();

	public abstract Hom<VAR, VAL> fixpoint(boolean paramBoolean);

	public static long getCacheHits() {
		return mCache.getCacheHits();
	}

	public static long getHits() {
		return mCache.getHits();
	}

	public static long getOpInCache() {
		return mCache.getOpInCache();
	}

	public boolean isLocallyInvariant(DD<VAR, VAL> dd) {
		return false;
	}

	protected abstract DD<VAR, VAL> getAny();

	protected abstract DD<VAR, VAL> getTrue();

	protected abstract DD<VAR, VAL> getFalse();

	public static long getOverallHits() {
		return overallHits;
	}

	public static void cleanStatistics() {
		overallHits = 0L;
		collision = 0L;
		totalEqual = 0L;
		mCache.cleanStatistics();
	}

	public static void resetCache() {
		mCache.clean();
	}

	public final int hashCode() {
		if (this.mHashCode == -1) {
			this.mHashCode = computeHashCode();
		}
		return this.mHashCode;
	}

	public final boolean equals(Object that) {
		totalEqual++;
		if (that != null && !isEqual(that) && hashCode() == that.hashCode()) {
			collision++;
			isEqual(that);
			if (collision < 100L) {
				System.out.println("Collision : " + getClass());
			}
		}

		return isEqual(that);
	}

	public static long getTotalEqual() {
		return totalEqual;
	}

	public static long getCollision() {
		return collision;
	}

	protected abstract int computeHashCode();

	protected abstract boolean isEqual(Object paramObject);
}
