package ch.unige.cui.smv.dd;

public interface Hom<Var, Val> {
	DD<Var, Val> phi(DD<Var, Val> paramTDD, Object... paramVarArgs);

	Hom<Var, Val> compose(Hom<Var, Val> paramTHom);
	Hom<Var, Val> compose(Hom<Var, Val> paramTHom, boolean paramBoolean);
	Hom<Var, Val> union(Hom<Var, Val> paramTHom);
	Hom<Var, Val> union(Hom<Var, Val> paramTHom, boolean paramBoolean);
	
	Hom<Var, Val> fixpoint();
	Hom<Var, Val> fixpoint(boolean paramBoolean);
	Hom<Var, Val> saturate();
	Hom<Var, Val> saturate(boolean paramBoolean);

	boolean isLocallyInvariant(DD<Var, Val> paramDD);
}
