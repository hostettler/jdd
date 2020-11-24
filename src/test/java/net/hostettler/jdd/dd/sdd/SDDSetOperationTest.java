package net.hostettler.jdd.dd.sdd;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.ObjSet;
import net.hostettler.jdd.dd.ValSet;
import net.hostettler.jdd.dd.ddd.DDDImpl;
import net.hostettler.jdd.dd.sdd.SDDImpl;
import net.hostettler.jdd.dd.util.DDGraphGenerator;

public class SDDSetOperationTest {
	private ValSet<Integer> set1;

	private ValSet<Integer> set2;

	private ValSet<Integer> set3;

	private DD<String, ValSet<Integer>> sdd1;

	private DD<String, ValSet<Integer>> sdd2;

	@Before
	public void setup() {
		this.set1 = ObjSet.create(1);
		this.set2 = ObjSet.create(2);
		this.set3 = ObjSet.create(3);
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("B", this.set2);
		DD<String, ValSet<Integer>> sDD2 = SDDImpl.create("B", this.set1, sDD1);
		DD<String, ValSet<Integer>> sDD3 = SDDImpl.create("B", this.set2, sDD1);
		DD<String, ValSet<Integer>> sDD4 = sDD2.union(sDD3);
		DD<String, ValSet<Integer>> sDD5 = SDDImpl.create("B", this.set2);
		this.sdd1 = SDDImpl.create("A", (DD) sDD4, sDD5);
		DD<String, ValSet<Integer>> sDD6 = SDDImpl.create("B", this.set2);
		DD<String, ValSet<Integer>> sDD7 = SDDImpl.create("B", this.set3, sDD6);
		DD<String, ValSet<Integer>> sDD8 = SDDImpl.create("B", this.set2);
		DD<String, ValSet<Integer>> sDD9 = sDD6.union(sDD7);
		this.sdd2 = SDDImpl.create("A", (DD) sDD9, sDD8);
		System.out.println("sdd1: " + this.sdd1);
		System.out.println("sdd2: " + this.sdd2);
		System.out.println((new DDGraphGenerator()).outputDOTFormat((DD) this.sdd1));
	}

	@Test
	public void testSDDUnion1Level() {
		System.out.println("*** Union 1 Level ***");
		ObjSet<Integer> objSet1 = ObjSet.create(Integer.valueOf(99));
		ObjSet<Integer> objSet2 = ObjSet.create(Integer.valueOf(11));
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("V1", objSet1);
		DD<String, ValSet<Integer>> sDD2 = SDDImpl.create("V1", objSet2);
		System.out.println(sDD1);
		System.out.println(sDD2);
		DD<String, ValSet<Integer>> sDD = sDD1.union(sDD2);
		Assert.assertEquals(SDDImpl.create("V1", ObjSet.create(Integer.valueOf(99), Integer.valueOf(11))), sDD);
		System.out.println(sDD);
		System.out.println(sDD.getStates());
	}

	@Test
	public void testSDDUnion0() {
		System.out.println("*** Union 0 ***");
		ObjSet<Integer> objSet1 = ObjSet.create(Integer.valueOf(99));
		ObjSet<Integer> objSet2 = ObjSet.create(Integer.valueOf(11));
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("V1", objSet1, SDDImpl.create("V2", objSet2));
		DD<String, ValSet<Integer>> sDD2 = SDDImpl.create("V1", objSet2, SDDImpl.create("V2", objSet1));
		DD<String, ValSet<Integer>> sDD = sDD1.union(sDD2);
		System.out.println(sDD);
		System.out.println(sDD.getStates());
	}

	@Test
	public void testSDDUnion1() {
		System.out.println("*** Union 1 ***");
		System.out.println("sdd1 + sdd2");
		ObjSet<Integer> objSet = ObjSet.create(new Integer[] { 1, 3 });
		DD<String, ValSet<Integer>> sDD = this.sdd1.union(this.sdd2);
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("B", objSet, SDDImpl.create("B", this.set2))
				.union(SDDImpl.create("B", this.set2, SDDImpl.getTrue(String.class, Integer.class)));
		DD<String, ValSet<Integer>> sDD2 = SDDImpl.create("A", (ValSet) sDD1, SDDImpl.create("B", this.set2));
		System.out.println("res : " + sDD);
		System.out.println("exp : " + sDD2);
		Assert.assertSame(sDD2, sDD);
	}

	@Test
	public void testSDDUnion2() {
		System.out.println("*** Union 2 ***");
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("a", ObjSet.create(1),
				SDDImpl.create("a", ObjSet.create(2), SDDImpl.create("a", ObjSet.create(3))));

		DD<String, ValSet<Integer>> sDD2 = SDDImpl.create("a", ObjSet.create(1),
				SDDImpl.create("a", ObjSet.create(4), SDDImpl.create("a", ObjSet.create(3))));
		DD<String, ValSet<Integer>> sDD = sDD1.union(sDD2);
		DD<String, ValSet<Integer>> sDD3 = SDDImpl.create("a", ObjSet.create(1), SDDImpl.create("a",
				(ValSet) ObjSet.create((Object[]) new Integer[] { 2, 4 }), SDDImpl.create("a", ObjSet.create(3))));
		System.out.println("res : " + sDD);
		System.out.println("exp : " + sDD3);
		Assert.assertSame(sDD3, sDD);
	}

	@Test
	public void testSDDUnion5() {
		System.out.println("*** Union 5 ***");
		System.out.println("*** 1 ***");
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("v0", ObjSet.create(0),
				SDDImpl.create("v1", ObjSet.create(Integer.valueOf(-1)), SDDImpl.create("v2", ObjSet.create(2))));
		DD<String, ValSet<Integer>> sDD2 = SDDImpl.create("v0", ObjSet.create(0),
				SDDImpl.create("v1", ObjSet.create(2), SDDImpl.create("v2", ObjSet.create(2))));
		System.out.println(sDD1.union(sDD2));
		DD<String, ValSet<Integer>> sDD3 = SDDImpl.create("v0", ObjSet.create(0),
				SDDImpl.create("v1", ObjSet.create(-1, 2), SDDImpl.create("v2", ObjSet.create(2))));
		Assert.assertEquals(sDD3, sDD1.union(sDD2));
		System.out.println("*** 2 ***");
		sDD1 = SDDImpl.create("v0", ObjSet.create(0, 1),
				SDDImpl.create("v1", ObjSet.create(0), SDDImpl.create("v2", ObjSet.create(2))));
		sDD2 = SDDImpl.create("v0", ObjSet.create(0),
				SDDImpl.create("v1", ObjSet.create(2), SDDImpl.create("v2", ObjSet.create(2))));
		System.out.println(sDD1.union(sDD2));
		sDD3 = (DD<String, ValSet<Integer>>) SDDImpl
				.create("v0", ObjSet.create(0),
						SDDImpl.create("v1", ObjSet.create(0, 2), SDDImpl.create("v2", ObjSet.create(2))))
				.union(SDDImpl.create("v0", ObjSet.create(1),
						SDDImpl.create("v1", ObjSet.create(0), SDDImpl.create("v2", ObjSet.create(2)))));
		Assert.assertEquals(sDD3, sDD1.union(sDD2));
		System.out.println("*** 3 ***");
		sDD1 = SDDImpl.create("v0", ObjSet.create(0,1),
				SDDImpl.create("v1", ObjSet.create(0), SDDImpl.create("v2", ObjSet.create(2))));
		sDD2 = SDDImpl.create("v0", ObjSet.create(0),
				SDDImpl.create("v1", ObjSet.create(2), SDDImpl.create("v2", ObjSet.create(1))));
		System.out.println(sDD1.union(sDD2));
		sDD3 = SDDImpl
				.create("v0", ObjSet.create(1),
						SDDImpl.create("v1", ObjSet.create(0), SDDImpl.create("v2", ObjSet.create(2))))
				.union(SDDImpl.create("v0", ObjSet.create(0), (DD<String, ValSet<Integer>>) SDDImpl
						.create("v1", ObjSet.create(2), SDDImpl.create("v2", ObjSet.create(1)))
						.union(SDDImpl.create("v1", ObjSet.create(0), SDDImpl.create("v2", ObjSet.create(2))))));
		Assert.assertEquals(sDD3, sDD1.union(sDD2));
	}

	@Test
	public void testSDDDifference() {
		System.out.println("*** Difference ***");
		System.out.println("sdd1 - sdd2");
		System.out.println(this.sdd1.difference(this.sdd2));
		this.sdd2 = this.sdd2.union(SDDImpl.create("B", this.set3));
		Assert.assertSame(this.sdd2, this.sdd1.difference(this.sdd2));
	}

	@Test
	public void testSDDIntersection() {
		System.out.println("*** Intersection ***");
		System.out.println("sdd1 * sdd2");
		DD<String, ValSet<Integer>> sDD = SDDImpl.create("B", this.set2);
		this.sdd2 = SDDImpl.create("A", (ValSet) SDDImpl.create("B", this.set2, sDD), sDD);
		DD<String, ValSet<Integer>> interSDD = this.sdd1.intersection(this.sdd2);
		System.out.println(interSDD);
		Assert.assertSame(this.sdd2, interSDD);
	}

	@Test
	public void testSDDSplit() {
		System.out.println("*** Split ***");
		System.out.println("sdd1.split()");
		System.out.println(this.sdd1.split());
		Assert.assertEquals(2L, this.sdd1.split().size());
		DD<String, ValSet<Integer>> sDD = SDDImpl.getFalse(String.class, Integer.class);
		for (Object sDD1 : this.sdd1.split())
			sDD = sDD.union((DD) sDD1);
		Assert.assertEquals(this.sdd1, sDD);

		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("a", ObjSet.create(1));
		for (int i = 1; i < 15; i++) {
			sDD1 = sDD1.union(SDDImpl.create("a", ObjSet.create(i)));
		}
		System.out.println(sDD1);
		Assert.assertSame(14, sDD1.split().size());

		sDD1 = SDDImpl.create("a", ObjSet.create(1));
		for (int i = 1; i < 100; i++) {
			sDD1 = sDD1.union(SDDImpl.create("a", ObjSet.create(i)));
		}
		System.out.println(sDD1);
		Assert.assertSame(99, sDD1.split().size());
	}
}
