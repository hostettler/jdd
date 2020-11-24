package net.hostettler.jdd.dd;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DD<VAR, VAL> extends ValSet<VAL> {
	DD<VAR, VAL> getTrue();
	DD<VAR, VAL> getFalse();
	DD<VAR, VAL> getAny();

	VAR getVariable();
	void setVariable(VAR variable);

	Set<VAL> getDomain();

	void addAlpha(VAL paramVal, DD<VAR, VAL> dd);
	VAL getAlpha(int paramInt);
	Map<VAL, DD<VAR, VAL>> getAlpha();
	DD<VAR, VAL> getAlpha(VAL value);

	DD<VAR, VAL> union(DD<VAR, VAL> operand);
	DD<VAR, VAL> union(DD<VAR, VAL> operand, boolean isCached);
	DD<VAR, VAL> difference(DD<VAR, VAL> operand);
	DD<VAR, VAL> difference(DD<VAR, VAL> operand, boolean isCached);
	DD<VAR, VAL> intersection(DD<VAR, VAL> operand);
	DD<VAR, VAL> intersection(DD<VAR, VAL> operand, boolean isCached);
	DD<VAR, VAL> append(DD<VAR, VAL> operand);

	double getStates();
	void printInLibDDDStyle(OutputStream paramOutputStream) throws IOException;
	DD<VAR, VAL> getTop();
	boolean ignoreDD();
	void setIgnoreThisDD(boolean paramBoolean);
	String[] getProperties();
	void setProperties(String[] paramArrayOfString);
	List<?> split();
}
