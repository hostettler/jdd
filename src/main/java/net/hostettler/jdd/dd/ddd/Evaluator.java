package net.hostettler.jdd.dd.ddd;

import java.util.Map;

import net.hostettler.jdd.dd.DD;

public interface Evaluator<VAR, VAL> {
	DD<VAR, VAL> evaluate(Map<VAR, VAL> paramMap);
}

