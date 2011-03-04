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

package x10dt.ui.editor;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.utils.Position;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IEditorPart;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import x10dt.tests.services.swbot.constants.WizardConstants;
import x10dt.tests.services.swbot.utils.ProjectUtils;
import x10dt.ui.tests.utils.EditorMatcher;

/**
 * @author rfuhrer@watson.ibm.com
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class QuickOutlineTests extends X10DTEditorTestBase {
  private static int testID = 0;

  private static final String PROJECT_NAME = "TestOutline";

  private static final String CLASS_NAME = "Hello1";

  private static final String SRC_FILE_NAME = CLASS_NAME + ".x10";

  private static final String MAIN_SIGNATURE= "main(Array[String])";

  private static final String MAIN_SIGNATURE_ELABORATED= "main(x10.array.Array[x10.lang.String])";

  @BeforeClass
  public static void beforeClass() throws Exception {
    X10DTEditorTestBase.BeforeClass();
    //createJavaBackEndProject(PROJECT_NAME, false);
    topLevelBot.shells()[0].activate();
  }

  @After
  public void after() throws Exception {
      afterTest();
  }

  @AfterClass
  public static void afterClass() throws Exception {
      X10DTEditorTestBase.AfterClass();
  }

  //@Test
  public void test() throws Exception {
	  for (int i = 0; i < 50; i++){
		  test1();
		  after();
	  }
  }
  
  @Test
  public void test1() throws Exception {
	String projName= PROJECT_NAME + testID++;

	createJavaBackEndProject(projName, false);  
    ProjectUtils.createClass(topLevelBot, CLASS_NAME, projName + "/src", false);
    topLevelBot.waitUntil(Conditions.waitForEditor(new EditorMatcher(SRC_FILE_NAME)));

    fSrcEditor = topLevelBot.activeEditor().toTextEditor();
    junit.framework.Assert.assertEquals(SRC_FILE_NAME, fSrcEditor.getTitle());

    final IEditorPart editorPart = fSrcEditor.getReference().getEditor(false);
    final UniversalEditor univEditor = (UniversalEditor) editorPart;

    univEditor.addModelListener(fUpdateListener);

    part1();
    for(int i=0; i < 25; i++) {
        part2(i);
    }
  }

  private void part1() throws Exception {
    insertMethodsAndNavigate(MAIN_SIGNATURE, MAIN_SIGNATURE_ELABORATED, "foo()", "foo()", 1);
  }

  private void part2(int idx) throws Exception {
    String method1Signature = "bar" + idx + "()";
    String method2Signature = "bletch" + idx + "()";
    int insertLine = 3 + 2 * idx;

    insertMethodsAndNavigate(method1Signature, method1Signature, method2Signature, method2Signature, insertLine);
  }

  private void insertMethodsAndNavigate(String method1Signature, String method1ElaboratedSignature,
                                        String method2Signature, String method2ElaboratedSignature, int insertLine) throws Exception {
      fUpdateListener.reset();

      fSrcEditor.setFocus();
      fSrcEditor.insertText(insertLine,   0, "public static def " + method1Signature + " { }\n");
      fSrcEditor.insertText(insertLine+1, 0, "public static def " + method2Signature + " { }\n");

      waitForParser();
      fSrcEditor.navigateTo(0, 0); // Go to the beginning so that quick-outline navigation has to move the cursor

      try {
          SWTBot outlineBot = invokeQuickOutline(topLevelBot);
          SWTBotTreeItem classItem = outlineBot.tree().getTreeItem(CLASS_NAME);
          SWTBotTreeItem meth2Item = classItem.getNode(method2ElaboratedSignature);

          classItem.getNode(method1ElaboratedSignature);
          meth2Item.doubleClick();
      } catch (WidgetNotFoundException e) {
          SWTBot outlineBot = invokeQuickOutline(topLevelBot); // OK to repeat because this resets the focus to editor.
          SWTBotTreeItem classItem = outlineBot.tree().getTreeItem(CLASS_NAME);
          SWTBotTreeItem meth2Item = classItem.getNode(method2ElaboratedSignature);

          classItem.getNode(method1ElaboratedSignature);
          meth2Item.doubleClick();
      }

      Position cursorPos = fSrcEditor.cursorPosition();

      junit.framework.Assert.assertEquals("Cursor positioned at incorrect line after quick outline item selection",
                                          insertLine + 1, cursorPos.line);
      junit.framework.Assert.assertEquals("Cursor positioned at incorrect column after quick outline item selection",
                                          1, cursorPos.column);
  }

  private SWTBot invokeQuickOutline(SWTBot bot) throws Exception {
	fSrcEditor.setFocus(); // Needed to make this method repeatable.
	submitQuickOutlineShortcut();

	SWTBotShell outlineShell = null;

	try {
		outlineShell = topLevelBot.shell(WizardConstants.QUICK_OUTLINE_SHELL); // TODO Fix this
	} catch (WidgetNotFoundException e) {
    	submitQuickOutlineShortcut();
    	outlineShell = topLevelBot.shell(WizardConstants.QUICK_OUTLINE_SHELL);
    }
    outlineShell.activate();

    return outlineShell.bot();
  }

  private void submitQuickOutlineShortcut() throws ParseException {
    if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) {
		fSrcEditor.pressShortcut(Keystrokes.COMMAND, KeyStroke.getInstance("o"));
	} else {
		fSrcEditor.pressShortcut(Keystrokes.CTRL, KeyStroke.getInstance("o"));
	}
  }
}
