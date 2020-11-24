package net.hostettler.jdd.dd.sdd;

import org.junit.Assert;
import org.junit.Test;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.ObjSet;
import net.hostettler.jdd.dd.ValSet;
import net.hostettler.jdd.dd.ddd.DDDImpl;

public class Tutorial4 {
	@Test
	public void testSDD() {
		DD<String, Integer> dDD1 = DDDImpl.create("x", 1, DDDImpl.create("y", 2, DDDImpl.create("z", 3)));
		DD<String, Integer> dDD2 = DDDImpl.create("x", 2, DDDImpl.create("y", 3, DDDImpl.create("z", 1)));
		DD<String, Integer> dDD3 = DDDImpl.create("x", 3, DDDImpl.create("y", 1, DDDImpl.create("z", 2)));
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("a", dDD1,
				SDDImpl.create("b", dDD2, SDDImpl.create("c", dDD3)));
		System.out.println(sDD1);
		DD<String, ValSet<Integer>> sDD2 = SDDImpl.create("a", dDD1);
		DD<String, ValSet<Integer>> sDD3 = SDDImpl.create("b", dDD2);
		DD<String, ValSet<Integer>> sDD4 = SDDImpl.create("c", dDD3);
		DD<String, ValSet<Integer>> sDD = sDD2.append(sDD3.append(sDD4));
		System.out.println(sDD);
		Assert.assertTrue((sDD.equals(sDD1) && sDD1.equals(sDD)));
	}

	@Test
	public void test() {
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("a", ObjSet.create(1, 2, 4),
				SDDImpl.create("b", ObjSet.create(1, 5), SDDImpl.create("c", ObjSet.create(11, 12))));
		DD<String, ValSet<Integer>> sDD2 = SDDImpl.create("a", ObjSet.create(8, 5),
				SDDImpl.create("b", ObjSet.create(3, 4), SDDImpl.create("c", ObjSet.create(11, 12))));
		System.out.println("************");
		System.out.println(sDD1);
		System.out.println(sDD2);
		System.out.println(sDD1.union(sDD2));
		sDD1 = SDDImpl.create("b", ObjSet.create(1, 5), SDDImpl.create("c", ObjSet.create(1, 12)));
		sDD2 = SDDImpl.create("b", ObjSet.create(3, 4), SDDImpl.create("c", ObjSet.create(11, 12)));
		System.out.println("************");
		System.out.println(sDD1);
		System.out.println(sDD2);
		System.out.println(sDD1.union(sDD2));
		sDD1 = SDDImpl.create("a", ObjSet.create(1, 2, 4), SDDImpl.create("b", ObjSet.create(1, 5)));
		sDD2 = SDDImpl.create("a", ObjSet.create(8, 5), SDDImpl.create("b", ObjSet.create(3, 4)));
		System.out.println("************");
		System.out.println(sDD1);
		System.out.println(sDD2);
		System.out.println(sDD1.union(sDD2));
	}
}
