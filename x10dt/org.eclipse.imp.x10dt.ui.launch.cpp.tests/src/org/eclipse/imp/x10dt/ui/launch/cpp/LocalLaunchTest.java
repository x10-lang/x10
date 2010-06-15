/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp;

import org.eclipse.imp.x10dt.tests.services.swbot.conditions.X10DTConditions;
import org.eclipse.imp.x10dt.tests.services.swbot.constants.LaunchConstants;
import org.eclipse.imp.x10dt.tests.services.swbot.constants.ViewConstants;
import org.eclipse.imp.x10dt.tests.services.swbot.utils.ProjectUtils;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests a local launch of Hello World program with C++ back-end.
 * 
 * @author egeay
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public final class LocalLaunchTest {
  
  // --- Before and After
  
  @BeforeClass @SuppressWarnings("all") public static void beforeClass() throws Exception {
    bot = new SWTWorkbenchBot();
    bot.viewByTitle("Welcome").close();
  }
  
  @AfterClass @SuppressWarnings("all") public static void sleep() {
    bot.sleep(2000);
  }
  
  // --- Test cases
  
  /**
   * Tests compilation and run of an X10 program without any intermediate actions.
   */
  @Test public void compileAndRunHelloWorld() {
    final String projectName = "demo"; //$NON-NLS-1$
    ProjectUtils.createX10ProjectWithCppBackEnd(bot, projectName);
    
    bot.menu(LaunchConstants.RUN_MENU).menu(LaunchConstants.RUN_CONFS_MENU_ITEM).click();
    final SWTBotShell runShell = bot.shell(LaunchConstants.RUN_CONF_DIALOG_TITLE);
    runShell.activate();
    bot.tree().select(LaunchConstants.NEW_CPP_LAUNCH_CONFIG).contextMenu(LaunchConstants.NEW_CONF_CONTEXT_MENU).click();
    
    bot.cTabItem(LaunchConstants.CPP_LAUNCH_CONFIG_APPLICATION_TAB).activate();
    bot.textInGroup(LaunchConstants.CPP_LAUNCH_CONFIG_X10_PROJECT, 0).setText(projectName);
    bot.textInGroup(LaunchConstants.CPP_LAUNCH_CONFIG_MAIN_CLASS, 0).setText("Hello"); //$NON-NLS-1$
    
    runShell.bot().button(LaunchConstants.RUN_BUTTON).click();
    
    final SWTBotView consoleView = bot.viewByTitle(ViewConstants.CONSOLE_VIEW_NAME);
    bot.waitUntil(X10DTConditions.workbenchPartIsActive(consoleView), 20000);
    consoleView.bot().styledText().getText().contains("Hello X10 world"); //$NON-NLS-1$
  }
  
  // --- Fields
  
  private static SWTWorkbenchBot bot;

}
