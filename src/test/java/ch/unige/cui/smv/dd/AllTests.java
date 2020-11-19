package ch.unige.cui.smv.dd;

import ch.unige.cui.smv.dd.ddd.DDDTestSuite;
import ch.unige.cui.smv.dd.sdd.SDDTestSuite;
import ch.unige.cui.smv.dd.util.UtilTestSuite;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestResult;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ SDDTestSuite.class,  DDDTestSuite.class,
		UtilTestSuite.class })
public class AllTests {
	public static void main(String[] paramArrayOfString) {
		TestResult testResult = new TestResult();
		JUnit4TestAdapter jUnit4TestAdapter = new JUnit4TestAdapter(SDDTestSuite.class);
		jUnit4TestAdapter.run(testResult);
		jUnit4TestAdapter = new JUnit4TestAdapter(DDDTestSuite.class);
		jUnit4TestAdapter.run(testResult);
		jUnit4TestAdapter = new JUnit4TestAdapter(UtilTestSuite.class);
		jUnit4TestAdapter.run(testResult);
	}
}
