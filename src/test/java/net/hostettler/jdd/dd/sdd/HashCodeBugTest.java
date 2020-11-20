package net.hostettler.jdd.dd.sdd;

import org.junit.Assert;
import org.junit.Test;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.ObjSet;
import net.hostettler.jdd.dd.ValSet;
import net.hostettler.jdd.dd.sdd.SDDImpl;

public class HashCodeBugTest {
	@Test
	public void testBug() {
		DD<String, ValSet<Integer>> sDD3 = SDDImpl.create("b", (ValSet) ObjSet.create(Integer.valueOf(3)),
				(DD) SDDImpl.SDD_TRUE);
		DD<String, ValSet<Integer>> sDD4 = SDDImpl.create("b", (ValSet) ObjSet.create(Integer.valueOf(4)),
				(DD) SDDImpl.SDD_TRUE);
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("a", (ValSet) ObjSet.create(Integer.valueOf(1)), sDD3);
		sDD1.addAlpha((ValSet) ObjSet.create(Integer.valueOf(2)), sDD4);
		System.out.println(sDD1);
		System.out.println("HashCode = " + sDD1.hashCode());
		DD<String, ?> sDD2 = SDDImpl.create("a", (ValSet) ObjSet.create(Integer.valueOf(1)), sDD3);
		sDD2 = sDD2.union(SDDImpl.create("a", (ValSet) ObjSet.create(Integer.valueOf(2)), sDD4));
		System.out.println(sDD2);
		System.out.println("HashCode = " + sDD2.hashCode());
		Assert.assertEquals(sDD1.hashCode(), sDD2.hashCode());
	}
}
