/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
 *******************************************************************************/

package x10dt.ui.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import x10dt.ui.editor.OutlineTests;
import x10dt.ui.editor.QuickOutlineTests;

/**
 * JUnit suite containing all the SWTBot-based tests for x10dt.ui
 * @author rfuhrer@watson.ibm.com
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ JavaBackEndSmokeTest.class, QuickOutlineTests.class, OutlineTests.class })
public final class SWTBotTestSuite {
}
