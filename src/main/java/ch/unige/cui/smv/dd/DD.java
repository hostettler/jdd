package ch.unige.cui.smv.dd;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DD<Var, Val> extends ValSet<Val> {
	DD<Var, Val> getDDTrue();
	DD<Var, Val> getDDFalse();
	DD<Var, Val> getDDAny();

	Var getVariable();
	void setVariable(Var paramVar);

	Set<Val> getDomain();

	void addAlpha(Val paramVal, DD<Var, Val> paramTDD);
	Val getAlpha(int paramInt);
	Map<Val, DD<Var, Val>> getAlpha();
	DD<Var, Val> getAlpha(Val paramVal);

	DD<Var, Val> union(DD<Var, Val> paramTDD);
	DD<Var, Val> union(DD<Var, Val> paramTDD, boolean paramBoolean);
	DD<Var, Val> difference(DD<Var, Val> paramTDD);
	DD<Var, Val> difference(DD<Var, Val> paramTDD, boolean paramBoolean);
	DD<Var, Val> intersection(DD<Var, Val> paramTDD);
	DD<Var, Val> intersection(DD<Var, Val> paramTDD, boolean paramBoolean);
	DD<Var, Val> append(DD<Var, Val> paramTDD);

	double getStates();
	void printInLibDDDStyle(OutputStream paramOutputStream) throws IOException;
	DD<Var, Val> getTop();
	boolean ignoreDD();
	void setIgnoreThisDD(boolean paramBoolean);
	String[] getProperties();
	void setProperties(String[] paramArrayOfString);
	List<?> split();
}
