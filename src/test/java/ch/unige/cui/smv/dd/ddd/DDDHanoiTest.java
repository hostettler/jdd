package ch.unige.cui.smv.dd.ddd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import ch.unige.cui.smv.dd.DD;
import ch.unige.cui.smv.dd.Hom;
import ch.unige.cui.smv.dd.HomImpl;

public class DDDHanoiTest {
	public static final int NB_RINGS = 10;

	public static final int NB_POLES = 3;

	@Test
	public void testHanoi() throws IOException {
		ArrayList<Ring> arrayList = new ArrayList();
		ArrayList<Pole> arrayList1 = new ArrayList();
		byte b;
		for (b = 0; b < 10; b++)
			arrayList.add(new Ring(b, b));
		for (b = 0; b < 3; b++)
			arrayList1.add(new Pole(b));
		DD<Ring, Pole> dDD = (DD) DDDImpl.DDD_TRUE;
		for (int i = arrayList.size(); i > 0; i--)
			dDD = (DD<Ring, Pole>) dDD.append( DDDImpl.create(arrayList.get(i - 1), arrayList1.get(0)));
		System.out.println(dDD);
		Hom<Ring, Pole> dDDHom = new DDDIdHom<Ring, Pole>();
		for (Ring ring : arrayList) {
			for (Pole pole : arrayList1) {
				for (Pole pole1 : arrayList1) {
					if (pole != pole1)
						dDDHom = dDDHom.union(new MoveRingHom(ring, pole, pole1));
				}
			}
		}
		dDDHom =  dDDHom.fixpoint();
		long l1 = System.currentTimeMillis();
		dDD = dDDHom.phi(dDD, new Object[0]);
		System.out.println("Time : " + (System.currentTimeMillis() - l1));
		System.out.println("nb states : " + dDD.getStates());
		long l2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println("Mem (MB) : " + (l2 / 1024L / 1024L));
		System.out.println("Hits : " + HomImpl.getHits() + " Cache Hits :" + HomImpl.getCacheHits());
		Assert.assertEquals(59049.0D, dDD.getStates(), 0.001D);
	}

	private class MoveRingHom extends DDDHomImpl<Ring, Pole> {
		private DDDHanoiTest.Ring mRing;

		private DDDHanoiTest.Pole mOrigin;

		private DDDHanoiTest.Pole mDestination;

		private DDDHanoiTest.ValidMove mValidMove;

		public MoveRingHom(DDDHanoiTest.Ring param1Ring, DDDHanoiTest.Pole param1Pole1, DDDHanoiTest.Pole param1Pole2) {
			this.mRing = param1Ring;
			this.mOrigin = param1Pole1;
			this.mDestination = param1Pole2;
			this.mValidMove = new DDDHanoiTest.ValidMove(this.mRing, this.mOrigin, this.mDestination);
		}

		protected DD<DDDHanoiTest.Ring, DDDHanoiTest.Pole> phi(DDDHanoiTest.Ring param1Ring,
				DDDHanoiTest.Pole param1Pole,
				Map<DDDHanoiTest.Pole, DD<DDDHanoiTest.Ring, DDDHanoiTest.Pole>> param1Map, Object... param1VarArgs) {
			return !param1Ring.equals(this.mRing)
					? DDDImpl.create(param1Ring, param1Pole,
							(DD<DDDHanoiTest.Ring, DDDHanoiTest.Pole>) phi(id(param1Map, param1Pole), new Object[0]))
					: (!param1Pole.equals(this.mOrigin) ? (DD<DDDHanoiTest.Ring, DDDHanoiTest.Pole>) getDDFalse()
							: DDDImpl.create(param1Ring, this.mDestination,
									(DD<DDDHanoiTest.Ring, DDDHanoiTest.Pole>) this.mValidMove
											.phi(id(param1Map, param1Pole), new Object[0])));
		}

		protected DD<DDDHanoiTest.Ring, DDDHanoiTest.Pole> phi1(Object... param1VarArgs) {
			return getDDTrue();
		}

		public String toString() {
			return "Move " + this.mRing.toString() + " from " + this.mOrigin.toString() + " to " + this.mDestination;
		}

		public int computeHashCode() {
			return getClass().hashCode() * 7523 + this.mRing.hashCode() * 5953 + this.mOrigin.hashCode() * 4289
					+ this.mDestination.hashCode() * 3539;
		}

		public boolean isEqual(Object param1Object) {
			boolean bool = (this == param1Object) ? true : false;
			if (!bool && param1Object instanceof MoveRingHom) {
				MoveRingHom moveRingHom = (MoveRingHom) param1Object;
				bool = (this.mRing == moveRingHom.mRing && this.mOrigin == moveRingHom.mOrigin
						&& this.mDestination == moveRingHom.mDestination) ? true : false;
			}
			return bool;
		}
	}

	private class ValidMove extends DDDHomImpl<Ring, Pole> {
		private DDDHanoiTest.Ring mRing;

		private DDDHanoiTest.Pole mOrigin;

		private DDDHanoiTest.Pole mDestination;

		public ValidMove(DDDHanoiTest.Ring param1Ring, DDDHanoiTest.Pole param1Pole1, DDDHanoiTest.Pole param1Pole2) {
			this.mRing = param1Ring;
			this.mOrigin = param1Pole1;
			this.mDestination = param1Pole2;
		}

		protected DD<DDDHanoiTest.Ring, DDDHanoiTest.Pole> phi(DDDHanoiTest.Ring param1Ring,
				DDDHanoiTest.Pole param1Pole,
				Map<DDDHanoiTest.Pole, DD<DDDHanoiTest.Ring, DDDHanoiTest.Pole>> param1Map, Object... param1VarArgs) {
			return ((param1Pole.equals(this.mOrigin) && !this.mRing.isSmallerThan(param1Ring))
					|| (param1Pole.equals(this.mDestination) && this.mRing.isSmallerThan(param1Ring)))
							? (DD<DDDHanoiTest.Ring, DDDHanoiTest.Pole>) getDDFalse()
							: DDDImpl.create(param1Ring, param1Pole,
									(DD<DDDHanoiTest.Ring, DDDHanoiTest.Pole>) phi(id(param1Map, param1Pole),
											new Object[0]));
		}

		protected DD<DDDHanoiTest.Ring, DDDHanoiTest.Pole> phi1(Object... param1VarArgs) {
			return  getDDTrue();
		}

		public int computeHashCode() {
			return getClass().hashCode() * 3 + this.mRing.hashCode() * 17 + this.mOrigin.hashCode() * 123
					+ this.mDestination.hashCode() * 23;
		}

		public boolean isEqual(Object param1Object) {
			boolean bool = (this == param1Object) ? true : false;
			if (!bool && param1Object instanceof ValidMove) {
				ValidMove validMove = (ValidMove) param1Object;
				bool = (this.mRing == validMove.mRing && this.mOrigin == validMove.mOrigin
						&& this.mDestination == validMove.mDestination) ? true : false;
			}
			return bool;
		}
	}

	private final class Pole {
		private int mNumber;

		public Pole(int param1Int) {
			this.mNumber = param1Int;
		}

		public String toString() {
			return "" + this.mNumber;
		}
	}

	private final class Ring {
		private int mSize;

		private int mNumber;

		private Ring(int param1Int1, int param1Int2) {
			this.mSize = param1Int2;
			this.mNumber = param1Int1;
		}

		public boolean isSmallerThan(Ring param1Ring) {
			return (this.mSize < param1Ring.mSize);
		}

		public String toString() {
			return "ring " + this.mNumber;
		}
	}
}

/*
 * Location:
 * C:\Users\steve.hostettler\Downloads\alpina\eclipse_alpina\configuration\org.
 * eclipse.osgi\bundles\7\1\.cp\lib\jdd_1.1.2.jar!\c\\unige\cui\smv\dd\ddd\
 * DDDHanoiTest.class Java compiler version: 6 (50.0) JD-Core Version: 1.1.3
 */