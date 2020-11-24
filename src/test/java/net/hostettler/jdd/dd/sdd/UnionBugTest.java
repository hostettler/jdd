package net.hostettler.jdd.dd.sdd;

import org.junit.Assert;
import org.junit.Test;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.ValSet;
import net.hostettler.jdd.dd.ddd.DDDImpl;

public class UnionBugTest {
	@Test
	public void testBug() {
		DD<VAR, ValSet<Integer>> sDD1 = SDDImpl.getAny(VAR.class, Integer.class);
		sDD1 = sDD1.union(SDDImpl.create(VAR.button, DDDImpl.create(NETVAR.on, 0, DDDImpl.create(NETVAR.off, 1)),
				SDDImpl.create(VAR.door, DDDImpl.create(NETVAR.open, 0, DDDImpl.create(NETVAR.closed, 1)),
						SDDImpl.create(VAR.heat, DDDImpl.create(NETVAR.warm, 1, DDDImpl.create(NETVAR.cold, 0))))));

		sDD1 = sDD1.union(SDDImpl.create(VAR.button, DDDImpl.create(NETVAR.on, 1, DDDImpl.create(NETVAR.off, 0)),
				SDDImpl.create(VAR.door, DDDImpl.create(NETVAR.open, 0, DDDImpl.create(NETVAR.closed, 1)),
						SDDImpl.create(VAR.heat, DDDImpl.create(NETVAR.warm, 1, DDDImpl.create(NETVAR.cold, 0))))));
		sDD1 = sDD1.union(SDDImpl.create(VAR.button, DDDImpl.create(NETVAR.on, 1, DDDImpl.create(NETVAR.off, 0)),
				SDDImpl.create(VAR.door, DDDImpl.create(NETVAR.open, 0, DDDImpl.create(NETVAR.closed, 1)),
						SDDImpl.create(VAR.heat, DDDImpl.create(NETVAR.warm, 0, DDDImpl.create(NETVAR.cold, 1))))));
		DD<VAR, ValSet<Integer>> sDD2 = SDDImpl.getAny(VAR.class, Integer.class);
		sDD2 = sDD2.union(SDDImpl.create(VAR.button, DDDImpl.create(NETVAR.on, 0, DDDImpl.create(NETVAR.off, 1)),
				SDDImpl.create(VAR.door, DDDImpl.create(NETVAR.open, 0, DDDImpl.create(NETVAR.closed, 1)),
						SDDImpl.create(VAR.heat, DDDImpl.create(NETVAR.warm, 0, DDDImpl.create(NETVAR.cold, 1))))));
		DD<VAR, ValSet<Integer>> sDD = SDDImpl.create(VAR.button,
				DDDImpl.create(NETVAR.on, 1, DDDImpl.create(NETVAR.off, 0)),
				SDDImpl.create(VAR.door, DDDImpl.create(NETVAR.open, 0, DDDImpl.create(NETVAR.closed, 1)),
						SDDImpl.create(VAR.heat, DDDImpl.create(NETVAR.warm, 0, DDDImpl.create(NETVAR.cold, 1)))));
		DD<VAR, ValSet<Integer>> sDD3 = sDD2.union(sDD);
		System.out.println(sDD3);
		System.out.println("***************");
		System.out.println(sDD1.union(sDD3));
		Assert.assertEquals((sDD1.union(sDD2)).union(sDD), sDD1.union(sDD2.union(sDD)));
	}

	private enum NETVAR {
		on, off, open, closed, cold, warm;
	}

	private enum VAR {
		button, door, heat;
	}
}
