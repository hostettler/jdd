package net.hostettler.jdd.dd.sdd;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.DDImpl;
import net.hostettler.jdd.dd.Hom;
import net.hostettler.jdd.dd.ObjSet;
import net.hostettler.jdd.dd.Terminator;
import net.hostettler.jdd.dd.ValSet;
import net.hostettler.jdd.dd.ddd.DDDImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SDDImpl<Var, Val> extends DDImpl<Var, ValSet<Val>> implements DD<Var, ValSet<Val>> {

	private static AppendHom mAppendHom = new AppendHom<Object, Object>();

	private static Map<DD<?, ?>, List<DD<?, ?>>> mSplitCache = new HashMap<>();

	private static final int MIN_CACHE_SPLIT_THRESHOLD = 50;
	public static final DD<?, ?> SDD_ANY = new SDDImpl(Terminator.ANY);
	public static final DD<?, ?> SDD_FALSE = new SDDImpl(Terminator.FALSE);
	public static final DD<?, ?> SDD_TRUE = new SDDImpl(Terminator.TRUE, 1.0D);

	private SDDImpl(Var variable) {
		super(variable);
	}

	private SDDImpl(Var variable, double nbStates) {
		super(variable, nbStates);
	}

	private SDDImpl(Var variable, double nbStates, boolean ignoreDD) {
		super(variable, nbStates, ignoreDD);
	}

	private SDDImpl(Var variable, Map<ValSet<Val>, DD<Var, ValSet<Val>>> alpha) {
		super(variable, alpha);
	}

	private SDDImpl(Var variable, ValSet<Val> value, DD<Var, ValSet<Val>> sdd) {
		super(variable, value, sdd);
	}

	private SDDImpl(Var variable, ValSet<Val> value) {
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

	protected DD<Var, ValSet<Val>> append(DD<Var, ValSet<Val>> operand1, DD<Var, ValSet<Val>> operand2) {
		DD<Var, ValSet<Val>> dD = mAppendHom.phi(operand1, new Object[] { operand2 });
		dD.setIgnoreThisDD(operand1.ignoreDD() & operand2.ignoreDD());
		return dD;
	}

	protected double getValueStates(ValSet<Val> value) {
		if (value instanceof DD) {
			return ((DD) value).getStates();
		}
		return value.getSize();
	}

	protected DD<Var, ValSet<Val>> setOperation(DDImpl.OP[][] operation, DD<Var, ValSet<Val>> operand1,
			DD<Var, ValSet<Val>> operand2) {
		if (operand1 == operand2) {
			switch (operation[3][3]) {

			case RET_DD_ANY:
				return operand1;

			case RET_DD_FALSE:
				return operand1;

			case RET_FIRST_OP:
				return getDDFalse();
			}
			throw new IllegalArgumentException();
		}

		DD<Var, ValSet<Val>> set = null;

		if (getDDFalse() == operand1) {
			if (getDDFalse() == operand2) {

				set = eval(operation[0][0], operand1, operand2);
			} else if (getDDTrue() == operand2) {

				set = eval(operation[0][1], operand1, operand2);
			} else if (getDDAny() == operand2) {

				set = eval(operation[0][2], operand1, operand2);
			} else {

				set = eval(operation[0][3], operand1, operand2);
			}
		} else if (getDDTrue() == operand1) {
			if (getDDFalse() == operand2) {
				set = eval(operation[1][0], operand1, operand2);
			} else if (getDDTrue() == operand2) {
				set = eval(operation[1][1], operand1, operand2);
			} else if (getDDAny() == operand2) {
				set = eval(operation[1][2], operand1, operand2);
			} else {
				set = eval(operation[1][3], operand1, operand2);
			}
		} else if (getDDAny() == operand1) {
			if (getDDFalse() == operand2) {
				set = eval(operation[2][0], operand1, operand2);
			} else if (getDDTrue() == operand2) {
				set = eval(operation[2][1], operand1, operand2);
			} else if (getDDAny() == operand2) {
				set = eval(operation[2][2], operand1, operand2);
			} else {
				set = eval(operation[2][3], operand1, operand2);
			}

		} else if (getDDFalse() == operand2) {
			set = eval(operation[3][0], operand1, operand2);
		} else if (getDDTrue() == operand2) {
			set = eval(operation[3][1], operand1, operand2);
		} else if (getDDAny() == operand2) {
			set = eval(operation[3][2], operand1, operand2);

		} else if (!operand1.getVariable().equals(operand2.getVariable())) {

			set = eval(operation[3][3], operand1, operand2);

		} else {

			set = getDDFalse();
			Var variable = (Var) operand2.getVariable();

			Map<ValSet<Val>, DD<Var, ValSet<Val>>> alpha1 = operand1.getAlpha();
			Map<ValSet<Val>, DD<Var, ValSet<Val>>> alpha2 = operand2.getAlpha();
			Map<ValSet<Val>, DD<Var, ValSet<Val>>> alpha = new HashMap<ValSet<Val>, DD<Var, ValSet<Val>>>();

			Map<DD<Var, Val>, ValSet<Val>> result = new HashMap<DD<Var, Val>, ValSet<Val>>();

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

			for (Map.Entry<DD<Var, Val>, ValSet<Val>> d : result.entrySet()) {

				if (d.getKey() != SDD_FALSE) {
					alpha.put((ValSet) d.getValue(), (DD) d.getKey());
				}
			}
			if (alpha.size() > 0) {

				set = canonicity(new SDDImpl(variable, alpha));

			} else {
				set = getDDFalse();
			}
			set.setIgnoreThisDD(operand1.ignoreDD() & operand2.ignoreDD());
		}

		return set;
	}

	private void union(Map<DD<Var, Val>, ValSet<Val>> result, Map<ValSet<Val>, DD<Var, ValSet<Val>>> alpha1,
			Map<ValSet<Val>, DD<Var, ValSet<Val>>> alpha2) {
		for (Map.Entry<ValSet<Val>, DD<Var, ValSet<Val>>> a : alpha1.entrySet()) {
			result.put((DD<Var, Val>) a.getValue(), a.getKey());
		}

		Map<DD<Var, Val>, ValSet<Val>> sums = new HashMap<DD<Var, Val>, ValSet<Val>>();
		Map<DD<Var, Val>, ValSet<Val>> rems = new HashMap<DD<Var, Val>, ValSet<Val>>();

		List<DD<Var, Val>> toRemove = new ArrayList<DD<Var, Val>>();

		for (Map.Entry<ValSet<Val>, DD<Var, ValSet<Val>>> aa : alpha2.entrySet()) {
			ValSet<Val> a = aa.getKey();
			DD<Var, Val> da = (DD<Var, Val>) aa.getValue();
			ValSet<Val> remainOfA = aa.getKey();

			for (Map.Entry<DD<Var, Val>, ValSet<Val>> ddb : result.entrySet()) {

				ValSet<Val> b = ddb.getValue();
				DD<Var, Val> db = ddb.getKey();
				ValSet<Val> remainOfB = b;

				ValSet<Val> aInterB = a.intersection(b);
				if (!aInterB.isEmpty()) {

					DD<Var, Val> daUdb = (DD<Var, Val>) da.union(db);
					ValSet<Val> oldInter = sums.get(daUdb);
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

			for (DD<Var, Val> d : toRemove) {
				result.remove(d);
			}
		}

		for (Map.Entry<DD<Var, Val>, ValSet<Val>> dd : rems.entrySet()) {
			squareUnion(result, dd.getKey(), dd.getValue());
		}

		for (Map.Entry<DD<Var, Val>, ValSet<Val>> dd : sums.entrySet()) {
			squareUnion(result, dd.getKey(), dd.getValue());
		}
	}

	private void inter(Map<DD<Var, Val>, ValSet<Val>> result, Map<ValSet<Val>, DD<Var, ValSet<Val>>> alpha1,
			Map<ValSet<Val>, DD<Var, ValSet<Val>>> alpha2) {
		for (Map.Entry<ValSet<Val>, DD<Var, ValSet<Val>>> aa : alpha1.entrySet()) {
			DD<Var, Val> da = (DD<Var, Val>) aa.getValue();
			ValSet<Val> a = aa.getKey();

			for (Map.Entry<ValSet<Val>, DD<Var, ValSet<Val>>> bb : alpha2.entrySet()) {
				ValSet<Val> b = bb.getKey();
				ValSet<Val> aInterB = a.intersection(b);
				if (!aInterB.isEmpty()) {
					DD<Var, Val> db = (DD<Var, Val>) bb.getValue();
					DD<Var, Val> daInterDb = (DD<Var, Val>) da.intersection(db);
					squareUnion(result, daInterDb, aInterB);
				}
			}
		}
	}

	private void diff(Map<DD<Var, Val>, ValSet<Val>> result, Map<ValSet<Val>, DD<Var, ValSet<Val>>> alpha1,
			Map<ValSet<Val>, DD<Var, ValSet<Val>>> alpha2) {
		Map<DD<Var, Val>, ValSet<Val>> rems = new HashMap<DD<Var, Val>, ValSet<Val>>();

		for (Map.Entry<ValSet<Val>, DD<Var, ValSet<Val>>> aa : alpha1.entrySet()) {
			rems.put((DD<Var, Val>) aa.getValue(), aa.getKey());
		}

		for (Map.Entry<ValSet<Val>, DD<Var, ValSet<Val>>> aa : alpha1.entrySet()) {
			DD<Var, Val> da = (DD<Var, Val>) aa.getValue();
			ValSet<Val> a = aa.getKey();

			for (Map.Entry<ValSet<Val>, DD<Var, ValSet<Val>>> bb : alpha2.entrySet()) {
				ValSet<Val> b = bb.getKey();
				ValSet<Val> aInterB = a.intersection(b);
				if (!aInterB.isEmpty()) {

					DD<Var, Val> db = (DD<Var, Val>) bb.getValue();
					DD<Var, Val> daMinusDb = (DD<Var, Val>) da.difference(db);
					squareUnion(result, daMinusDb, aInterB);

					ValSet<Val> restA = rems.get(da);
					restA = restA.difference(aInterB);
					rems.put(da, restA);
				}
			}
		}

		for (Map.Entry<DD<Var, Val>, ValSet<Val>> d : rems.entrySet()) {
			ValSet<Val> values = d.getValue();
			if (!values.isEmpty()) {
				squareUnion(result, d.getKey(), values);
			}
		}
	}

	protected DD<Var, ValSet<Val>> eval(DDImpl.OP operation, DD<Var, ValSet<Val>> operand1,
			DD<Var, ValSet<Val>> operand2) {
		return super.eval(operation, operand1, operand2);
	}

	protected DD<Var, ValSet<Val>> copy(DD<Var, ValSet<Val>> tree) {
		DD<Var, ValSet<Val>> tDD;

		if (getDDTrue() == tree || getDDFalse() == tree || getDDAny() == tree) {

			tDD = tree;
		} else {

			SDDImpl sDDImpl = new SDDImpl();
			sDDImpl.setVariable(tree.getVariable());
			for (ValSet<Val> value : (Iterable<ValSet<Val>>) tree.getDomain()) {
				DD<Var, ValSet<Val>> subtree = tree.getAlpha(value);
				sDDImpl.addAlpha(value, subtree);
			}
			tDD = sDDImpl;
		}
		return tDD;
	}

	private void squareUnion(Map<DD<Var, Val>, ValSet<Val>> reverseAlpha, DD<Var, Val> sdd, ValSet<Val> values) {
		ValSet<Val> a = reverseAlpha.get(sdd);
		if (a != null) {
			values = values.union(a);
		}
		reverseAlpha.put(sdd, values);
	}

	public List<DD<Var, Val>> split() {
		if (getStates() < 2.0D) {
			List<SDDImpl<Var, Val>> l = new ArrayList<>();
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

	private List<ValSet<Val>> split(ValSet<Val> valset2split) {
		List<ValSet<Val>> valList = new ArrayList<ValSet<Val>>();

		if (valset2split instanceof SDDImpl) {
			DD<Var, Val> sdd2split = (SDDImpl) valset2split;
			Var variable = (Var) sdd2split.getVariable();

			if (sdd2split == SDD_TRUE || sdd2split == SDD_FALSE || sdd2split == SDD_ANY) {

				valList.add((ValSet) sdd2split);
			} else {

				for (Map.Entry entry : sdd2split.getAlpha().entrySet()) {
					ValSet<Val> a = (ValSet) entry.getKey();
					DD<Var, ValSet<Val>> d = (DD) entry.getValue();

					List<ValSet<Val>> sons = split((ValSet) d);
					for (ValSet<Val> son : sons) {
						List<ValSet<Val>> vals = split(a);
						for (ValSet<Val> val : vals) {
							valList.add((ValSet) create(variable, val, (SDDImpl) son));
						}
					}
				}
			}
		} else {

			for (Val val : valset2split) {
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
		if (this == getDDTrue())
			return "[ " + branchAsString + "]" + lineSeparator;
		if (this == getDDAny())
			return "[ " + branchAsString + " T ]" + lineSeparator;
		if (this == getDDFalse()) {
			return "[ " + branchAsString + " 0 ]" + lineSeparator;
		}
		String t = "";
		for (ValSet<Val> val : (Iterable<ValSet<Val>>) getDomain()) {
			SDDImpl<Var, Val> dd = (SDDImpl<Var, Val>) getAlpha(val);
			if (val instanceof DD) {
				t = t + dd.flatenizeRepresentation(
						branchAsString + getVariable() + "(" + ((DDImpl) val).flatenizeRepresentation("") + ") ");

				continue;
			}
			t = t + dd.flatenizeRepresentation(branchAsString + getVariable() + "(" + val + ") ");
		}

		return t;
	}

	public DD<Var, ValSet<Val>> getTop() {
		return new SDDImpl((Var) getVariable());
	}

	private static class AppendHom<Var, Val> extends SDDHomImpl<Var, Val> {
		private AppendHom() {
		}

		protected DD<Var, ValSet<Val>> phi1(Object... parameters) {
			return (DD) parameters[0];
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
			return (DD) SDDImpl.create(e, x, (DD) phi(alpha.get(x), (DD) paramVarArgs[0]));
		}
	}

	@Override
	public DD<Var, ValSet<Val>> getDDTrue() {
		return (DD) SDDImpl.SDD_TRUE;
	}

	@Override
	public DD<Var, ValSet<Val>> getDDFalse() {
		// TODO Auto-generated method stub
		return (DD) SDDImpl.SDD_FALSE;
	}

	@Override
	public DD<Var, ValSet<Val>> getDDAny() {
		// TODO Auto-generated method stub
		return (DD) SDDImpl.SDD_ANY;
	}

	@Override
	public ValSet<ValSet<Val>> difference(ValSet<ValSet<Val>> paramTDataSet) {
		return difference((DD) paramTDataSet, true);
	}

	@Override
	public ValSet<ValSet<Val>> intersection(ValSet<ValSet<Val>> paramTDataSet) {
		return intersection((DD) paramTDataSet, true);
	}

	@Override
	public ValSet<ValSet<Val>> union(ValSet<ValSet<Val>> paramTDataSet) {
		return union((DD) paramTDataSet, true);
	}

}
