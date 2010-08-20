package org.eclipse.imp.x10dt.ui.tests;

import org.eclipse.imp.x10dt.ui.editor.X10DocProviderTest;
import org.eclipse.imp.x10dt.ui.editor.X10DocProviderTest_Parameterized;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ IndentationTests.class, X10DocProviderTest.class, X10DocProviderTest_Parameterized.class })
public class PDEUITestSuite {
  // The class remains completely empty, being used only as a holder for the above annotations.
}