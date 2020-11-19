package ch.unige.cui.smv.dd.ddd;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import ch.unige.cui.smv.dd.DD;
import ch.unige.cui.smv.dd.Hom;
import ch.unige.cui.smv.dd.util.DDGraphGenerator;

public class TutorialPhilo {
	private DD<PLACE, Integer> buildInitStateForPhilo() {
		return DDDImpl.create(PLACE.p1Eat, Integer.valueOf(0),
				DDDImpl.create(PLACE.p1Fork, Integer.valueOf(1), DDDImpl.create(PLACE.p1Think, Integer.valueOf(1),
						DDDImpl.create(PLACE.p2Eat, Integer.valueOf(0),
								DDDImpl.create(PLACE.p2Fork, Integer.valueOf(1), DDDImpl.create(PLACE.p2Think,
										Integer.valueOf(1), DDDImpl.create(PLACE.sharedFork, Integer.valueOf(1))))))));
	}

	@Test
	public void testDiningPhilo() throws IOException {
		try {
			DD<PLACE, Integer> dDD = DDDImpl.getTrue(PLACE.class, Integer.class);
			dDD = buildInitStateForPhilo();
			System.out.println("Initial Marking : ");
			dDD.printInLibDDDStyle(System.out);
			DDDIdHom<PLACE, Integer> dDDIdHom = new DDDIdHom<>();
			HMinus hMinus1 = new HMinus(PLACE.p1Think);
			Hom<PLACE, Integer> dDDHom1 = hMinus1.compose(new HMinus(PLACE.sharedFork));
			dDDHom1 = dDDHom1.compose(new HMinus(PLACE.p1Fork));
			dDDHom1 = dDDHom1.compose(new HPlus(PLACE.p1Eat));
			Hom<PLACE, Integer> dDDHom5 = dDDIdHom.union(dDDHom1);
			HMinus hMinus2 = new HMinus(PLACE.p1Eat);
			Hom<PLACE, Integer> dDDHom2 = hMinus2.compose(new HPlus(PLACE.sharedFork));
			dDDHom2 = dDDHom2.compose(new HPlus(PLACE.p1Fork));
			dDDHom2 = dDDHom2.compose(new HPlus(PLACE.p1Think));
			dDDHom5 = dDDHom5.union(dDDHom2);
			HMinus hMinus3 = new HMinus(PLACE.p2Think);
			Hom<PLACE, Integer> dDDHom3 = hMinus3.compose(new HMinus(PLACE.sharedFork));
			dDDHom3 = dDDHom3.compose(new HMinus(PLACE.p2Fork));
			dDDHom3 = dDDHom3.compose(new HPlus(PLACE.p2Eat));
			dDDHom5 = dDDHom5.union(dDDHom3);
			HMinus hMinus4 = new HMinus(PLACE.p2Eat);
			Hom<PLACE, Integer> dDDHom4 = hMinus4.compose(new HPlus(PLACE.sharedFork));
			dDDHom4 = dDDHom4.compose(new HPlus(PLACE.p2Fork));
			dDDHom4 = dDDHom4.compose(new HPlus(PLACE.p2Think));
			dDDHom5 = dDDHom5.union(dDDHom4);
			Hom<PLACE, Integer> dDDHom6 = dDDHom5.fixpoint();
			DD<PLACE, Integer> object = dDDHom6.phi(dDD, new Object[0]);
			System.out.println("Nb states : " + object.getStates());
			System.out.println("State Space Marking : ");
			object.printInLibDDDStyle(System.out);
			System.out.println((new DDGraphGenerator()).outputDOTFormat((DD) object));
		} catch (Exception exception) {
			System.err.println(exception);
			exception.printStackTrace();
		}
	}

	private class HPlus extends DDDHomImpl<PLACE, Integer> {
		private TutorialPhilo.PLACE mPlace;

		public HPlus(TutorialPhilo.PLACE param1PLACE) {
			this.mPlace = param1PLACE;
		}

		protected DD<TutorialPhilo.PLACE, Integer> phi(TutorialPhilo.PLACE param1PLACE, Integer param1Integer,
				Map<Integer, DD<TutorialPhilo.PLACE, Integer>> param1Map, Object... param1VarArgs) {
			return param1PLACE.equals(this.mPlace)
					? DDDImpl.create(param1PLACE, Integer.valueOf(param1Integer.intValue() + 1),
							(DD<TutorialPhilo.PLACE, Integer>) id(param1Map, param1Integer))
					: DDDImpl.create(param1PLACE, param1Integer,
							(DD<TutorialPhilo.PLACE, Integer>) phi(id(param1Map, param1Integer), new Object[0]));
		}

		protected DD<PLACE, Integer> phi1(Object... param1VarArgs) {
			return getDDFalse();
		}

		public int computeHashCode() {
			return getClass().hashCode() * 3671 + this.mPlace.hashCode() * 3947;
		}

		public boolean isEqual(Object param1Object) {
			boolean bool = (this == param1Object) ? true : false;
			if (!bool && param1Object instanceof HPlus) {
				HPlus hPlus = (HPlus) param1Object;
				bool = (this.mPlace == hPlus.mPlace) ? true : false;
			}
			return bool;
		}
	}

	private class HMinus extends DDDHomImpl<PLACE, Integer> {
		private TutorialPhilo.PLACE mPlace;

		public HMinus(TutorialPhilo.PLACE param1PLACE) {
			this.mPlace = param1PLACE;
		}

		protected DD<TutorialPhilo.PLACE, Integer> phi(TutorialPhilo.PLACE param1PLACE, Integer param1Integer,
				Map<Integer, DD<TutorialPhilo.PLACE, Integer>> param1Map, Object... param1VarArgs) {
			return param1PLACE.equals(this.mPlace)
					? ((param1Integer.intValue() >= 1)
							? DDDImpl.create(param1PLACE, Integer.valueOf(param1Integer.intValue() - 1),
									(DD<TutorialPhilo.PLACE, Integer>) id(param1Map, param1Integer))
							: (DD<TutorialPhilo.PLACE, Integer>) getDDFalse())
					: DDDImpl.create(param1PLACE, param1Integer,
							(DD<TutorialPhilo.PLACE, Integer>) phi(id(param1Map, param1Integer), new Object[0]));
		}

		protected DD<PLACE, Integer> phi1(Object... param1VarArgs) {
			return  getDDFalse();
		}

		public int computeHashCode() {
			return getClass().hashCode() * 7523 + this.mPlace.hashCode() * 5953;
		}

		public boolean isEqual(Object param1Object) {
			boolean bool = (this == param1Object) ? true : false;
			if (!bool && param1Object instanceof HMinus) {
				HMinus hMinus = (HMinus) param1Object;
				bool = (this.mPlace == hMinus.mPlace) ? true : false;
			}
			return bool;
		}
	}

	private enum PLACE {
		p1Think, p2Think, p1Fork, p2Fork, p1Eat, p2Eat, sharedFork;
	}
}
