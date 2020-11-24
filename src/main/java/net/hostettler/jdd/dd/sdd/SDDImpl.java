package net.hostettler.jdd.dd.sdd;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.DDImpl;
import net.hostettler.jdd.dd.ObjSet;
import net.hostettler.jdd.dd.Terminator;
import net.hostettler.jdd.dd.ValSet;

public final class SDDImpl<VAR, VAL> extends DDImpl<VAR, ValSet<VAL>> implements DD<VAR, ValSet<VAL>> {

	private static AppendHom mAppendHom = new AppendHom<Object, Object>();

	private static Map<DD<?, ?>, List<DD<?, ?>>> mSplitCache = new HashMap<>();

	private static final int MIN_CACHE_SPLIT_THRESHOLD = 50;
	public static final DD<?, ?> SDD_ANY = new SDDImpl(Terminator.ANY);
	public static final DD<?, ?> SDD_FALSE = new SDDImpl(Terminator.FALSE);
	public static final DD<?, ?> SDD_TRUE = new SDDImpl(Terminator.TRUE, 1.0D);

	private SDDImpl(VAR variable) {
		super(variable);
	}

	private SDDImpl(VAR variable, double nbStates) {
		super(variable, nbStates);
	}

	private SDDImpl(VAR variable, double nbStates, boolean ignoreDD) {
		super(variable, nbStates, ignoreDD);
	}

	private SDDImpl(VAR variable, Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha) {
		super(variable, alpha);
	}

	private SDDImpl(VAR variable, ValSet<VAL> value, DD<VAR, ValSet<VAL>> sdd) {
		super(variable, value, sdd);
	}

	private SDDImpl(VAR variable, ValSet<VAL> value) {
		super(variable, value, (DD) SDD_TRUE);
	}

	private SDDImpl() {
	}

	public static <Var, Val> DD<Var, ValSet<Val>> create(Var variable, ValSet<Val> value) {
		return (DD<Var, ValSet<Val>>) canonicity(new SDDImpl(variable, value, SDD_TRUE));
	}

	public static <Var, Val> DD<Var, ValSet<Val>> create(Var variable, DD<Var, ValSet<Val>> value) {
		return (DD<Var, ValSet<Val>>) canonicity(new SDDImpl(variable, value, SDD_TRUE));
	}

	public static <Var, Val> DD<Var, ValSet<Val>> create(Var variable, ValSet<Val> value, DD<Var, ValSet<Val>> sdd) {
		if (sdd == SDD_FALSE) {
			return (DD) sdd;
		}
		DD<Var, Val> newSDD = new SDDImpl(variable, value, sdd);
		return (DD<Var, ValSet<Val>>) canonicity(newSDD);
	}

	protected DD<VAR, ValSet<VAL>> append(DD<VAR, ValSet<VAL>> operand1, DD<VAR, ValSet<VAL>> operand2) {
		DD<VAR, ValSet<VAL>> dD = mAppendHom.phi(operand1, new Object[] { operand2 });
		dD.setIgnoreThisDD(operand1.ignoreDD() & operand2.ignoreDD());
		return dD;
	}

	protected double getValueStates(ValSet<VAL> value) {
		if (value instanceof DD) {
			return ((DD) value).getStates();
		}
		return value.getSize();
	}

	protected DD<VAR, ValSet<VAL>> setOperation(DDImpl.OP[][] operation, DD<VAR, ValSet<VAL>> operand1,
			DD<VAR, ValSet<VAL>> operand2) {
		if (operand1 == operand2) {
			switch (operation[3][3]) {

			case RET_DD_ANY:
				return operand1;

			case RET_DD_FALSE:
				return operand1;

			case RET_FIRST_OP:
				return getFalse();
			}
			throw new IllegalArgumentException();
		}

		DD<VAR, ValSet<VAL>> set = null;

		if (getFalse() == operand1) {
			if (getFalse() == operand2) {

				set = eval(operation[0][0], operand1, operand2);
			} else if (getTrue() == operand2) {

				set = eval(operation[0][1], operand1, operand2);
			} else if (getAny() == operand2) {

				set = eval(operation[0][2], operand1, operand2);
			} else {

				set = eval(operation[0][3], operand1, operand2);
			}
		} else if (getTrue() == operand1) {
			if (getFalse() == operand2) {
				set = eval(operation[1][0], operand1, operand2);
			} else if (getTrue() == operand2) {
				set = eval(operation[1][1], operand1, operand2);
			} else if (getAny() == operand2) {
				set = eval(operation[1][2], operand1, operand2);
			} else {
				set = eval(operation[1][3], operand1, operand2);
			}
		} else if (getAny() == operand1) {
			if (getFalse() == operand2) {
				set = eval(operation[2][0], operand1, operand2);
			} else if (getTrue() == operand2) {
				set = eval(operation[2][1], operand1, operand2);
			} else if (getAny() == operand2) {
				set = eval(operation[2][2], operand1, operand2);
			} else {
				set = eval(operation[2][3], operand1, operand2);
			}

		} else if (getFalse() == operand2) {
			set = eval(operation[3][0], operand1, operand2);
		} else if (getTrue() == operand2) {
			set = eval(operation[3][1], operand1, operand2);
		} else if (getAny() == operand2) {
			set = eval(operation[3][2], operand1, operand2);

		} else if (!operand1.getVariable().equals(operand2.getVariable())) {

			set = eval(operation[3][3], operand1, operand2);

		} else {

			set = getFalse();
			VAR variable = (VAR) operand2.getVariable();

			Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha1 = operand1.getAlpha();
			Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha2 = operand2.getAlpha();
			Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha = new HashMap<ValSet<VAL>, DD<VAR, ValSet<VAL>>>();

			Map<DD<VAR, VAL>, ValSet<VAL>> result = new HashMap<DD<VAR, VAL>, ValSet<VAL>>();

			switch (operation[3][3]) {

			case RET_DD_ANY:
				union(result, alpha1, alpha2);
				break;

			case RET_DD_FALSE:
				inter(result, alpha1, alpha2);
				break;

			case RET_FIRST_OP:
				diff(result, alpha1, alpha2);
				break;
			default:
				throw new IllegalArgumentException();
			}

			for (Map.Entry<DD<VAR, VAL>, ValSet<VAL>> d : result.entrySet()) {

				if (d.getKey() != SDD_FALSE) {
					alpha.put((ValSet) d.getValue(), (DD) d.getKey());
				}
			}
			if (alpha.size() > 0) {

				set = canonicity(new SDDImpl(variable, alpha));

			} else {
				set = getFalse();
			}
			set.setIgnoreThisDD(operand1.ignoreDD() & operand2.ignoreDD());
		}

		return set;
	}

	private void union(Map<DD<VAR, VAL>, ValSet<VAL>> result, Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha1,
			Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha2) {
		for (Map.Entry<ValSet<VAL>, DD<VAR, ValSet<VAL>>> a : alpha1.entrySet()) {
			result.put((DD<VAR, VAL>) a.getValue(), a.getKey());
		}

		Map<DD<VAR, VAL>, ValSet<VAL>> sums = new HashMap<DD<VAR, VAL>, ValSet<VAL>>();
		Map<DD<VAR, VAL>, ValSet<VAL>> rems = new HashMap<DD<VAR, VAL>, ValSet<VAL>>();

		List<DD<VAR, VAL>> toRemove = new ArrayList<DD<VAR, VAL>>();

		for (Map.Entry<ValSet<VAL>, DD<VAR, ValSet<VAL>>> aa : alpha2.entrySet()) {
			ValSet<VAL> a = aa.getKey();
			DD<VAR, VAL> da = (DD<VAR, VAL>) aa.getValue();
			ValSet<VAL> remainOfA = aa.getKey();

			for (Map.Entry<DD<VAR, VAL>, ValSet<VAL>> ddb : result.entrySet()) {

				ValSet<VAL> b = ddb.getValue();
				DD<VAR, VAL> db = ddb.getKey();
				ValSet<VAL> remainOfB = b;

				ValSet<VAL> aInterB = a.intersection(b);
				if (!aInterB.isEmpty()) {

					DD<VAR, VAL> daUdb = (DD<VAR, VAL>) da.union(db);
					ValSet<VAL> oldInter = sums.get(daUdb);
					if (oldInter != null) {
						sums.put(daUdb, aInterB.union(oldInter));
					} else {
						sums.put(daUdb, aInterB);
					}

					remainOfA = remainOfA.difference(aInterB);

					remainOfB = remainOfB.difference(aInterB);
					result.put(db, remainOfB);
					if (remainOfB.isEmpty()) {
						toRemove.add(db);
					}
				}
			}

			if (!remainOfA.isEmpty()) {
				rems.put(da, remainOfA);
			}

			for (DD<VAR, VAL> d : toRemove) {
				result.remove(d);
			}
		}

		for (Map.Entry<DD<VAR, VAL>, ValSet<VAL>> dd : rems.entrySet()) {
			squareUnion(result, dd.getKey(), dd.getValue());
		}

		for (Map.Entry<DD<VAR, VAL>, ValSet<VAL>> dd : sums.entrySet()) {
			squareUnion(result, dd.getKey(), dd.getValue());
		}
	}

	private void inter(Map<DD<VAR, VAL>, ValSet<VAL>> result, Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha1,
			Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha2) {
		for (Map.Entry<ValSet<VAL>, DD<VAR, ValSet<VAL>>> aa : alpha1.entrySet()) {
			DD<VAR, VAL> da = (DD<VAR, VAL>) aa.getValue();
			ValSet<VAL> a = aa.getKey();

			for (Map.Entry<ValSet<VAL>, DD<VAR, ValSet<VAL>>> bb : alpha2.entrySet()) {
				ValSet<VAL> b = bb.getKey();
				ValSet<VAL> aInterB = a.intersection(b);
				if (!aInterB.isEmpty()) {
					DD<VAR, VAL> db = (DD<VAR, VAL>) bb.getValue();
					DD<VAR, VAL> daInterDb = (DD<VAR, VAL>) da.intersection(db);
					squareUnion(result, daInterDb, aInterB);
				}
			}
		}
	}

	private void diff(Map<DD<VAR, VAL>, ValSet<VAL>> result, Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha1,
			Map<ValSet<VAL>, DD<VAR, ValSet<VAL>>> alpha2) {
		Map<DD<VAR, VAL>, ValSet<VAL>> rems = new HashMap<DD<VAR, VAL>, ValSet<VAL>>();

		for (Map.Entry<ValSet<VAL>, DD<VAR, ValSet<VAL>>> aa : alpha1.entrySet()) {
			rems.put((DD<VAR, VAL>) aa.getValue(), aa.getKey());
		}

		for (Map.Entry<ValSet<VAL>, DD<VAR, ValSet<VAL>>> aa : alpha1.entrySet()) {
			DD<VAR, VAL> da = (DD<VAR, VAL>) aa.getValue();
			ValSet<VAL> a = aa.getKey();

			for (Map.Entry<ValSet<VAL>, DD<VAR, ValSet<VAL>>> bb : alpha2.entrySet()) {
				ValSet<VAL> b = bb.getKey();
				ValSet<VAL> aInterB = a.intersection(b);
				if (!aInterB.isEmpty()) {

					DD<VAR, VAL> db = (DD<VAR, VAL>) bb.getValue();
					DD<VAR, VAL> daMinusDb = (DD<VAR, VAL>) da.difference(db);
					squareUnion(result, daMinusDb, aInterB);

					ValSet<VAL> restA = rems.get(da);
					restA = restA.difference(aInterB);
					rems.put(da, restA);
				}
			}
		}

		for (Map.Entry<DD<VAR, VAL>, ValSet<VAL>> d : rems.entrySet()) {
			ValSet<VAL> values = d.getValue();
			if (!values.isEmpty()) {
				squareUnion(result, d.getKey(), values);
			}
		}
	}

	protected DD<VAR, ValSet<VAL>> eval(DDImpl.OP operation, DD<VAR, ValSet<VAL>> operand1,
			DD<VAR, ValSet<VAL>> operand2) {
		return super.eval(operation, operand1, operand2);
	}

	protected DD<VAR, ValSet<VAL>> copy(DD<VAR, ValSet<VAL>> tree) {
		DD<VAR, ValSet<VAL>> tDD;

		if (getTrue() == tree || getFalse() == tree || getAny() == tree) {

			tDD = tree;
		} else {

			SDDImpl<VAR, VAL> sDDImpl = new SDDImpl();
			sDDImpl.setVariable(tree.getVariable());
			for (ValSet<VAL> value : (Iterable<ValSet<VAL>>) tree.getDomain()) {
				DD<VAR, ValSet<VAL>> subtree = tree.getAlpha(value);
				sDDImpl.addAlpha(value, subtree);
			}
			tDD = sDDImpl;
		}
		return tDD;
	}

	private void squareUnion(Map<DD<VAR, VAL>, ValSet<VAL>> reverseAlpha, DD<VAR, VAL> sdd, ValSet<VAL> values) {
		ValSet<VAL> a = reverseAlpha.get(sdd);
		if (a != null) {
			values = values.union(a);
		}
		reverseAlpha.put(sdd, values);
	}

	public List<DD<VAR, VAL>> split() {
		if (getStates() < 2.0D) {
			List<SDDImpl<VAR, VAL>> l = new ArrayList<>();
			l.add(this);
			return (List) l;
		}
		if (getStates() < 50.0D) {
			return (List) split((ValSet) this);
		}
		List<DD<?, ?>> split = mSplitCache.get(this);
		if (split == null) {
			split = (List) split((ValSet) this);
			mSplitCache.put(this, split);
		}
		return (List) split;
	}

	private List<ValSet<VAL>> split(ValSet<VAL> valset2split) {
		List<ValSet<VAL>> valList = new ArrayList<ValSet<VAL>>();

		if (valset2split instanceof SDDImpl) {
			DD<VAR, VAL> sdd2split = (SDDImpl) valset2split;
			VAR variable = (VAR) sdd2split.getVariable();

			if (sdd2split == SDD_TRUE || sdd2split == SDD_FALSE || sdd2split == SDD_ANY) {

				valList.add(sdd2split);
			} else {

				for (Map.Entry<VAL, DD<VAR, VAL>> entry : sdd2split.getAlpha().entrySet()) {
					VAL a = entry.getKey();
					DD<VAR, VAL> d = entry.getValue();

					List<ValSet<VAL>> sons = split(d);
					for (ValSet<VAL> son : sons) {
						List<ValSet<VAL>> vals = split((ValSet) a);
						for (ValSet<VAL> val : vals) {
							valList.add((ValSet) create(variable, val, (SDDImpl) son));
						}
					}
				}
			}
		} else {

			for (VAL val : valset2split) {
				valList.add(ObjSet.create(val));
			}
		}

		return valList;
	}

	public void printInLibDDDStyle(OutputStream stream) throws IOException {
		stream.write(flatenizeRepresentation("").getBytes());
	}

	public String flatenizeRepresentation(String branchAsString) {
		String lineSeparator = System.getProperty("line.separator");
		if (this == getTrue())
			return "[ " + branchAsString + "]" + lineSeparator;
		if (this == getAny())
			return "[ " + branchAsString + " T ]" + lineSeparator;
		if (this == getFalse()) {
			return "[ " + branchAsString + " 0 ]" + lineSeparator;
		}
		String t = "";
		for (ValSet<VAL> val : (Iterable<ValSet<VAL>>) getDomain()) {
			SDDImpl<VAR, VAL> dd = (SDDImpl<VAR, VAL>) getAlpha(val);
			if (val instanceof DD) {
				t = t + dd.flatenizeRepresentation(
						branchAsString + getVariable() + "(" + ((DDImpl) val).flatenizeRepresentation("") + ") ");

				continue;
			}
			t = t + dd.flatenizeRepresentation(branchAsString + getVariable() + "(" + val + ") ");
		}

		return t;
	}

	public DD<VAR, ValSet<VAL>> getTop() {
		return new SDDImpl((VAR) getVariable());
	}

	private static class AppendHom<Var, Val> extends SDDHomImpl<Var, Val> {
		private AppendHom() {
		}

		@SuppressWarnings("unchecked")
		protected DD<Var, ValSet<Val>> phi1(Object... parameters) {
			return (DD<Var, ValSet<Val>>) parameters[0];
		}

		public int computeHashCode() {
			return getClass().hashCode() * 6143;
		}

		public boolean isEqual(Object that) {
			boolean eq = (this == that || that instanceof AppendHom);
			return eq;
		}

		@Override
		protected DD<Var, ValSet<Val>> phi(Var e, ValSet<Val> x, Map<ValSet<Val>, DD<Var, ValSet<Val>>> alpha,
				Object... paramVarArgs) {
			return SDDImpl.create(e, x, phi(alpha.get(x), paramVarArgs[0]));
		}
	}

	@SuppressWarnings("unchecked")
	public static <VAR, VAL> DD<VAR, ValSet<VAL>> getTrue(Class<VAR> varClass, Class<VAL> valClass) {
		return (DD<VAR, ValSet<VAL>>) SDDImpl.SDD_TRUE;
	}

	@SuppressWarnings("unchecked")
	public static <VAR, VAL> DD<VAR, ValSet<VAL>> getFalse(Class<VAR> varClass, Class<VAL> valClass) {
		return (DD<VAR, ValSet<VAL>>) SDDImpl.SDD_FALSE;
	}

	@SuppressWarnings("unchecked")
	public static <VAR, VAL> DD<VAR, ValSet<VAL>> getAny(Class<VAR> varClass, Class<VAL> valClass) {
		return (DD<VAR, ValSet<VAL>>) SDDImpl.SDD_ANY;
	}

	@SuppressWarnings("unchecked")
	public DD<VAR, ValSet<VAL>> getTrue() {
		return (DD<VAR, ValSet<VAL>>) SDDImpl.SDD_TRUE;
	}

	@SuppressWarnings("unchecked")
	public DD<VAR, ValSet<VAL>> getFalse() {
		return (DD<VAR, ValSet<VAL>>) SDDImpl.SDD_FALSE;
	}

	@SuppressWarnings("unchecked")
	public DD<VAR, ValSet<VAL>> getAny() {
		return (DD<VAR, ValSet<VAL>>) SDDImpl.SDD_ANY;
	}

	@Override
	public ValSet<ValSet<VAL>> difference(ValSet<ValSet<VAL>> paramTDataSet) {
		return difference((DD) paramTDataSet, true);
	}

	@Override
	public ValSet<ValSet<VAL>> intersection(ValSet<ValSet<VAL>> paramTDataSet) {
		return intersection((DD) paramTDataSet, true);
	}

	@Override
	public ValSet<ValSet<VAL>> union(ValSet<ValSet<VAL>> paramTDataSet) {
		return union((DD) paramTDataSet, true);
	}

}
