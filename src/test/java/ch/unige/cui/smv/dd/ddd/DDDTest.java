package ch.unige.cui.smv.dd.ddd;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ch.unige.cui.smv.dd.DD;

public class DDDTest {
	private DD<String, Integer> mDDD1;

	private DD<String, Integer> mDDD2;

	private Integer value1;

	@Before
	public void setup() {
		this.value1 = Integer.valueOf(1);
		this.mDDD1 = DDDImpl.create("a", this.value1);
		this.mDDD2 = DDDImpl.create("b", this.value1);
	}

	@Test
	public void testCanonicity() {
		System.out.println("****** Canonicity ******");
		DD<String, Integer> dDD1 = DDDImpl.create("a", Integer.valueOf(1));
		DD<String, Integer> dDD2 = DDDImpl.create("b", Integer.valueOf(1));
		Assert.assertSame(this.mDDD1, dDD1);
		Assert.assertSame(this.mDDD2, dDD2);
	}

	@Test
	public void testLeafes() {
		System.out.println("****** LEAFES ******");
		System.out.println(DDDImpl.DDD_TRUE);
		System.out.println(DDDImpl.DDD_FALSE);
		System.out.println(DDDImpl.DDD_ANY);
	}

	@Test
	public void testSimpleDDDs() {
		System.out.println("****** Build ******");
		System.out.println(this.mDDD1);
		System.out.println(this.mDDD2);
	}

	@Test
	public void testConcatDDDs() {
		System.out.println("****** Concat ******");
		System.out.println("Concat :" + this.mDDD1.append(this.mDDD2));
		Assert.assertEquals("a--1-->b--1-->ONE", ((DD) this.mDDD1.append(this.mDDD2)).toString());
	}

	@Test
	public void testCopy() {
		System.out.println("****** Copy ******");
		Assert.assertEquals(DDDImpl.DDD_TRUE, DDDImpl.DDD_TRUE.copy());
		Assert.assertEquals(DDDImpl.DDD_FALSE, DDDImpl.DDD_FALSE.copy());
		Assert.assertEquals(DDDImpl.DDD_ANY, DDDImpl.DDD_ANY.copy());
		System.out.println("Original:" + this.mDDD1);
		System.out.println("Copy: " + this.mDDD1.copy());
		Assert.assertEquals(this.mDDD1.toString(), this.mDDD1.copy().toString());
		this.mDDD2 = this.mDDD2.append(this.mDDD1);
		System.out.println(this.mDDD2);
		this.mDDD2 = this.mDDD2.append(this.mDDD1);
		System.out.println(this.mDDD2);
		this.mDDD2 =  this.mDDD2.append(this.mDDD1);
		System.out.println(this.mDDD2);
		Assert.assertEquals(this.mDDD1, this.mDDD1.copy());
		DD<String, Integer> dDD = (DD) (this.mDDD2.append(this.mDDD1)).copy();
		System.out.println("Original:" + this.mDDD2.append(this.mDDD1));
		System.out.println("Copy: " + dDD);
		Assert.assertEquals((this.mDDD2.append(this.mDDD1)).getStates(), dDD.getStates(), 0.01D);
		Assert.assertEquals(this.mDDD2.append(this.mDDD1), dDD);
	}
}

