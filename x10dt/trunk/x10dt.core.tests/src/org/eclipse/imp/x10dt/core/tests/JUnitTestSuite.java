/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.core.tests;

import org.eclipse.imp.x10dt.core.tests.compiler.CompilerTests;
import org.eclipse.imp.x10dt.core.tests.compiler.CompletenessCompilerTests;
import org.eclipse.imp.x10dt.core.tests.compiler.DataParamCompilerTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * All command-line JUnit test cases for this plugin.
 * 
 * @author egeay
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ CompilerTests.class, CompletenessCompilerTests.class, DataParamCompilerTests.class })
public final class JUnitTestSuite {
}
