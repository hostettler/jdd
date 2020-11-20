package ch.unige.cui.smv.dd.ddd;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import ch.unige.cui.smv.dd.DD;
import ch.unige.cui.smv.dd.Hom;

public class Tutorial1 {
	@Test
	public void testDDD() {
		DD<String, Integer> dDD1 = DDDImpl.create("a", 1, DDDImpl.create("b", 2, DDDImpl.create("c", 3)));
		System.out.println(dDD1);
		DD<String, Integer> dDD2 = DDDImpl.create("a", 1);
		DD<String, Integer> dDD3 = DDDImpl.create("b", 2);
		DD<String, Integer> dDD4 = DDDImpl.create("c", 3);
		DD<String, Integer> dDD = dDD2.append(dDD3.append(dDD4));
		System.out.println(dDD);
		Assert.assertTrue((dDD.equals(dDD1) && dDD1.equals(dDD)));

		Hom<String, Integer> filter = new SimplePropagationDDDHomImpl<String, Integer>() {
			protected DD<String, Integer> phi(String var, Integer val,
					Map<Integer, DD<String, Integer>> alpha, Object... parameters) {
				
				String str = (String) parameters[0];
				Integer integer = (Integer) parameters[1];
				
				if (var.equals(str) && val.equals(integer)) {
					return (DD) DDDImpl.DDD_FALSE;	
				} else {
					return DDDImpl.create(var, val, phi(id(alpha, val), parameters));	
				}
				
			}
		};
		
		System.out.println(filter.phi(dDD1, new Object[] { "c", 3 }));
	}
}
