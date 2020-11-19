package ch.unige.cui.smv.dd.ddd;

import java.io.IOException;

import org.junit.Test;

import ch.unige.cui.smv.dd.DD;

public class Tutorial2 {
	@Test
	public void testDDDUnion() throws IOException {
		DD<String, Integer> dDD1 = DDDImpl.create("a", Integer.valueOf(1),
				DDDImpl.create("b", Integer.valueOf(2), DDDImpl.create("c", Integer.valueOf(3))));
		DD<String, Integer> dDD2 = DDDImpl.create("a", Integer.valueOf(1),
				DDDImpl.create("b", Integer.valueOf(2), DDDImpl.create("c", Integer.valueOf(5))));
		(dDD1.union(dDD2)).printInLibDDDStyle(System.out);
	}
}
