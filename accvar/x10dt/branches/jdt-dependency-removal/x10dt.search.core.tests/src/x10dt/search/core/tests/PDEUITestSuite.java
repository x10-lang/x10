/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import x10dt.search.core.engine.HelloWholeWorldTests;
import x10dt.search.core.engine.MethodAndFieldTests;
import x10dt.search.core.engine.SearchScopeTests;
import x10dt.search.core.engine.TypeHierarchyTests;

/**
 * Test suite for Plugin test cases.
 * 
 * @author egeay
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ HelloWholeWorldTests.class, MethodAndFieldTests.class, SearchScopeTests.class, 
                      TypeHierarchyTests.class })
public final class PDEUITestSuite {
}
