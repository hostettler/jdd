package ch.unige.cui.smv.dd.ddd;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DDDTest.class, Tutorial1.class, Tutorial2.class, Tutorial3.class, TutorialPhilo.class,
		DDDSetOperationTest.class, DDDHomTest.class, DDDHanoiTest.class, DDDSequenceCollectorTest.class,
		DDDDiningPhilosophersTest.class })
public class DDDTestSuite {
}

/*
 * Location:
 * C:\Users\steve.hostettler\Downloads\alpina\eclipse_alpina\configuration\org.
 * eclipse.osgi\bundles\7\1\.cp\lib\jdd_1.1.2.jar!\c\\unige\cui\smv\dd\ddd\
 * DDDTestSuite.class Java compiler version: 6 (50.0) JD-Core Version: 1.1.3
 */