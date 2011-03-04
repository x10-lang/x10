/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import x10dt.core.tests.compiler.CompilerTests;
import x10dt.core.tests.compiler.CompletenessCompilerTests;
import x10dt.core.tests.compiler.DataParamCompilerTests;
import x10dt.core.tests.compiler.X10TestsParamCompilerTests;

/**
 * All command-line JUnit test cases for this plugin.
 * 
 * @author egeay
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ CompilerTests.class, CompletenessCompilerTests.class, DataParamCompilerTests.class, X10TestsParamCompilerTests.class })
public final class JUnitTestSuite {
}
