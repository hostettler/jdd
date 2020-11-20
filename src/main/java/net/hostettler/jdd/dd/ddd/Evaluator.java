package net.hostettler.jdd.dd.ddd;

import java.util.Map;

import net.hostettler.jdd.dd.DD;

public interface Evaluator<Var, Val> {
	DD<Var, Val> evaluate(Map<Var, Val> paramMap);
}

