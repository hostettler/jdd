package net.hostettler.jdd.dd.ddd;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.Hom;
import net.hostettler.jdd.dd.ddd.DDDImpl;
import net.hostettler.jdd.dd.ddd.SimplePropagationDDDHomImpl;

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
					return getFalse();	
				} else {
					return DDDImpl.create(var, val, phi(id(alpha, val), parameters));	
				}
				
			}
		};
		
		DD<String, Integer> dDD7 = DDDImpl.create("a", 1, DDDImpl.create("b", 2, DDDImpl.create("c", 4)));
		
		System.out.println(filter.phi(dDD1.union(dDD7), new Object[] { "c", 3 }));
	}

}
