package ch.unige.cui.smv.dd.ddd;

import java.util.Map;

import ch.unige.cui.smv.dd.DD;

public interface Evaluator<Var, Val> {
	DD<Var, Val> evaluate(Map<Var, Val> paramMap);
}

