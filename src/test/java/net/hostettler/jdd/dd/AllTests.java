package net.hostettler.jdd.dd;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestResult;
import net.hostettler.jdd.dd.ddd.DDDTestSuite;
import net.hostettler.jdd.dd.sdd.SDDTestSuite;
import net.hostettler.jdd.dd.util.UtilTestSuite;

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
