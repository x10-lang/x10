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

import x10dt.ui.editor.ContentAssistTests;
import x10dt.ui.editor.OutlineTests;
import x10dt.ui.editor.QuickOutlineTests;
import x10dt.ui.editor.SyntaxColoringTests;

/**
 * JUnit suite containing all the SWTBot-based tests for x10dt.ui
 * 
 * @author rfuhrer@watson.ibm.com
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ QuickOutlineTests.class, JavaBackEndSmokeTest.class, OutlineTests.class,
                      SyntaxColoringTests.class, ContentAssistTests.class, RTC753Test.class })
public final class SWTBotTestSuite {
}
