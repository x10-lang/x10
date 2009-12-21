package com.ibm.wala.cast.x10.tests;

import org.junit.runner.RunWith;

import com.ibm.wala.cast.x10.tests.X10TestRunner.TestRootPath;
import com.ibm.wala.cast.x10.tests.X10TestRunner.X10Tests;

/**
 * Test cases for <b>com.ibm.wala.cast.x10</b> project.
 * 
 * @author egeay
 */
@RunWith(value=X10TestRunner.class)
@TestRootPath(path="../x10.tests/examples/")
public final class AllX10ConstructsTests extends X10CallGraphBuildingTestCaseBaseClass {
  
  // --- X10 File Tests

  @X10Tests(recursive=true, exclude_filter=".*_MustFailCompile.x10$,.*/dims/.*.x10$") public void Constructs() {}
  
}
