package net.hostettler.jdd.dd.ddd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.DDImpl;
import net.hostettler.jdd.dd.Hom;
import net.hostettler.jdd.dd.util.DDGraphGenerator;

public class DDDDiningPhilosophersTest {
	public static final int NB_PHILO = 30;

	private String buildStateId(STATES paramSTATES, Integer paramInteger) {
		return paramSTATES.toString() + paramInteger.toString();
	}

	private DD<String, Integer> buildInitStateForPhilo(int paramInt) {
		return DDDImpl
				.create(buildStateId(STATES.Fork, Integer.valueOf(paramInt)), Integer.valueOf(1),
						DDDImpl.create(
								buildStateId(STATES.HasR, Integer
										.valueOf(paramInt)),
								Integer.valueOf(0), DDDImpl
										.create(buildStateId(STATES.WaitR, Integer.valueOf(paramInt)),
												Integer.valueOf(0), DDDImpl
														.create(buildStateId(STATES.HasL, Integer.valueOf(paramInt)),
																Integer.valueOf(0), DDDImpl
																		.create(buildStateId(STATES.WaitL,
																				Integer.valueOf(paramInt)),
																				Integer.valueOf(0),
																				DDDImpl.create(
																						buildStateId(STATES.Idle,
																								Integer.valueOf(
																										paramInt)),
																						Integer.valueOf(1)))))));
	}

	@Test
	public void testDiningPhilo() throws IOException {
		DD<String, Integer> dDD1 = DDDImpl.getTrue(String.class, Integer.class);
		for (byte b1 = 0; b1 < NB_PHILO; b1++)
			dDD1 =  dDD1.append(buildInitStateForPhilo(b1));
		dDD1.printInLibDDDStyle(System.out);
		System.out.println((new DDGraphGenerator()).outputDOTFormat(dDD1));
		ArrayList<Hom<String, Integer>> arrayList = new ArrayList<>();
		for (byte b2 = 0; b2 < NB_PHILO; b2++) {
			HMinus hMinus1 = new HMinus(buildStateId(STATES.Idle, Integer.valueOf(b2)), 1);
			HPlus hPlus1 = new HPlus(buildStateId(STATES.WaitL, Integer.valueOf(b2)), Integer.valueOf(1));
			HPlus hPlus2 = new HPlus(buildStateId(STATES.WaitR, Integer.valueOf(b2)), Integer.valueOf(1));
			Hom<String, Integer> dDDHom1 = hMinus1.compose(hPlus1.compose(hPlus2));
			arrayList.add(dDDHom1);
			hMinus1 = new HMinus(buildStateId(STATES.WaitL, Integer.valueOf(b2)), 1);
			HMinus hMinus2 = new HMinus(buildStateId(STATES.Fork, Integer.valueOf(b2)), 1);
			hPlus2 = new HPlus(buildStateId(STATES.HasL, Integer.valueOf(b2)), Integer.valueOf(1));
			Hom<String, Integer> dDDHom2 =  hMinus1.compose(hMinus2.compose(hPlus2));
			arrayList.add(dDDHom2);
			hMinus1 = new HMinus(buildStateId(STATES.WaitR, Integer.valueOf(b2)), 1);
			hMinus2 = new HMinus(buildStateId(STATES.Fork, Integer.valueOf((b2 + 1) % NB_PHILO)), 1);
			hPlus2 = new HPlus(buildStateId(STATES.HasR, Integer.valueOf(b2)), Integer.valueOf(1));
			Hom<String, Integer> dDDHom3 =  hMinus1.compose(hMinus2.compose(hPlus2));
			arrayList.add(dDDHom3);
			hMinus1 = new HMinus(buildStateId(STATES.HasL, Integer.valueOf(b2)), 1);
			hMinus2 = new HMinus(buildStateId(STATES.HasR, Integer.valueOf(b2)), 1);
			hPlus2 = new HPlus(buildStateId(STATES.Idle, Integer.valueOf(b2)), Integer.valueOf(1));
			HPlus hPlus3 = new HPlus(buildStateId(STATES.Fork, Integer.valueOf(b2)), Integer.valueOf(1));
			HPlus hPlus4 = new HPlus(buildStateId(STATES.Fork, Integer.valueOf((b2 + 1) % NB_PHILO)), Integer.valueOf(1));
			Hom<String, Integer> dDDHom4 =  hMinus1.compose(hMinus2.compose(hPlus2.compose(hPlus3.compose(hPlus4))));
			arrayList.add(dDDHom4);
		}
		long l = System.currentTimeMillis();
		DD<String, Integer> dDD2 = dDD1;
		while (true) {
			DD<String, Integer> dDD = dDD2;
			for (Hom<String, Integer> dDDHom : arrayList) {
				DD<String, Integer> object = dDDHom.phi(dDD2);
				if (object != DDDImpl.DDD_FALSE)
					dDD2 = dDD2.union(object);
			}
			if (dDD2 == dDD) {
				System.out.println("Time : " + (System.currentTimeMillis() - l));
				System.out.println("Nb states : " + dDD2.getStates());
				System.out.println("Op in cache : " + DDDHomImpl.getOpInCache() + " Hits : " + DDDHomImpl.getHits()
						+ " Cache hits : " + DDDHomImpl.getCacheHits() + " Percentage: "
						+ ((float) DDDHomImpl.getCacheHits() * 100.0F / (float) DDDHomImpl.getHits()) + "%");
				System.out.println("Nb DDD : " + DDImpl.getNbDD());
				System.out.println("Nb hom : " + arrayList.size());
				System.gc();
				long l1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				System.out.println("Mem (MB) : " + (l1 / 1024L));
				Assert.assertEquals(6.440026026380245E18D, dDD2.getStates(), 0.001D);
				return;
			}
		}
	}

	private class HPlus extends DDDHomImpl<String, Integer> {
		private String mPlace;

		private Integer mValue;

		public HPlus(String param1String, Integer param1Integer) {
			this.mPlace = param1String;
			this.mValue = param1Integer;
		}

		protected DD<String, Integer> phi(String param1String, Integer param1Integer,
				Map<Integer, DD<String, Integer>> param1Map, Object... param1VarArgs) {
			return param1String.equals(this.mPlace)
					? DDDImpl.create(param1String, Integer.valueOf(param1Integer.intValue() + this.mValue.intValue()),
							(DD<String, Integer>) id(param1Map, param1Integer))
					: DDDImpl.create(param1String, param1Integer,
							(DD<String, Integer>) phi(id(param1Map, param1Integer), new Object[0]));
		}

		protected DD<String, Integer> phi1(Object... param1VarArgs) {
			return   getFalse();
		}

		public int computeHashCode() {
			return getClass().hashCode() * 3671 + this.mPlace.hashCode() * 3947 + this.mValue.intValue() * 3533;
		}

		public boolean isEqual(Object param1Object) {
			boolean bool = (this == param1Object) ? true : false;
			if (!bool && param1Object instanceof HPlus) {
				HPlus hPlus = (HPlus) param1Object;
				bool = (this.mPlace.equals(hPlus.mPlace) && this.mValue.equals(hPlus.mValue)) ? true : false;
			}
			return bool;
		}
	}

	private class HMinus extends DDDHomImpl<String, Integer> {
		private String mPlace;

		private int mValue;

		public HMinus(String param1String, int param1Int) {
			this.mPlace = param1String;
			this.mValue = param1Int;
		}

		protected DD<String, Integer> phi(String param1String, Integer param1Integer,
				Map<Integer, DD<String, Integer>> param1Map, Object... param1VarArgs) {
			return param1String.equals(this.mPlace)
					? ((param1Integer.intValue() >= this.mValue)
							? DDDImpl.create(param1String, Integer.valueOf(param1Integer.intValue() - this.mValue),
									(DD<String, Integer>) id(param1Map, param1Integer))
							: (DD<String, Integer>) getFalse())
					: DDDImpl.create(param1String, param1Integer,
							(DD<String, Integer>) phi(id(param1Map, param1Integer), new Object[0]));
		}

		protected DD<String, Integer> phi1(Object... param1VarArgs) {
			return  getFalse();
		}

		public int computeHashCode() {
			return getClass().hashCode() * 7523 + this.mPlace.hashCode() * 5953 + this.mValue * 4289;
		}

		public boolean isEqual(Object param1Object) {
			boolean bool = (this == param1Object) ? true : false;
			if (!bool && param1Object instanceof HMinus) {
				HMinus hMinus = (HMinus) param1Object;
				bool = (this.mPlace.equals(hMinus.mPlace) && this.mValue == hMinus.mValue) ? true : false;
			}
			return bool;
		}
	}

	private enum STATES {
		Fork, HasR, WaitR, HasL, WaitL, Idle;
	}
}
