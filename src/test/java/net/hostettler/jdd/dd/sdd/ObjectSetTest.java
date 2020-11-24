package net.hostettler.jdd.dd.sdd;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.hostettler.jdd.dd.ObjSet;

public class ObjectSetTest {
	private ObjSet<Integer> mSet1;

	private ObjSet<Integer> mSet2;

	private ObjSet<Integer> mSet3;

	private ObjSet<Integer> mSet4;

	private ObjSet<Integer> mSet5;

	@Before
	public void setup() {
		this.mSet1 = ObjSet.create(1);
		this.mSet2 = ObjSet.create(2);
		this.mSet3 = ObjSet.create(1, 2);
		this.mSet4 = ObjSet.create(2, 3);
		this.mSet5 = ObjSet.create(1, 2, 3);
	}

	@Test
	public void testDifference() {
		System.out.println("*** Difference ***");
		System.out.println(this.mSet3.difference(this.mSet4));
		Assert.assertSame(this.mSet1, this.mSet3.difference(this.mSet4));
	}

	@Test
	public void testIntersection() {
		System.out.println("*** Intersection ***");
		System.out.println(this.mSet3.intersection(this.mSet4));
		Assert.assertSame(this.mSet2, this.mSet3.intersection(this.mSet4));
	}

	@Test
	public void testUnion() {
		System.out.println("*** Union ***");
		System.out.println(this.mSet3.union(this.mSet4));
		Assert.assertSame(this.mSet5, this.mSet3.union(this.mSet4));
	}

	@Test
	public void testCanonicity() {
		System.out.println("*** Canonicity ***");
		Assert.assertSame(this.mSet3,
				ObjSet.create((Object[]) new Integer[] { Integer.valueOf(1), Integer.valueOf(2) }));
		Assert.assertSame(this.mSet4,
				ObjSet.create((Object[]) new Integer[] { Integer.valueOf(2), Integer.valueOf(3) }));
	}

	@Test
	public void testNull() {
		Assert.assertSame(ObjSet.createEmtpy(), ObjSet.create((Integer) null));
	}
}
