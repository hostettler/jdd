package ch.unige.cui.smv.dd.util;

import org.junit.Assert;
import org.junit.Test;

public class PairTest {
	@Test
	public void testPair2String() {
		Pair<String, Integer> pair = new Pair<String, Integer>("a", Integer.valueOf(1));
		Assert.assertEquals("Pair[a,1]", pair.toString());
	}

	@Test
	public void testPairEquals() {
		Pair<String, Integer> pair1 = new Pair<String, Integer>("a", Integer.valueOf(1));
		Pair<String, Integer> pair2 = new Pair<String, Integer>("a", Integer.valueOf(1));
		Pair<String, Integer> pair3 = new Pair<String, Integer>("b", Integer.valueOf(1));
		Pair<String, Integer> pair4 = new Pair<String, Integer>("a", Integer.valueOf(2));
		Pair<String, Integer> pair5 = new Pair<String, Integer>("b", Integer.valueOf(2));
		Assert.assertEquals(pair1, pair2);
		Assert.assertFalse(pair1.equals(pair3));
		Assert.assertFalse(pair3.equals(pair1));
		Assert.assertFalse(pair1.equals(pair4));
		Assert.assertFalse(pair4.equals(pair1));
		Assert.assertFalse(pair1.equals(pair5));
		Assert.assertFalse(pair5.equals(pair1));
	}

	@Test
	public void testPairHashCode() {
		Pair<String, Integer> pair1 = new Pair<String, Integer>("a", Integer.valueOf(1));
		Pair<String, Integer> pair2 = new Pair<String, Integer>("a", Integer.valueOf(1));
		Pair<String, Integer> pair3 = new Pair<String, Integer>("b", Integer.valueOf(1));
		Pair<String, Integer> pair4 = new Pair<String, Integer>("a", Integer.valueOf(2));
		Pair<String, Integer> pair5 = new Pair<String, Integer>("b", Integer.valueOf(2));
		Assert.assertEquals(pair1.hashCode(), pair2.hashCode());
		Assert.assertFalse((pair1.hashCode() == pair3.hashCode()));
		Assert.assertFalse((pair3.hashCode() == pair1.hashCode()));
		Assert.assertFalse((pair1.hashCode() == pair4.hashCode()));
		Assert.assertFalse((pair4.hashCode() == pair1.hashCode()));
		Assert.assertFalse((pair1.hashCode() == pair5.hashCode()));
		Assert.assertFalse((pair5.hashCode() == pair1.hashCode()));
	}

	@Test
	public void testPairOf() {
		Pair<String, Integer> pair = new Pair<String, Integer>("a", Integer.valueOf(1));
		Assert.assertEquals(pair, Pair.of("a", Integer.valueOf(1)));
	}
}

/*
 * Location:
 * C:\Users\steve.hostettler\Downloads\alpina\eclipse_alpina\configuration\org.
 * eclipse.osgi\bundles\7\1\.cp\lib\jdd_1.1.2.jar!\c\\unige\cui\smv\d\\util\
 * PairTest.class Java compiler version: 6 (50.0) JD-Core Version: 1.1.3
 */