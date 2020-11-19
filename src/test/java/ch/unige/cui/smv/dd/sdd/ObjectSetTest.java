package ch.unige.cui.smv.dd.sdd;

import ch.unige.cui.smv.dd.ObjSet;
import ch.unige.cui.smv.dd.ValSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ObjectSetTest {
	private ObjSet<Integer> mSet1;

	private ObjSet<Integer> mSet2;

	private ObjSet<Integer> mSet3;

	private ObjSet<Integer> mSet4;

	private ObjSet<Integer> mSet5;

	@Before
	public void setup() {
		this.mSet1 = ObjSet.create(Integer.valueOf(1));
		this.mSet2 = ObjSet.create(Integer.valueOf(2));
		this.mSet3 = (ObjSet) ObjSet.create((Object[]) new Integer[] { Integer.valueOf(1), Integer.valueOf(2) });
		this.mSet4 = (ObjSet) ObjSet.create((Object[]) new Integer[] { Integer.valueOf(2), Integer.valueOf(3) });
		this.mSet5 = (ObjSet) ObjSet
				.create((Object[]) new Integer[] { Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3) });
	}

	@Test
	public void testDifference() {
		System.out.println("*** Difference ***");
		System.out.println(this.mSet3.difference((ValSet) this.mSet4));
		Assert.assertSame(this.mSet1, this.mSet3.difference((ValSet) this.mSet4));
	}

	@Test
	public void testIntersection() {
		System.out.println("*** Intersection ***");
		System.out.println(this.mSet3.intersection((ValSet) this.mSet4));
		Assert.assertSame(this.mSet2, this.mSet3.intersection((ValSet) this.mSet4));
	}

	@Test
	public void testUnion() {
		System.out.println("*** Union ***");
		System.out.println(this.mSet3.union((ValSet) this.mSet4));
		Assert.assertSame(this.mSet5, this.mSet3.union((ValSet) this.mSet4));
	}

	@Test
	public void testCanonicity() {
		System.out.println("*** Canonicity ***");
		Assert.assertSame(this.mSet3,
				ObjSet.create((Object[]) new Integer[] { Integer.valueOf(1), Integer.valueOf(2) }));
		Assert.assertSame(this.mSet4,
				ObjSet.create((Object[]) new Integer[] { Integer.valueOf(2), Integer.valueOf(3) }));
	}
}

/*
 * Location:
 * C:\Users\steve.hostettler\Downloads\alpina\eclipse_alpina\configuration\org.
 * eclipse.osgi\bundles\7\1\.cp\lib\jdd_1.1.2.jar!\c\\unige\cui\smv\dd\sdd\
 * ObjectSetTest.class Java compiler version: 6 (50.0) JD-Core Version: 1.1.3
 */