package ch.unige.cui.smv.dd.ddd;

import ch.unige.cui.smv.dd.DD;
import java.util.HashMap;
import java.util.Map;

public class DDDSequenceCollector<Var, Val> extends DDDHomImpl<Var, Val> {
	private Evaluator<Var, Val> mEvaluator;

	public DDDSequenceCollector(Evaluator<Var, Val> evaluator) {
		super(false);
		this.mEvaluator = evaluator;
	}

	protected DD<Var, Val> phi(Var e, Val x, Map<Val, DD<Var, Val>> alpha, Object... parameters) {
		Map<Var, Val> values = getValues(parameters);
		values.put(e, x);
		return DDDImpl.create(e, x,  phi(id(alpha, x), new Object[] { values }));
	}

	protected DD<?, ?> phi1(Object... parameters) {
		return this.mEvaluator.evaluate(getValues(parameters));
	}

	protected int computeHashCode() {
		return getClass().hashCode() * 2357 + this.mEvaluator.hashCode() * 1733;
	}

	protected boolean isEqual(Object that) {
		boolean eq = (this == that);
		if (!eq && that instanceof DDDSequenceCollector) {
			DDDSequenceCollector thatMove = (DDDSequenceCollector) that;
			eq = this.mEvaluator.equals(thatMove.mEvaluator);
		}
		return eq;
	}

	private Map<Var, Val> getValues(Object... parameters) {
		Map<Var, Val> values = null;
		if (parameters.length == 1 && parameters[0] != null) {
			values = (Map<Var, Val>) parameters[0];
		} else {
			values = new HashMap<Var, Val>();
		}
		return values;
	}

	public String toString() {
		return "DDDSequenceCollector(" + this.mEvaluator + ")";
	}
}
