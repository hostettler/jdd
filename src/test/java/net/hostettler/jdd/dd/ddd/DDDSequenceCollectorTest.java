package net.hostettler.jdd.dd.ddd;

import java.util.Map;

import org.junit.Test;

import junit.framework.Assert;
import net.hostettler.jdd.dd.DD;
import net.hostettler.jdd.dd.ddd.DDDImpl;
import net.hostettler.jdd.dd.ddd.DDDSequenceCollector;
import net.hostettler.jdd.dd.ddd.Evaluator;

public class DDDSequenceCollectorTest {
	@Test
	public void testSequenceCollector() {
		DD<String, Integer> dDD1 = DDDImpl.create("A", Integer.valueOf(1), DDDImpl.create("B", Integer.valueOf(2)));
		dDD1 = (DD<String, Integer>) dDD1
				.union(DDDImpl.create("A", Integer.valueOf(2), DDDImpl.create("B", Integer.valueOf(2))));
		dDD1 = (DD<String, Integer>) dDD1.union(DDDImpl.create("A", Integer.valueOf(3),
				DDDImpl.create("B", Integer.valueOf(2), DDDImpl.create("C", Integer.valueOf(2)))));
		Evaluator<String, Integer> evaluator = new Evaluator<String, Integer>() {
			public DD<String, Integer> evaluate(Map<String, Integer> param1Map) {
				System.out.print("--> ");
				int i = 0;
				for (Map.Entry<String, Integer> entry : param1Map.entrySet()) {
					System.out.print((String) entry.getKey() + "::" + entry.getValue() + " ");
					i += ((Integer) entry.getValue()).intValue();
				}
				System.out.println("");
				return DDDImpl.create("S", Integer.valueOf(i));
			}

			public String toString() {
				return "SumEvaluator";
			}
		};
		DDDSequenceCollector<String, Integer> dDDSequenceCollector = new DDDSequenceCollector<String, Integer>(
				evaluator);
		System.out.println(dDD1);
		System.out.println("Collector : " + dDDSequenceCollector);
		Object object = dDDSequenceCollector.phi(dDD1, new Object[0]);
		System.out.println(object);
		DD<String, Integer> dDD2 = DDDImpl.create("A", Integer.valueOf(1),
				DDDImpl.create("B", Integer.valueOf(2), DDDImpl.create("S", Integer.valueOf(3))));
		dDD2 = (DD<String, Integer>) dDD2.union(DDDImpl.create("A", Integer.valueOf(2),
				DDDImpl.create("B", Integer.valueOf(2), DDDImpl.create("S", Integer.valueOf(4)))));
		dDD2 = (DD<String, Integer>) dDD2.union(DDDImpl.create("A", Integer.valueOf(3), DDDImpl.create("B",
				Integer.valueOf(2), DDDImpl.create("C", Integer.valueOf(2), DDDImpl.create("S", Integer.valueOf(7))))));
		Assert.assertSame(dDD2, object);
	}
}

