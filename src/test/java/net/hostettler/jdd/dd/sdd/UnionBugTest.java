package net.hostettler.jdd.dd.sdd;

import org.junit.Assert;
import org.junit.Test;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.ValSet;
import net.hostettler.jdd.dd.ddd.DDDImpl;
import net.hostettler.jdd.dd.sdd.SDDImpl;

public class UnionBugTest {
	@Test
	public void testBug() {
		DD sDD1 = SDDImpl.SDD_FALSE;
		sDD1 =  sDD1.union(SDDImpl.create(VAR.button,
				(ValSet<?>) DDDImpl.create(NETVAR.on, Integer.valueOf(0),
						DDDImpl.create(NETVAR.off, Integer.valueOf(1))),
				SDDImpl.create(VAR.door,
						(DD) (ValSet<?>) DDDImpl.create(NETVAR.open, Integer.valueOf(0),
								DDDImpl.create(NETVAR.closed, Integer.valueOf(1))),
						SDDImpl.create(VAR.heat, (ValSet<?>) DDDImpl.create(NETVAR.warm, Integer.valueOf(1),
								DDDImpl.create(NETVAR.cold, Integer.valueOf(0)))))));
		sDD1 =  sDD1.union(SDDImpl.create(VAR.button,
				(ValSet<?>) DDDImpl.create(NETVAR.on, Integer.valueOf(1),
						DDDImpl.create(NETVAR.off, Integer.valueOf(0))),
				SDDImpl.create(VAR.door,
						(DD) (ValSet<?>) DDDImpl.create(NETVAR.open, Integer.valueOf(0),
								DDDImpl.create(NETVAR.closed, Integer.valueOf(1))),
						SDDImpl.create(VAR.heat, (ValSet<?>) DDDImpl.create(NETVAR.warm, Integer.valueOf(1),
								DDDImpl.create(NETVAR.cold, Integer.valueOf(0)))))));
		sDD1 =  sDD1.union(SDDImpl.create(VAR.button,
				(ValSet<?>) DDDImpl.create(NETVAR.on, Integer.valueOf(1),
						DDDImpl.create(NETVAR.off, Integer.valueOf(0))),
				SDDImpl.create(VAR.door,
						(DD) (ValSet<?>) DDDImpl.create(NETVAR.open, Integer.valueOf(0),
								DDDImpl.create(NETVAR.closed, Integer.valueOf(1))),
						SDDImpl.create(VAR.heat, (ValSet<?>) DDDImpl.create(NETVAR.warm, Integer.valueOf(0),
								DDDImpl.create(NETVAR.cold, Integer.valueOf(1)))))));
		DD sDD2 = SDDImpl.SDD_FALSE;
		sDD2 =  sDD2.union(SDDImpl.create(VAR.button,
				(ValSet<?>) DDDImpl.create(NETVAR.on, Integer.valueOf(0),
						DDDImpl.create(NETVAR.off, Integer.valueOf(1))),
				SDDImpl.create(VAR.door,
						(DD) (ValSet<?>) DDDImpl.create(NETVAR.open, Integer.valueOf(0),
								DDDImpl.create(NETVAR.closed, Integer.valueOf(1))),
						SDDImpl.create(VAR.heat, (ValSet<?>) DDDImpl.create(NETVAR.warm, Integer.valueOf(0),
								DDDImpl.create(NETVAR.cold, Integer.valueOf(1)))))));
		DD<VAR, ?> sDD = SDDImpl.create(VAR.button,
				(ValSet<?>) DDDImpl.create(NETVAR.on, Integer.valueOf(1),
						DDDImpl.create(NETVAR.off, Integer.valueOf(0))),
				SDDImpl.create(VAR.door,
						(DD) (ValSet<?>) DDDImpl.create(NETVAR.open, Integer.valueOf(0),
								DDDImpl.create(NETVAR.closed, Integer.valueOf(1))),
						SDDImpl.create(VAR.heat, (ValSet<?>) DDDImpl.create(NETVAR.warm, Integer.valueOf(0),
								DDDImpl.create(NETVAR.cold, Integer.valueOf(1))))));
		DD sDD3 =  sDD2.union(sDD);
		System.out.println(sDD3);
		System.out.println("***************");
		System.out.println(sDD1.union(sDD3));
		Assert.assertEquals(( sDD1.union(sDD2)).union(sDD), sDD1.union(sDD2.union(sDD)));
	}

	private enum NETVAR {
		on, off, open, closed, cold, warm;
	}

	private enum VAR {
		button, door, heat;
	}
}
