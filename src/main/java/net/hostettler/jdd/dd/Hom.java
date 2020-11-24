package net.hostettler.jdd.dd;

public interface Hom<VAR, VAL> {
	DD<VAR, VAL> phi(DD<VAR, VAL> operand, Object... parameters);

	Hom<VAR, VAL> compose(Hom<VAR, VAL> operand);
	Hom<VAR, VAL> compose(Hom<VAR, VAL> operand, boolean isCached);
	Hom<VAR, VAL> union(Hom<VAR, VAL> operand);
	Hom<VAR, VAL> union(Hom<VAR, VAL> operand, boolean isCached);
	
	Hom<VAR, VAL> fixpoint();
	Hom<VAR, VAL> fixpoint(boolean isCached);
	Hom<VAR, VAL> saturate();
	Hom<VAR, VAL> saturate(boolean isCached);

	boolean isLocallyInvariant(DD<VAR, VAL> operand);
}
