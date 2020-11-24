package net.hostettler.jdd.dd.ddd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.DDImpl;
import net.hostettler.jdd.dd.Hom;
import net.hostettler.jdd.dd.Terminator;
import net.hostettler.jdd.dd.ValSet;

public final class DDDImpl<VAR, VAL> extends DDImpl<VAR, VAL> implements DD<VAR, VAL> {
	private static Map<DD<? extends Object, ? extends Object>, List<DD<?, ?>>> mSplitCache = new HashMap<>();

	private static final int MIN_CACHE_SPLIT_THRESHOLD = 50;

	private static AppendHom<?, ?> mAppendHom = new AppendHom<>();

	private DDDImpl(VAR variable) {
		super(variable);
	}

	private DDDImpl(VAR variable, double nbStates) {
		super(variable, nbStates);
	}

	private DDDImpl(VAR variable, VAL value, DD<VAR, VAL> ddd) {
		super(variable, value, ddd);
	}
	
	@SuppressWarnings("unchecked")
	private DDDImpl(VAR variable, VAL value) {
		super(variable, value, (DD<VAR, VAL>) DDD_TRUE);
	}

	private DDDImpl() {
	}

	public static <Var, Val> DD<Var, Val> create(Var variable, Val value, DD<Var, Val> ddd) {
		if (ddd == DDD_FALSE) {
			return ddd;
		}
		DD<Var, Val> newDDD = new DDDImpl<Var, Val>(variable, value, ddd);
		return (DD<Var, Val>) canonicity(newDDD);
	}

	public static <Var, Val> DD<Var, Val> create(Var variable, Val value) {
		DD<Var, Val> newDDD = new DDDImpl<Var, Val>(variable, value);
		return (DD<Var, Val>) canonicity(newDDD);
	}

	@SuppressWarnings("unchecked")
	protected DD<VAR, VAL> append(DD<VAR, VAL> operand1, DD<VAR, VAL> operand2) {
		return mAppendHom.phi((DD)operand1, operand2);
	}

	private static class AppendHom<Variable, Value> extends DDDHomImpl<Variable, Value> {
		private AppendHom() {
		}

		protected DD<Variable, Value> phi(Variable e, Value x, Map<Value, DD<Variable, Value>> alpha,
				Object... parameters) {
			return DDDImpl.create(e, x, phi(alpha.get(x), parameters));
		}

		protected DD<Variable, Value> phi1(Object... parameters) {
			return (DD<Variable, Value>) parameters[0];
		}

		public int computeHashCode() {
			return getClass().hashCode() * 7433;
		}

		public boolean isEqual(Object that) {
			boolean eq = (this == that || that instanceof AppendHom);
			return eq;
		}
	}

	protected DD<VAR, VAL> setOperation(DDImpl.OP[][] operation, DD<VAR, VAL> operand1, DD<VAR, VAL> operand2) {
		DD<VAR, VAL> dD;
		if (operand1 == operand2) {
			switch (operation[3][3]) {

			case RET_DD_ANY:
				return operand1;

			case RET_DD_FALSE:
				return operand1;

			case RET_FIRST_OP:
				return getFalse();
			default:
				break;
			}
			throw new IllegalArgumentException();
		}

		if (DDD_FALSE == operand1) {
			if (DDD_FALSE == operand2) {

				dD = eval(operation[0][0], operand1, operand2);
			} else if (DDD_TRUE == operand2) {

				dD = eval(operation[0][1], operand1, operand2);
			} else if (DDD_ANY == operand2) {

				dD = eval(operation[0][2], operand1, operand2);
			} else {

				dD = eval(operation[0][3], operand1, operand2);
			}
		} else if (DDD_TRUE == operand1) {
			if (DDD_FALSE == operand2) {
				dD = eval(operation[1][0], operand1, operand2);
			} else if (DDD_TRUE == operand2) {
				dD = eval(operation[1][1], operand1, operand2);
			} else if (DDD_ANY == operand2) {
				dD = eval(operation[1][2], operand1, operand2);
			} else {
				dD = eval(operation[1][3], operand1, operand2);
			}
		} else if (DDD_ANY == operand1) {
			if (DDD_FALSE == operand2) {
				dD = eval(operation[2][0], operand1, operand2);
			} else if (DDD_TRUE == operand2) {
				dD = eval(operation[2][1], operand1, operand2);
			} else if (DDD_ANY == operand2) {
				dD = eval(operation[2][2], operand1, operand2);
			} else {
				dD = eval(operation[2][3], operand1, operand2);
			}

		} else if (DDD_FALSE == operand2) {
			dD = eval(operation[3][0], operand1, operand2);
		} else if (DDD_TRUE == operand2) {
			dD = eval(operation[3][1], operand1, operand2);
		} else if (DDD_ANY == operand2) {
			dD = eval(operation[3][2], operand1, operand2);
		} else {

			DDDImpl<VAR, VAL> op1 = (DDDImpl<VAR, VAL>) operand1;
			DDDImpl<VAR, VAL> op2 = (DDDImpl<VAR, VAL>) operand2;

			if (!op1.getVariable().equals(op2.getVariable())) {

				dD = eval(operation[3][3], operand1, operand2);

			} else {

				Set<VAL> domain = new HashSet<VAL>(op1.getAlpha().keySet());

				domain.addAll(op2.getAlpha().keySet());
				dD = new DDDImpl<>();
				dD.setVariable(op1.getVariable());

				boolean falseSubTree = true;
				for (VAL value : domain) {

					DD<VAR, VAL> target = setOperationWithCache(operation, operand1.getAlpha(value),
							operand2.getAlpha(value));

					if (target != getFalse()) {
						falseSubTree = false;
						dD.addAlpha(value, target);
					}
				}
				if (falseSubTree) {
					dD = getFalse();
				}
			}
		}

		return canonicity(dD);
	}

	protected DD<VAR, VAL> copy(DD<VAR, VAL> tree) {
		DD<VAR, VAL> tDD = null;

		if (DDD_TRUE == tree || DDD_FALSE == tree || DDD_ANY == tree) {
			tDD = tree;
		} else {
			DDDImpl<VAR, VAL> dDDImpl = new DDDImpl<>();
			dDDImpl.setVariable(tree.getVariable());
			for (VAL value : tree.getDomain()) {
				DD<VAR, VAL> subtree = tree.getAlpha(value);
				dDDImpl.addAlpha(value, subtree);
			}
			tDD = dDDImpl;
		}

		return tDD;
	}

	@SuppressWarnings("unchecked")
	public DD<VAR, VAL> getAny() {
		return (DD<VAR, VAL>) DDD_ANY;
	}

	@SuppressWarnings("unchecked")
	public DD<VAR, VAL> getTrue() {
		return (DD<VAR, VAL>) DDD_TRUE;
	}

	@SuppressWarnings("unchecked")
	public DD<VAR, VAL> getFalse() {
		return ((DD<VAR, VAL>) DDD_FALSE);
	}

	public static final DD<?, ?> DDD_ANY = new DDDImpl<Object, Object>(Terminator.ANY);
	public static final DD<?, ?> DDD_FALSE = new DDDImpl<Object, Object>(Terminator.FALSE);
	public static final DD<?, ?> DDD_TRUE = new DDDImpl<Object, Object>(Terminator.TRUE, 1.0D);

	
	@SuppressWarnings("unchecked")
	public static <TVar, TVal> DD<TVar, TVal> getTrue(Class<TVar> varClass, Class<TVal> valClass) {
		return (DD<TVar, TVal>) DDD_TRUE;
	}

	@SuppressWarnings("unchecked")
	public static <TVar, TVal> DD<TVar, TVal> getFalse(Class<TVar> varClass, Class<TVal> valClass) {
		return (DD<TVar, TVal>) DDD_FALSE;
	}

	@SuppressWarnings("unchecked")
	public static <TVar, TVal> DD<TVar, TVal> getAny(Class<TVar> varClass, Class<TVal> valClass) {
		return (DD<TVar, TVal>) DDD_ANY;
	}

	public DD<VAR, VAL> getTop() {
		return new DDDImpl<>(getVariable());
	}

	public List<?> split() {
		if (getStates() < 2.0D) {
			List<DDDImpl<VAR, VAL>> l = new ArrayList<>();
			l.add(this);
			return l;
		}
		if (getStates() < MIN_CACHE_SPLIT_THRESHOLD) {
			return split(this);
		}
		List<DD<?, ?>> split = mSplitCache.get(this);
		if (split == null) {
			split = (List) split(this);
			mSplitCache.put(this, split);
		}
		return split;
	}

	private List<DD<VAR, VAL>> split(DD<VAR, VAL> ddd2split) {
		List<DD<VAR, VAL>> valList = new ArrayList<DD<VAR, VAL>>();
		VAR variable = (VAR) ddd2split.getVariable();

		if (ddd2split == DDD_TRUE || ddd2split == DDD_FALSE || ddd2split == DDD_ANY) {

			valList.add(ddd2split);
		} else {

			for (Map.Entry<VAL, DD<VAR, VAL>> entry : (Iterable<Map.Entry<VAL, DD<VAR, VAL>>>) ddd2split.getAlpha()
					.entrySet()) {
				VAL a = entry.getKey();
				DD<VAR, VAL> d = entry.getValue();

				List<DD<VAR, VAL>> sons = split((DD<VAR, VAL>) d);
				for (DD<VAR, VAL> son : sons) {
					valList.add(create(variable, a, son));
				}
			}
		}
		return valList;
	}

	public ValSet<VAL> intersection(ValSet<VAL> operand) {
		return intersection((DD) operand, true);
	}

	public ValSet<VAL> union(ValSet<VAL> operand) {
		return union((DD) operand, true);
	}

	public ValSet<VAL> difference(ValSet<VAL> operand) {
		return difference((DD) operand, true);
	}

}
