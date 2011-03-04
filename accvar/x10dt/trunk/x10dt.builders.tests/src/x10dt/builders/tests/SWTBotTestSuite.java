package x10dt.builders.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * JUnit suite containing all the SWTBot-based tests for the builders
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ MiscTests.class, DependencyTests.class })
public final class SWTBotTestSuite {
}
