package net.hostettler.jdd.dd.sdd;

import org.junit.Assert;
import org.junit.Test;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.ObjSet;
import net.hostettler.jdd.dd.ValSet;
import net.hostettler.jdd.dd.ddd.DDDImpl;
import net.hostettler.jdd.dd.sdd.SDDImpl;

public class Tutorial4 {
	@Test
	public void testSDD() {
		DD dDD1 = DDDImpl.create("x", Integer.valueOf(1),
				DDDImpl.create("y", Integer.valueOf(2), DDDImpl.create("z", Integer.valueOf(3))));
		DD dDD2 = DDDImpl.create("x", Integer.valueOf(2),
				DDDImpl.create("y", Integer.valueOf(3), DDDImpl.create("z", Integer.valueOf(1))));
		DD dDD3 = DDDImpl.create("x", Integer.valueOf(3),
				DDDImpl.create("y", Integer.valueOf(1), DDDImpl.create("z", Integer.valueOf(2))));
		DD sDD1 = SDDImpl.create("a", dDD1, SDDImpl.create("b", dDD2,  SDDImpl.create("c", dDD3)));
		System.out.println(sDD1);
		DD sDD2 = SDDImpl.create("a", dDD1);
		DD sDD3 = SDDImpl.create("b", dDD2);
		DD sDD4 = SDDImpl.create("c", dDD3);
		DD sDD =  sDD2.append(sDD3.append(sDD4));
		System.out.println(sDD);
		Assert.assertTrue((sDD.equals(sDD1) && sDD1.equals(sDD)));
	}

	@Test
	public void test() {
		DD sDD1 = SDDImpl.create("a",
				ObjSet.create((Object[]) new Integer[] { Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(4) }),
				SDDImpl.create("b", (ValSet) ObjSet.create((Object[]) new Integer[] { Integer.valueOf(1), Integer.valueOf(5) }),
						SDDImpl.create("c",
								ObjSet.create((Object[]) new Integer[] { Integer.valueOf(11), Integer.valueOf(12) }))));
		DD sDD2 = SDDImpl.create("a",
				ObjSet.create((Object[]) new Integer[] { Integer.valueOf(8), Integer.valueOf(5) }),
				SDDImpl.create("b", (ValSet)ObjSet.create((Object[]) new Integer[] { Integer.valueOf(3), Integer.valueOf(4) }),
						SDDImpl.create("c",
								ObjSet.create((Object[]) new Integer[] { Integer.valueOf(11), Integer.valueOf(12) }))));
		System.out.println("************");
		System.out.println(sDD1);
		System.out.println(sDD2);
		System.out.println(sDD1.union(sDD2));
		sDD1 = SDDImpl.create("b", (ValSet)ObjSet.create((Object[]) new Integer[] { Integer.valueOf(1), Integer.valueOf(5) }),
				SDDImpl.create("c",
						ObjSet.create((Object[]) new Integer[] { Integer.valueOf(11), Integer.valueOf(12) })));
		sDD2 = SDDImpl.create("b", (ValSet)ObjSet.create((Object[]) new Integer[] { Integer.valueOf(3), Integer.valueOf(4) }),
				SDDImpl.create("c",
						ObjSet.create((Object[]) new Integer[] { Integer.valueOf(11), Integer.valueOf(12) })));
		System.out.println("************");
		System.out.println(sDD1);
		System.out.println(sDD2);
		System.out.println(sDD1.union(sDD2));
		sDD1 = SDDImpl.create("a",
				(ValSet)ObjSet.create((Object[]) new Integer[] { Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(4) }),
				SDDImpl.create("b",
						ObjSet.create((Object[]) new Integer[] { Integer.valueOf(1), Integer.valueOf(5) })));
		sDD2 = SDDImpl.create("a", (ValSet)ObjSet.create((Object[]) new Integer[] { Integer.valueOf(8), Integer.valueOf(5) }),
				SDDImpl.create("b",
						ObjSet.create((Object[]) new Integer[] { Integer.valueOf(3), Integer.valueOf(4) })));
		System.out.println("************");
		System.out.println(sDD1);
		System.out.println(sDD2);
		System.out.println(sDD1.union(sDD2));
	}
}
