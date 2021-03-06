package net.hostettler.jdd.dd.ddd;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.hostettler.jdd.dd.DD;

public class DDDSetOperationTest {
	private Integer value1;

	private Integer value2;

	private Integer value3;

	private DD<String, Integer> ddd1;

	private DD<String, Integer> ddd2;

	@Before
	public void setup() {
		this.value1 = Integer.valueOf(1);
		this.value2 = Integer.valueOf(2);
		this.value3 = Integer.valueOf(3);
		DD<String, Integer> dDD1 = DDDImpl.create("B", this.value2);
		DD<String, Integer> dDD2 = DDDImpl.create("B", this.value1, dDD1);
		DD<String, Integer> dDD3 = DDDImpl.create("B", this.value2, dDD1);
		DD<String, Integer> dDD4 = (DD) dDD2.union(dDD3);
		this.ddd1 = dDD4;
		DD<String, Integer> dDD5 = DDDImpl.create("B", this.value2);
		dDD5 = DDDImpl.create("B", this.value2, dDD5);
		DD<String, Integer> dDD6 = DDDImpl.create("B", this.value3, dDD5);
		DD<String, Integer> dDD7 = dDD5.union(dDD6);
		this.ddd2 = dDD7;
		System.out.println("ddd1: " + this.ddd1);
		System.out.println("ddd2: " + this.ddd2);
	}

	@Test
	public void testdddUnion() {
		System.out.println("*** Union ***");
		System.out.println("ddd1 + ddd2");
		System.out.println(this.ddd1.union(this.ddd2));
		
		DD<String, Integer> dDD = DDDImpl.create("B", this.value1, DDDImpl.create("B", this.value2));
		
		dDD = (DD<String, Integer>) dDD.union(DDDImpl.create("B", this.value2, DDDImpl.create("B", this.value2)));
		dDD = (DD<String, Integer>) dDD.union(
				DDDImpl.create("B", this.value3, DDDImpl.create("B", this.value2, DDDImpl.create("B", this.value2))));
		Assert.assertSame(dDD, this.ddd1.union(this.ddd2));
		Assert.assertEquals(dDD.getStates(), ((DD) this.ddd1.union(this.ddd2)).getStates(), 0.01D);
		Assert.assertEquals(3.0D, ((DD) this.ddd1.union(this.ddd2)).getStates(), 0.01D);
		
		Assert.assertEquals(DDDImpl.getAny(String.class, Integer.class), (this.ddd1.union(this.ddd2).union(DDDImpl.getTrue(String.class, Integer.class))) );
		Assert.assertEquals(this.ddd1.union(this.ddd2), (this.ddd1.union(this.ddd2).union(DDDImpl.getFalse(String.class, Integer.class))) );
		Assert.assertEquals(DDDImpl.getAny(String.class, Integer.class), (this.ddd1.union(this.ddd2).union(DDDImpl.getAny(String.class, Integer.class))) );
		
	}

	@Test
	public void testdddDifference() {
		System.out.println("*** Difference ***");
		System.out.println("ddd1 - ddd2");
		System.out.println(this.ddd1.difference(this.ddd2));
		DD<String, Integer> dDD = DDDImpl.create("B", this.value1, DDDImpl.create("B", this.value2));
		Assert.assertSame(dDD, this.ddd1.difference(this.ddd2));
		
		Assert.assertEquals((this.ddd1.union(this.ddd2).difference(DDDImpl.getTrue(String.class, Integer.class))), (this.ddd1.union(this.ddd2).difference(DDDImpl.getTrue(String.class, Integer.class))) );
		Assert.assertEquals(this.ddd1.union(this.ddd2), (this.ddd1.union(this.ddd2).difference(DDDImpl.getFalse(String.class, Integer.class))) );
		Assert.assertEquals(DDDImpl.getAny(String.class, Integer.class), (this.ddd1.union(this.ddd2).difference(DDDImpl.getAny(String.class, Integer.class))) );
	}

	@Test
	public void testdddIntersection() {
		System.out.println("*** Intersection ***");
		System.out.println("ddd1 * ddd2");
		System.out.println(this.ddd1.intersection(this.ddd2));
		DD<String, Integer> dDD = DDDImpl.create("B", this.value2, DDDImpl.create("B", this.value2));
		Assert.assertSame(dDD, this.ddd1.intersection(this.ddd2));
		
		Assert.assertEquals(DDDImpl.getFalse(String.class, Integer.class), (this.ddd1.union(this.ddd2).intersection(DDDImpl.getTrue(String.class, Integer.class))) );
		Assert.assertEquals(DDDImpl.getFalse(String.class, Integer.class), (this.ddd1.union(this.ddd2).intersection(DDDImpl.getFalse(String.class, Integer.class))) );
		Assert.assertEquals(DDDImpl.getAny(String.class, Integer.class), (this.ddd1.union(this.ddd2).intersection(DDDImpl.getAny(String.class, Integer.class))) );
	}
	
	@Test
	public void testdddSplit() {
		
		System.out.println("*** Split ***");				
		DD<String, Integer> dDD = DDDImpl.create("a", 1);
		Assert.assertSame(1, dDD.split().size());
		
		System.out.println("*** Split ***");		
		System.out.println(this.ddd1.union(this.ddd2));		
		Assert.assertSame(3, (this.ddd1.union(this.ddd2)).split().size());
		
		for (int i = 1; i < 100; i++) {
			dDD= dDD.union(DDDImpl.create("a", i));
		}
		Assert.assertSame(99, dDD.split().size());
	}
	
	
}

