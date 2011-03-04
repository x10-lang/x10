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

import java.util.List;

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

import x10dt.core.utils.Timeout;
import x10dt.tests.services.swbot.constants.LaunchConstants;
import x10dt.tests.services.swbot.constants.ViewConstants;
import x10dt.tests.services.swbot.utils.SWTBotUtils;
import x10dt.ui.tests.utils.EditorMatcher;

/**
 * @author rfuhrer@watson.ibm.com
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class JavaBackEndSmokeTest extends X10DTTestBase {
  public static final String JAVA_BACK_END_PROJECT_DIALOG_NAME_FIELD = "Name:";

  private static final String CLASS_NAME = "Hello"; //$NON-NLS-1$

  private static final String CLASS_SRCFILE_NAME = CLASS_NAME + ".x10"; //$NON-NLS-1$

  private static final String PROJECT_NAME = "TestProject"; //$NON-NLS-1$

  private final static String LINE_MARKER = "public static def main("; //$NON-NLS-1$

  /**
   * The bot for the editor created for the "Hello, World" sample class.
   */
  protected SWTBotEclipseEditor fSrcEditor;

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
  public void basicLaunchTest() throws Exception {
    createJavaBackEndProject(PROJECT_NAME, true);

    topLevelBot.waitUntil(Conditions.waitForEditor(new EditorMatcher(CLASS_SRCFILE_NAME)));

    fSrcEditor = topLevelBot.editorByTitle(CLASS_SRCFILE_NAME).toTextEditor();

    String launchName = PROJECT_NAME;

    createAndRunJavaBackEndLaunchConfig(launchName, PROJECT_NAME, CLASS_NAME);

    checkConsoleOutput("Hello, world!"); //$NON-NLS-1$

    modifySrcText();
    launchRunModeLaunchConfig("1 " + launchName);
    checkConsoleOutput("Huh?\n" + "Hello, world!"); //$NON-NLS-1$ //$NON-NLS-2$
  }

  private void modifySrcText() {
    List<String> lines = fSrcEditor.getLines();
    for (int i = 0; i < lines.size(); i++) {
      if (lines.get(i).contains(LINE_MARKER)) {
        fSrcEditor.insertText(i + 1, 0, "    Console.OUT.println(\"Huh?\");\n"); //$NON-NLS-1$
        break;
      }
    }
    fSrcEditor.save();
  }

  private void launchRunModeLaunchConfig(String launchMenuItemName) {
    SWTBotMenu runMenu = topLevelBot.menu(LaunchConstants.RUN_MENU);
    SWTBotMenu helloLaunch = runMenu.menu(launchMenuItemName); //$NON-NLS-1$

    helloLaunch.click();
  }

  public static void createAndRunJavaBackEndLaunchConfig(String launchName, String projectName, String mainTypeName) {
    SWTBotMenu runMenu = topLevelBot.menu(LaunchConstants.RUN_MENU);
    SWTBotMenu runConfigs = runMenu.menu(LaunchConstants.RUN_CONFS_MENU_ITEM);

    runConfigs.click();
    topLevelBot.waitUntil(Conditions.shellIsActive(LaunchConstants.RUN_CONF_DIALOG_TITLE));

    SWTBotShell configsShell = topLevelBot.shell(LaunchConstants.RUN_CONF_DIALOG_TITLE);

    configsShell.activate();
    SWTBot configsBot = configsShell.bot();

    SWTBotTreeItem x10AppItem = configsBot.tree().getTreeItem(LaunchConstants.JAVA_BACK_END_LAUNCH_CONFIG_TYPE);
    x10AppItem.doubleClick();

    SWTBotText launchNameText = configsBot.textWithLabel(JAVA_BACK_END_PROJECT_DIALOG_NAME_FIELD);
    launchNameText.setText(launchName);

    SWTBotCTabItem mainTab = configsBot.cTabItem(LaunchConstants.JAVA_BACK_END_LAUNCH_CONFIG_MAIN_TAB);

    mainTab.activate();
    configsBot.textInGroup(LaunchConstants.JAVA_BACK_END_LAUNCH_CONFIG_PROJECT, 0).setText(projectName);
    configsBot.textInGroup(LaunchConstants.JAVA_BACK_END_LAUNCH_CONFIG_MAIN_CLASS, 0).setText(mainTypeName);

    configsBot.button(LaunchConstants.RUN_BUTTON).click();
  }

  public static void checkConsoleOutput(String contents) {
    // look for the Console view, and check the output
    final Matcher<IViewReference> withPartName = WidgetMatcherFactory.withPartName(ViewConstants.CONSOLE_VIEW_NAME);
    final WaitForView waitForConsole = Conditions.waitForView(withPartName);

    topLevelBot.waitUntil(waitForConsole);

    SWTBotView consoleView = new SWTBotView(waitForConsole.get(0), topLevelBot);

    consoleView.bot().styledText().getText().equals(contents);
  }
}
