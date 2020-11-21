package net.hostettler.jdd.dd.sdd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.DDImpl;
import net.hostettler.jdd.dd.Hom;
import net.hostettler.jdd.dd.HomImpl;
import net.hostettler.jdd.dd.ValSet;
import net.hostettler.jdd.dd.ddd.DDDHomImpl;
import net.hostettler.jdd.dd.ddd.DDDIdHom;
import net.hostettler.jdd.dd.ddd.DDDImpl;
import net.hostettler.jdd.dd.sdd.SDDIdHom;
import net.hostettler.jdd.dd.sdd.SDDImpl;
import net.hostettler.jdd.dd.sdd.SDDLocalHom;

public class SDDDiningPhilosophersTest {
	public static final int NB_PHILO = 100;

	private List<Hom<Integer, ValSet<Integer>>> mEvents = getTransitionRelation();

	private DD<STATES, Integer> buildInitState() {
		return DDDImpl.create(STATES.Fork, Integer.valueOf(1),
				DDDImpl.create(STATES.HasL, Integer.valueOf(0),
						DDDImpl.create(STATES.WaitR, Integer.valueOf(0),
								DDDImpl.create(STATES.HasR, Integer.valueOf(0), DDDImpl.create(STATES.WaitL,
										Integer.valueOf(0), DDDImpl.create(STATES.Idle, Integer.valueOf(1)))))));
	}

	Hom<STATES, Integer> saturateLocal(Hom<STATES, Integer> paramHom) {
		Hom<STATES, Integer> dDDHom =  paramHom.union(new DDDIdHom<>());
		return  dDDHom.fixpoint();
	}

	Hom<Integer, ValSet<Integer>> saturateGlobal(Hom<Integer, ValSet<Integer>> paramSDDHom) {
		Hom<Integer, ValSet<Integer>> sDDHom = paramSDDHom.union(new SDDIdHom<Integer, Integer>());
		return sDDHom.fixpoint();
	}

	private List<Hom<Integer, ValSet<Integer>>> getTransitionRelation() {
		ArrayList<Hom<Integer, ValSet<Integer>>> arrayList = new ArrayList<>();
		HMinus hMinus1 = new HMinus(STATES.Idle, 1);
		HPlus hPlus2 = new HPlus(STATES.WaitL, Integer.valueOf(1));
		HPlus hPlus3 = new HPlus(STATES.WaitR, Integer.valueOf(1));
		Hom<STATES, Integer> dDDHom1 = (Hom) hMinus1.compose(hPlus2.compose((Hom) hPlus3));
		hMinus1 = new HMinus(STATES.WaitL, 1);
		HMinus hMinus2 = new HMinus(STATES.Fork, 1);
		hPlus3 = new HPlus(STATES.HasL, Integer.valueOf(1));
		Hom<STATES, Integer> dDDHom2 = (Hom) hMinus2.compose(hMinus1.compose((Hom) hPlus3));
		hMinus1 = new HMinus(STATES.HasL, 1);
		hMinus2 = new HMinus(STATES.HasR, 1);
		hPlus3 = new HPlus(STATES.Idle, Integer.valueOf(1));
		HPlus hPlus4 = new HPlus(STATES.Fork, Integer.valueOf(1));
		Hom<?, ?> dDDHom4 = (Hom) hMinus1.compose(hMinus2.compose(hPlus3.compose((Hom) hPlus4)));
		hMinus1 = new HMinus(STATES.WaitR, 1);
		HPlus hPlus1 = new HPlus(STATES.HasR, Integer.valueOf(1));
		Hom<?, ?> dDDHom3 = (Hom) hMinus1.compose((Hom) hPlus1);
		Hom<STATES, Integer> dDDHom5 = saturateLocal(
				(Hom<STATES, Integer>) saturateLocal(dDDHom2).compose((Hom) saturateLocal(dDDHom1)));
		for (byte b = 0; b < NB_PHILO; b++) {
			Hom<Integer, ValSet<Integer>> sDDHom1 =  new SDDLocalHom(
							new HMinus(STATES.Fork, 1),Integer.valueOf((b + 1) % NB_PHILO)).compose(new SDDLocalHom(
									dDDHom3, Integer.valueOf(b)));
							
			sDDHom1 = saturateGlobal(sDDHom1);
			Hom<Integer, ValSet<Integer>> sDDHom2 =  (new SDDLocalHom(dDDHom4,
					Integer.valueOf(b)))
							.compose(new SDDLocalHom(
									new HPlus(STATES.Fork, Integer.valueOf(1)),
									Integer.valueOf((b + 1) % NB_PHILO)));
			sDDHom2 = saturateGlobal(sDDHom2);
			Hom<Integer, ValSet<Integer>> sDDHom3 = 	((new SDDLocalHom(
					dDDHom5, Integer.valueOf(b))).compose(sDDHom1)).compose(sDDHom2);
			sDDHom3 = saturateGlobal(sDDHom3);
			arrayList.add(sDDHom3);
		}
		return arrayList;
	}

	DD<Integer, ValSet<Integer>> markingsChainingLoop(DD<Integer, ValSet<Integer>> paramSDD) {
		Hom sDDHom = this.mEvents.get(0);
		for (byte b = 1; b < this.mEvents.size(); b++)
			sDDHom =  sDDHom.compose(this.mEvents.get(b));
		return  sDDHom.union(new SDDIdHom<Object, Object>()).fixpoint().phi(paramSDD,
				new Object[0]);
	}

	
	@Before
	public void setup() {
		DDImpl.resetCache();
		HomImpl.resetCache();
	}

	@Test
	public void testDiningPhilo() throws IOException {
		try {
			DD<STATES, Integer> dDD = buildInitState();
			DD<Integer, ValSet<Integer>> sDD = (DD) SDDImpl.SDD_TRUE;
			for (byte b = 0; b < NB_PHILO; b++)
				sDD = SDDImpl.create(Integer.valueOf(b), (ValSet) dDD, sDD);
			long l1 = System.currentTimeMillis();
			DD<Integer, ValSet<Integer>> sDD1 = markingsChainingLoop(sDD);
			System.out.println("Time : " + (System.currentTimeMillis() - l1));
			System.out.println("Nb states (MARKING): " + sDD1.getStates());
			System.out.println("Op in cache : " + HomImpl.getOpInCache() + " Hits : " + HomImpl.getHits()
					+ " Cache hits : " + HomImpl.getCacheHits() + " Percentage: "
					+ ((float) HomImpl.getCacheHits() * 100.0F / (float) HomImpl.getHits()) + "%");
			System.out.println("Nb DDD : " + DDImpl.getNbDD());
			System.gc();
			long l2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			System.out.println("HomImpl Collisions : " + HomImpl.getCollision());
			System.out.println("HomImpl Total : " + HomImpl.getTotalEqual());
			System.out.println("HomImpl % : " + (HomImpl.getCollision() * 100L / HomImpl.getTotalEqual()));
			System.out.println("DDImpl Collisions : " + DDImpl.getCollision());
			System.out.println("DDImpl Total : " + DDImpl.getTotalEqual());
			System.out.println("DDImpl % : " + (DDImpl.getCollision() * 100L / DDImpl.getTotalEqual()));
			System.out.println("Mem (MB) : " + (l2 / 1024L / 1024L));
			StringBuilder stringBuilder = new StringBuilder();
			Formatter formatter = new Formatter(stringBuilder);
			formatter.format("%1.14E", new Object[] { Double.valueOf(sDD1.getStates()) });
			System.out.println(stringBuilder);
			Assert.assertEquals("4.96926405783747E+62", stringBuilder.toString());
		} catch (Exception exception) {
			System.err.println(exception);
			exception.printStackTrace();
		}
	}

	public static void main(String[] paramArrayOfString) throws Exception {
		(new SDDDiningPhilosophersTest()).testDiningPhilo();
	}

	private class HPlus extends DDDHomImpl<STATES, Integer> {
		private SDDDiningPhilosophersTest.STATES mPlace;

		private int mValue;

		public HPlus(SDDDiningPhilosophersTest.STATES param1STATES, Integer param1Integer) {
			super(true);
			this.mPlace = param1STATES;
			this.mValue = param1Integer.intValue();
		}

		protected DD<SDDDiningPhilosophersTest.STATES, Integer> phi(SDDDiningPhilosophersTest.STATES param1STATES,
				Integer param1Integer, Map<Integer, DD<SDDDiningPhilosophersTest.STATES, Integer>> param1Map,
				Object... param1VarArgs) {
			return DDDImpl.create(param1STATES, Integer.valueOf(param1Integer.intValue() + this.mValue),
					 id(param1Map, param1Integer));
		}

		public boolean isLocallyInvariant(DD<SDDDiningPhilosophersTest.STATES, Integer> param1DD) {
			return (param1DD.getVariable() != this.mPlace);
		}

		protected DD<?, ?> phi1(Object... param1VarArgs) {
			return getDDFalse();
		}

		public int computeHashCode() {
			return getClass().hashCode() * 3373 + this.mPlace.hashCode() * 3229 + this.mValue * 3191;
		}

		public boolean isEqual(Object param1Object) {
			boolean bool = (this == param1Object) ? true : false;
			if (!bool && param1Object instanceof HPlus) {
				HPlus hPlus = (HPlus) param1Object;
				bool = (this.mPlace == hPlus.mPlace && this.mValue == hPlus.mValue) ? true : false;
			}
			return bool;
		}

		public String toString() {
			return "h+(" + this.mPlace.toString() + ", " + this.mValue + ")";
		}

	}

	private class HMinus extends DDDHomImpl<STATES, Integer> {
		private SDDDiningPhilosophersTest.STATES mPlace;

		private int mValue;

		public HMinus(SDDDiningPhilosophersTest.STATES param1STATES, int param1Int) {
			super(true);
			this.mPlace = param1STATES;
			this.mValue = param1Int;
		}

		protected DD<SDDDiningPhilosophersTest.STATES, Integer> phi(SDDDiningPhilosophersTest.STATES param1STATES,
				Integer param1Integer, Map<Integer, DD<SDDDiningPhilosophersTest.STATES, Integer>> param1Map,
				Object... param1VarArgs) {
			return (param1Integer.intValue() >= this.mValue)
					? DDDImpl.create(param1STATES, Integer.valueOf(param1Integer.intValue() - this.mValue),
							 id(param1Map, param1Integer))
					: getDDFalse();
		}

		public boolean isLocallyInvariant(DD<SDDDiningPhilosophersTest.STATES, Integer> param1DD) {
			return (param1DD.getVariable() != this.mPlace);
		}

		protected DD<?, ?> phi1(Object... param1VarArgs) {
			return getDDFalse();
		}

		public int computeHashCode() {
			return getClass().hashCode() * 5011 + this.mPlace.hashCode() * 691 + this.mValue * 349;
		}

		public boolean isEqual(Object param1Object) {
			boolean bool = (this == param1Object) ? true : false;
			if (!bool && param1Object instanceof HMinus) {
				HMinus hMinus = (HMinus) param1Object;
				bool = (this.mPlace == hMinus.mPlace && this.mValue == hMinus.mValue) ? true : false;
			}
			return bool;
		}

		public String toString() {
			return "h-(" + this.mPlace.toString() + ", " + this.mValue + ")";
		}
	}

	private enum STATES {
		Fork, HasR, WaitR, HasL, WaitL, Idle;
	}
}

/*
 * Location:
 * C:\Users\steve.hostettler\Downloads\alpina\eclipse_alpina\configuration\org.
 * eclipse.osgi\bundles\7\1\.cp\lib\jdd_1.1.2.jar!\c\\unige\cui\smv\dd\sdd\
 * SDDDiningPhilosophersTest.class Java compiler version: 6 (50.0) JD-Core
 * Version: 1.1.3
 */