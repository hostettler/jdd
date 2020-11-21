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

public final class DDDImpl<Var, Val> extends DDImpl<Var, Val> implements DD<Var, Val> {
	private static Map<DD<? extends Object, ? extends Object>, List<DD<?, ?>>> mSplitCache = new HashMap<>();

	private static final int MIN_CACHE_SPLIT_THRESHOLD = 50;

	private static AppendHom mAppendHom = new AppendHom<Object, Object>();

	private DDDImpl(Var variable) {
		super(variable);
	}

	private DDDImpl(Var variable, double nbStates) {
		super(variable, nbStates);
	}

	private DDDImpl(Var variable, Val value, DD<Var, Val> sdd) {
		super(variable, value, sdd);
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
		DD<Var, Val> newDDD = new DDDImpl<Var, Val>(variable, value, (DD) DDD_TRUE);
		return (DD<Var, Val>) canonicity(newDDD);
	}

	protected DD<Var, Val> append(DD<Var, Val> operand1, DD<Var, Val> operand2) {
		return mAppendHom.phi(operand1, new Object[] { operand2 });
	}

	private static class AppendHom<Variable, Value> extends DDDHomImpl<Variable, Value> {
		private AppendHom() {
		}

		protected DD<Variable, Value> phi(Variable e, Value x, Map<Value, DD<Variable, Value>> alpha,
				Object... parameters) {
			return DDDImpl.create(e, x, phi(alpha.get(x), parameters));
		}

		protected DD<?, ?> phi1(Object... parameters) {
			return (DD<?, ?>) parameters[0];
		}

		public Hom<Variable, Value> compose(Hom<Variable, Value> subHom) {
			return (Hom<Variable, Value>) super.compose((Hom<Variable, Value>) subHom);
		}

		public int computeHashCode() {
			return getClass().hashCode() * 7433;
		}

		public boolean isEqual(Object that) {
			boolean eq = (this == that || that instanceof AppendHom);
			return eq;
		}
	}

	protected DD<Var, Val> setOperation(DDImpl.OP[][] operation, DD<Var, Val> operand1, DD<Var, Val> operand2) {
		DD dD;
		if (operand1 == operand2) {
			switch (operation[3][3]) {

			case RET_DD_ANY:
				return operand1;

			case RET_DD_FALSE:
				return operand1;

			case RET_FIRST_OP:
				return (DD<Var, Val>) DDD_FALSE;
			}
			throw new IllegalArgumentException();
		}

		DD<Var, Val> set = null;

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

			DDDImpl<Var, Val> op1 = (DDDImpl<Var, Val>) operand1;
			DDDImpl<Var, Val> op2 = (DDDImpl<Var, Val>) operand2;

			if (!op1.getVariable().equals(op2.getVariable())) {

				dD = eval(operation[3][3], operand1, operand2);

			} else {

				Set<Val> domain = new HashSet<Val>(op1.getAlpha().keySet());

				domain.addAll(op2.getAlpha().keySet());
				dD = new DDDImpl<>();
				dD.setVariable(op1.getVariable());

				boolean falseSubTree = true;
				for (Val value : domain) {

					DD<Var, Val> target = setOperationWithCache(operation, operand1.getAlpha(value),
							operand2.getAlpha(value));

					if (target != DDD_FALSE) {
						falseSubTree = false;
						dD.addAlpha(value, target);
					}
				}
				if (falseSubTree) {
					dD = DDD_FALSE;
				}
			}
		}

		return canonicity(dD);
	}

	protected DD<Var, Val> copy(DD<Var, Val> tree) {
		DD<Var, Val> tDD = null;

		if (DDD_TRUE == tree || DDD_FALSE == tree || DDD_ANY == tree) {
			tDD = tree;
		} else {
			DDDImpl dDDImpl = new DDDImpl<>();
			dDDImpl.setVariable(tree.getVariable());
			for (Val value : tree.getDomain()) {
				DD<Var, Val> subtree = tree.getAlpha(value);
				dDDImpl.addAlpha(value, subtree);
			}
			tDD = dDDImpl;
		}

		return tDD;
	}

	public DD<Var, Val> getDDAny() {
		return (DD<Var, Val>) DDD_ANY;
	}

	public DD<Var, Val> getDDTrue() {
		return (DD<Var, Val>) DDD_TRUE;
	}

	public DD<Var, Val> getDDFalse() {
		return (DD<Var, Val>) DDD_FALSE;
	}

	public static final DD<?, ?> DDD_ANY = new DDDImpl(Terminator.ANY);
	public static final DD<?, ?> DDD_FALSE = new DDDImpl(Terminator.FALSE);
	public static final DD<?, ?> DDD_TRUE = new DDDImpl(Terminator.TRUE, 1.0D);

	
	public static <TVar, TVal> DD<TVar, TVal> getTrue(Class<TVar> varClass, Class<TVal> valClass) {
		return (DD) DDD_TRUE;
	}

	public static <TVar, TVal> DD<TVar, TVal> getFalse(Class<TVar> varClass, Class<TVal> valClass) {
		return (DD) DDD_FALSE;
	}

	public static <TVar, TVal> DD<TVar, TVal> getAny(Class<TVar> varClass, Class<TVal> valClass) {
		return (DD) DDD_ANY;
	}

	public DD<Var, Val> getTop() {
		return new DDDImpl((Var) getVariable());
	}

	public List<?> split() {
		if (getStates() < 2.0D) {
			List<DDDImpl<Var, Val>> l = new ArrayList<>();
			l.add(this);
			return l;
		}
		if (getStates() < 50.0D) {
			return split(this);
		}
		List split = mSplitCache.get(this);
		if (split == null) {
			split = split(this);
			mSplitCache.put(this, split);
		}
		return split;
	}

	private List<DD<Var, Val>> split(DD<Var, Val> ddd2split) {
		List<DD<Var, Val>> valList = new ArrayList<DD<Var, Val>>();
		Var variable = (Var) ddd2split.getVariable();

		if (ddd2split == DDD_TRUE || ddd2split == DDD_FALSE || ddd2split == DDD_ANY) {

			valList.add(ddd2split);
		} else {

			for (Map.Entry<Val, DD<Var, Val>> entry : (Iterable<Map.Entry<Val, DD<Var, Val>>>) ddd2split.getAlpha()
					.entrySet()) {
				Val a = entry.getKey();
				DD<Var, Val> d = entry.getValue();

				List<DD<Var, Val>> sons = split((DD<Var, Val>) d);
				for (DD<Var, Val> son : sons) {
					valList.add(create(variable, a, son));
				}
			}
		}
		return valList;
	}

	public ValSet<Val> intersection(ValSet<Val> operand) {
		return intersection((DD) operand, true);
	}

	public ValSet<Val> union(ValSet<Val> operand) {
		return union((DD) operand, true);
	}

	public ValSet<Val> difference(ValSet<Val> operand) {
		return difference((DD) operand, true);
	}

}
