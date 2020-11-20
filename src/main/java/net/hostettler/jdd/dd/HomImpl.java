package net.hostettler.jdd.dd;

import java.util.Map;

import net.hostettler.jdd.dd.util.ArrayWrapper;
import net.hostettler.jdd.dd.util.OperationCacheWeak;

public abstract class HomImpl<Var, Val> implements Hom<Var, Val> {
	private static long overallHits = 0L;

	private int mHashCode = -1;

	private static OperationCacheWeak<ArrayWrapper, DD<?,?>> mCache = new OperationCacheWeak<>();

	private boolean mActivateCache;

	private static long collision = 0L;

	private static long totalEqual = 0L;

	protected HomImpl(boolean activateCache) {
		this.mActivateCache = activateCache;
	}

	protected abstract DD<?, ?> phi1(Object... paramVarArgs);

	protected abstract DD<Var, Val> phiX(Var paramVar, Val paramVal, Map<Val, DD<Var, Val>> paramMap,
			Object... paramVarArgs);

	public final DD<Var, Val> phi(DD<Var, Val> operand, Object... parameters) {
		DD<Var, Val> sum = null;
		overallHits++;

		if (getDDAny() == operand || getDDFalse() == operand) {
			sum = operand;
		} else {
			ArrayWrapper params = null;

			if (this.mActivateCache) {
				if (parameters.length > 0) {
					params = new ArrayWrapper(new Object[] { this, operand, parameters });
				} else {
					params = new ArrayWrapper(new Object[] { this, operand });
				}
				sum = (DD<Var, Val>) mCache.get(params);
			}

			if (sum == null) {
				sum = getDDFalse();

				if (getDDTrue() == operand) {
					sum = (DD) phi1(parameters);
				} else {
					Var variable = (Var) operand.getVariable();
					Map<Val, DD<Var, Val>> alpha = operand.getAlpha();

					if (isLocallyInvariant((DD<Var, Val>) operand)) {
						for (Val x : operand.getDomain()) {
							DD<Var, Val> phiResult = phi((DD) id(alpha, x), parameters);

							if (phiResult != getDDFalse()) {
								DD<Var, Val> newDD = (DD<Var, Val>) operand.getTop();
								newDD.setIgnoreThisDD(operand.ignoreDD());
								newDD.addAlpha(x, phiResult);
								DD<Var, Val> dd = newDD;
								dd = DDImpl.canonicity(dd);
								sum = sum.union(dd);
							}
						}
					} else {
						for (Val x : operand.getDomain()) {
							DD<Var, Val> phiResult = phiX(variable, x, alpha, parameters);
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

	protected final  DD<Var, Val> id(Map<Val, DD<Var, Val>> alpha, Val x) {
		return alpha.get(x);
	}

	public abstract Hom<Var, Val> compose(Hom<Var, Val> paramTHom);

	public abstract Hom<Var, Val> compose(Hom<Var, Val> paramTHom, boolean paramBoolean);

	public abstract Hom<Var, Val> union(Hom<Var, Val> paramTHom);

	public abstract Hom<Var, Val> union(Hom<Var, Val> paramTHom, boolean paramBoolean);

	public abstract Hom<Var, Val> fixpoint();

	public abstract Hom<Var, Val> fixpoint(boolean paramBoolean);

	public static long getCacheHits() {
		return mCache.getCacheHits();
	}

	public static long getHits() {
		return mCache.getHits();
	}

	public static long getOpInCache() {
		return mCache.getOpInCache();
	}

	public boolean isLocallyInvariant(DD<Var, Val> dd) {
		return false;
	}

	protected abstract DD<Var, Val> getDDAny();
	protected abstract DD<Var, Val> getDDTrue();
	protected abstract DD<Var, Val> getDDFalse();

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
		if (!isEqual(that) && hashCode() == that.hashCode()) {
			collision++;
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
