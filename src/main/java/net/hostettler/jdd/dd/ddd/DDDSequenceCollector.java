package net.hostettler.jdd.dd.ddd;

import java.util.HashMap;
import java.util.Map;

import net.hostettler.jdd.dd.DD;

public class DDDSequenceCollector<VAR, VAL> extends DDDHomImpl<VAR, VAL> {
	private Evaluator<VAR, VAL> mEvaluator;

	public DDDSequenceCollector(Evaluator<VAR, VAL> evaluator) {
		super(false);
		this.mEvaluator = evaluator;
	}

	protected DD<VAR, VAL> phi(VAR e, VAL x, Map<VAL, DD<VAR, VAL>> alpha, Object... parameters) {
		Map<VAR, VAL> values = getValues(parameters);
		values.put(e, x);
		return DDDImpl.create(e, x,  phi(id(alpha, x), new Object[] { values }));
	}

	protected DD<VAR, VAL> phi1(Object... parameters) {
		return this.mEvaluator.evaluate(getValues(parameters));
	}

	protected int computeHashCode() {
		return getClass().hashCode() * 2357 + this.mEvaluator.hashCode() * 1733;
	}

	@SuppressWarnings("unchecked")
	protected boolean isEqual(Object that) {
		boolean eq = (this == that);
		if (!eq && that instanceof DDDSequenceCollector) {
			DDDSequenceCollector<VAR, VAL> thatMove = (DDDSequenceCollector<VAR, VAL>) that;
			eq = this.mEvaluator.equals(thatMove.mEvaluator);
		}
		return eq;
	}

	private Map<VAR, VAL> getValues(Object... parameters) {
		Map<VAR, VAL> values = null;
		if (parameters.length == 1 && parameters[0] != null) {
			values = (Map<VAR, VAL>) parameters[0];
		} else {
			values = new HashMap<VAR, VAL>();
		}
		return values;
	}

	public String toString() {
		return "DDDSequenceCollector(" + this.mEvaluator + ")";
	}
}
