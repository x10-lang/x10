/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rick Lesniak (lesniakr@us.ibm.com) - initial API and implementation,
 *    									   adapted from JavaBackEndSmokeTest.java 
*******************************************************************************/

package x10dt.ui.tests;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.waits.WaitForView;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCTabItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IViewReference;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;


import x10dt.core.utils.Timeout;
import x10dt.tests.services.swbot.constants.LaunchConstants;
import x10dt.tests.services.swbot.constants.ViewConstants;
import x10dt.tests.services.swbot.constants.WizardConstants;
import x10dt.tests.services.swbot.utils.SWTBotUtils;
import x10dt.ui.tests.utils.EditorMatcher;

/**
 * @author lesniakr@us.ibm.com
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ImportX10ArchiveTest extends X10DTTestBase {

	private static final String CLASS_NAME = "QSort";  //$NON-NLS-1$

	private static final String SUBCLASS_NAME = "QSortable";  //$NON-NLS-1$

	private static final String CLASS_SRCFILE_NAME = CLASS_NAME + ".x10"; //$NON-NLS-1$

	private final static String LINE_MARKER = "public static def main("; //$NON-NLS-1$

	private static final String ARCHIVE_NAME = "/Users/lesniakr/x10dt/QSort.x10.tar.gz"; //$NON-NLS-1$	// specify file at top level of this workspace

	private static final String PROJECT_NAME = "ArchiveTest"; //$NON-NLS-1$	//will be created as a new empty project to accept the import

	public static final List<String> EXPECTED_OUTPUT = Arrays.asList(	"size of array: 1000",
																		"array is now sorted", 
																		"++++++ Test succeeded spectacularly!"
																	);

  /**
   * The bot for the editor created for the archive class.
   */
  protected SWTBotEclipseEditor fZipTestEditor;

  @BeforeClass
  public static void beforeClass() throws Exception {
    SWTBotPreferences.KEYBOARD_STRATEGY = "org.eclipse.swtbot.swt.finder.keyboard.SWTKeyboardStrategy"; //$NON-NLS-1$
    topLevelBot = new SWTWorkbenchBot();
    SWTBotUtils.closeWelcomeViewIfNeeded(topLevelBot);
    topLevelBot.perspectiveByLabel("X10").activate();
    SWTBotPreferences.TIMEOUT = Timeout.SIXTY_SECONDS;
  }
  @After
  public void after() throws Exception {
    SWTBotUtils.closeAllEditors(topLevelBot);
    SWTBotUtils.closeAllShells(topLevelBot);
  }

  @AfterClass
  public static void afterClass() throws Exception {
    SWTBotUtils.saveAllDirtyEditors(topLevelBot);
  }
  
  @Test
  public void basicImportZipTest() throws Exception {

	  Boolean createHello = false;
	  String launchName = PROJECT_NAME;

	  String operationMsg = null;			//string describing the current operation, for use in constructing error messages

	  try
	  {
		  // create a java project to import the archive into
		  operationMsg = "create Java Backend Project '" + PROJECT_NAME + "'";
		  createJavaBackEndProject(PROJECT_NAME, createHello);

		  //import the archive
		  operationMsg = "import Java Backend Project '" + PROJECT_NAME + "'";
		  importArchiveToJavaBackEndProject(ARCHIVE_NAME, PROJECT_NAME + "/src", true);
		  topLevelBot.waitUntil(Conditions.waitForEditor(new EditorMatcher("Hello.x10")));

		  //open the imported file in an editor view
		  operationMsg = "open project '" + PROJECT_NAME + "', file '" + CLASS_SRCFILE_NAME + "'";
		  openX10FileInEditor(PROJECT_NAME, CLASS_SRCFILE_NAME);

		  try
		  {
			  //wait for the editor to open
			  operationMsg = "open '" + CLASS_SRCFILE_NAME + "' editor view";
			  topLevelBot.waitUntil(Conditions.waitForEditor(new EditorMatcher(CLASS_SRCFILE_NAME)));

			  //make an editor widget in case we want to modify the source file later on
			  operationMsg = "create '" + CLASS_SRCFILE_NAME + "' editor widget";
			  fZipTestEditor = topLevelBot.editorByTitle(CLASS_SRCFILE_NAME).toTextEditor();
		  }
		  catch (Exception e)
		  {
			  throw new X10DT_Test_Exception(e.getMessage());  //turn it into an X10_Test_Exception so the exception structure works
		  }

		  //run the program
		  operationMsg = "run configuration '" + launchName + "' of class '" + CLASS_SRCFILE_NAME + "' in project '" + PROJECT_NAME + "'";
		  createAndRunJavaBackEndLaunchConfig(launchName, PROJECT_NAME, CLASS_NAME);

		  //Well, let's see if it worked
		  // verify that the actual output matches the expected output
		  operationMsg = "match expected output of class '" + CLASS_NAME + 
		  "' in project '" + PROJECT_NAME + "', using Run Configuration '" + launchName + "'";
		  boolean match = verifyConsoleOutput(EXPECTED_OUTPUT, 1); //$NON-NLS-1$
		  Assert.assertTrue("ImportArchiveTest: Console output does not match", match); //$NON-NLS-1$

		  //    	// now let's change the source code to produce different console output, and run it again
		  //    	operationMsg = "modify class '" + CLASS_SRCFILE_NAME + 
		  //						"' in project '" + PROJECT_NAME + "', to produce different expected output'";
		  //    	modifySrcText();
		  //        
		  //    	operationMsg = "re-run configuration '" + launchName + "' of class '" + CLASS_SRCFILE_NAME + "' in project '" + PROJECT_NAME + "'";
		  //    	launchRunModeLaunchConfig("1 " + launchName);
		  //    	
		  //    	operationMsg = "match modified expected output of class '" + CLASS_SRCFILE_NAME + 
		  //						"' in project '" + PROJECT_NAME + "', using Run Configuration '" + launchName + "'";
		  //    	match = checkConsoleOutput(EXPECTED_OUTPUT, 2); //$NON-NLS-1$ //$NON-NLS-2$
		  //        Assert.assertTrue("ImportArchiveTest: Modified console output does not match", match); //$NON-NLS-1$

		  //Let's make sure the type indexer is working
		  
		  //look for a specific source class
		  String typeName = SUBCLASS_NAME;
		  String searchString = typeName;
		  operationMsg = "find x10 type '" + typeName +  "', using search pattern '" + searchString + "'";
		  findX10TypeDef(typeName, searchString);

		  //look for the X10 'Array' class, using a substring search pattern
		  typeName = "Array";
		  searchString = typeName.substring(0, 2);
		  operationMsg = "find x10 type '" + typeName +  "', using search pattern '" + searchString + "'";
		  findX10TypeDef(typeName, searchString);

		  //look for a specific source class, using a substring search pattern
		  typeName = SUBCLASS_NAME;
		  searchString = typeName.substring(0, 2);
		  operationMsg = "find x10 type '" + typeName +  "', using search pattern '" + searchString + "'";
		  findX10TypeDef(typeName, searchString);
	  }
	  catch (X10DT_Test_Exception e)
	  {
		  Assert.fail("Failed to " + operationMsg + ". \n  Reason: " + e.getMessage()); //$NON-NLS-1$
	  }
	  catch (Exception e)
	  {
		  Assert.fail("An error of great mystery and ominous portent has occurred.\n  We must be cautious! For it is written:\n \"Behold! " + e.getMessage() + "\"");
	  }
  }

/*
 * Support routines
 */
  
  //	Launch application in Run mode
  //
  private void launchRunModeLaunchConfig(String launchMenuItemName) throws X10DT_Test_Exception {
	  try
	  {
		  topLevelBot.menu(LaunchConstants.RUN_MENU)
    					.menu(LaunchConstants.RUN_HISTORY_ITEM)
    						.menu(launchMenuItemName).click(); //$NON-NLS-1$
	  }
	  catch (Exception e)
	  {
		  throw new X10DT_Test_Exception("Failed to find the '" +
				  					LaunchConstants.RUN_HISTORY_ITEM  + "' item from menu '" +
				  						LaunchConstants.RUN_MENU +
				  							"'.\n        Reason: " + e.getMessage());
	  }
  }

  //	Verify console output from application
  //
  public static boolean verifyConsoleOutput(List<String> expectedLines, int startAtLine) throws X10DT_Test_Exception {

	  String operationMsg = null;			//string describing the current operation, for use in constructing error messages
	  boolean matches = true;				//verification result. Assume the best!
	  
	  // look for the Console view, and check the output
	  try {
		  operationMsg = "create a matcher for the '" + ViewConstants.CONSOLE_VIEW_NAME + "' view";
		  final Matcher<IViewReference> withPartName = WidgetMatcherFactory.withPartName(ViewConstants.CONSOLE_VIEW_NAME);
		  final WaitForView waitForConsole = Conditions.waitForView(withPartName);

		  topLevelBot.waitUntil(waitForConsole);

		  SWTBotView consoleView = new SWTBotView(waitForConsole.get(0), topLevelBot);
		  
		  //load the text of the actual output
		  operationMsg = "load the actual console output";
		  List<String> actualLines = consoleView.bot().styledText().getLines();
		  Iterator<String> actualLine = actualLines.listIterator(startAtLine);
		  Iterator<String> expectedLine = expectedLines.listIterator(0);
		  
		  //loop and compare actual and expected one line at a time
		  operationMsg = "match the actual console output with the expected console output";
		  int currentLine = startAtLine - 1;	// track for possible use in error message
		  String currentActualLine = "";		// track for possible use in error message
		  String currentExpectedLine = "";		// track for possible use in error message
		  while (matches && expectedLine.hasNext() && actualLine.hasNext()) {
			  currentLine++;
			  currentActualLine = actualLine.next();
			  currentExpectedLine = expectedLine.next();
			  matches = currentActualLine.equals(currentExpectedLine);
		  }
		  
		  if (!matches) //D'oh!
		  {
			  throw new X10DT_Test_Exception ("Mismatch at line " + currentLine + 
					  							"\n    actual: '" + currentActualLine + 
					  							"\n    expected: '" + currentExpectedLine + "'");
		  }
	  }
	  catch (X10DT_Test_Exception e)
	  {
		  Assert.fail("Failed to " + operationMsg + ". \n  Reason: " + e.getMessage()); //$NON-NLS-1$
	  }
	  return matches;
  }
}

////	Alter source code to change console output
////
//private void modifySrcText() {
//  List<String> lines = fZipTestEditor.getLines();
//  for (int i = 0; i < lines.size(); i++) {
//    if (lines.get(i).contains(LINE_MARKER)) {
//      fZipTestEditor.insertText(i + 1, 0, "    Console.OUT.println(\"Huh?\");\n"); //$NON-NLS-1$
//      break;
//    }
//  }
//  fZipTestEditor.save();
//}
