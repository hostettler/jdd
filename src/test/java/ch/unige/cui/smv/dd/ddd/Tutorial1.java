package ch.unige.cui.smv.dd.ddd;

import org.junit.Assert;
import org.junit.Test;

import ch.unige.cui.smv.dd.DD;

public class Tutorial1 {
	@Test
	public void testDDD() {
		DD<String, Integer> dDD1 = DDDImpl.create("a", Integer.valueOf(1),
				DDDImpl.create("b", Integer.valueOf(2), DDDImpl.create("c", Integer.valueOf(3))));
		System.out.println(dDD1);
		DD<String, Integer> dDD2 = DDDImpl.create("a", Integer.valueOf(1));
		DD<String, Integer> dDD3 = DDDImpl.create("b", Integer.valueOf(2));
		DD<String, Integer> dDD4 = DDDImpl.create("c", Integer.valueOf(3));
		DD<String, Integer> dDD =  dDD2.append(dDD3.append(dDD4));
		System.out.println(dDD);
		Assert.assertTrue((dDD.equals(dDD1) && dDD1.equals(dDD)));
	}
}

