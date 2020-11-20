package net.hostettler.jdd.dd.sdd;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.ObjSet;
import net.hostettler.jdd.dd.ValSet;
import net.hostettler.jdd.dd.sdd.SDDImpl;

public class SDDTest {
	private DD<String, ValSet<Integer>> sdd1;
	private DD<String, ValSet<Integer>> sdd2;
	private ValSet<Integer> set1;

	@Before
	public void setup() {
		this.set1 = (ValSet<Integer>) ObjSet.create(Integer.valueOf(1));
		this.sdd1 = SDDImpl.create("a", this.set1);
		this.sdd2 = SDDImpl.create("b", this.set1);
	}

	@Test
	public void testCanonicity() {
		System.out.println("****** Canonicity ******");
		DD<String, ValSet<Integer>> sDD1 = SDDImpl.create("a",  ObjSet.create(Integer.valueOf(1)));
		DD<String, ValSet<Integer>> sDD2 = SDDImpl.create("b", ObjSet.create(Integer.valueOf(1)));
		Assert.assertSame(this.sdd1, sDD1);
		Assert.assertSame(this.sdd2, sDD2);
	}

	@Test
	public void testLeafes() {
		System.out.println("****** LEAFES ******");
		System.out.println(SDDImpl.SDD_TRUE);
		System.out.println(SDDImpl.SDD_FALSE);
		System.out.println(SDDImpl.SDD_ANY);
	}

	@Test
	public void testSimpleSDDs() {
		System.out.println("****** Build ******");
		System.out.println(this.sdd1);
		System.out.println(this.sdd2);
	}

	@Test
	public void testConcatSDDs() {
		System.out.println("****** Concat ******");
		System.out.println("Concat :" + this.sdd1.append(this.sdd2));
		Assert.assertEquals("a--{1}-->b--{1}-->ONE", ( this.sdd1.append(this.sdd2)).toString());
	}

	@Test
	public void testCopy() {
		System.out.println("****** Copy ******");
		Assert.assertEquals(SDDImpl.SDD_TRUE, SDDImpl.SDD_TRUE.copy());
		Assert.assertEquals(SDDImpl.SDD_FALSE, SDDImpl.SDD_FALSE.copy());
		Assert.assertEquals(SDDImpl.SDD_ANY, SDDImpl.SDD_ANY.copy());
		System.out.println("Original:" + this.sdd1);
		System.out.println("Copy: " + this.sdd1.copy());
		Assert.assertEquals(this.sdd1.toString(), this.sdd1.copy().toString());
		this.sdd2 = (DD<String, ValSet<Integer>>) this.sdd2.append(this.sdd1);
		System.out.println(this.sdd2);
		this.sdd2 = (DD<String, ValSet<Integer>>) this.sdd2.append(this.sdd1);
		System.out.println(this.sdd2);
		this.sdd2 = (DD<String, ValSet<Integer>>) this.sdd2.append(this.sdd1);
		System.out.println(this.sdd2);
		Assert.assertEquals(this.sdd1, this.sdd1.copy());
		System.out.println("Original:" + this.sdd2.append(this.sdd1));
		System.out.println("Copy: " + (this.sdd2.append(this.sdd1)).copy());
		Assert.assertEquals(this.sdd2.append(this.sdd1), (this.sdd2.append(this.sdd1)).copy());
	}

	@Test
	public void testIgnoreDD1() {
		System.out.println("****** IgnoreDD ******");
		DD<String, ValSet<Integer>>  sDD1 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(99)));
		sDD1.setIgnoreThisDD(true);
		DD<String, ValSet<Integer>>  sDD2 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(99)), (DD) sDD1);
		sDD2.setIgnoreThisDD(true);
		Assert.assertEquals(Boolean.valueOf(true), Boolean.valueOf(sDD2.ignoreDD()));
	}

	@Test
	public void testIgnoreDD2() {
		System.out.println("****** IgnoreDD ******");
		DD<String, ValSet<Integer>>  sDD1 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(54)));
		sDD1.setIgnoreThisDD(true);
		DD<String, ValSet<Integer>>   sDD2 = SDDImpl.create("a", ObjSet.create(Integer.valueOf(47)));
		sDD2 = sDD2.append((DD)sDD1);
		sDD2.setIgnoreThisDD(true);
		Assert.assertEquals(Boolean.valueOf(true), Boolean.valueOf(sDD2.ignoreDD()));
	}
}
