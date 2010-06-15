/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp;

import static org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants.NEW_CPP_PROJECT_NAME;
import static org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants.X10_PROJECT_CPP_BACKEND;
import junit.framework.Assert;

import org.eclipse.imp.x10dt.tests.services.swbot.conditions.X10DTConditions;
import org.eclipse.imp.x10dt.tests.services.swbot.constants.LaunchConstants;
import org.eclipse.imp.x10dt.tests.services.swbot.constants.ViewConstants;
import org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants;
import org.eclipse.imp.x10dt.tests.services.swbot.utils.PerspectiveUtils;
import org.eclipse.imp.x10dt.tests.services.swbot.utils.ProjectUtils;
import org.eclipse.imp.x10dt.tests.services.swbot.utils.SWTBotUtils;
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
  @Test public void shouldCompileAndRunHelloWorld() {
    final String projectName = "demo"; //$NON-NLS-1$
    ProjectUtils.createX10ProjectWithCppBackEndFromTopMenu(bot, projectName);
    
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
  
  /**
   * Tests creation of new project with the same name than the previous one. This should disable the next button.
   * Then, we cancel it and test that the project is still here in the Package Explorer.
   */
  @Test public void shouldNotCreateProjectWithExistingName() {
    final String projectName = "demo"; //$NON-NLS-1$    
    PerspectiveUtils.switchToX10Perspective(bot);
    bot.viewByTitle(ViewConstants.PACKAGE_EXPLORER_VIEW_NAME).setFocus();
    bot.sleep(1000);
    SWTBotUtils.findSubMenu(bot.tree().contextMenu(ViewConstants.NEW_MENU), X10_PROJECT_CPP_BACKEND).click();
    
    bot.textWithLabel(NEW_CPP_PROJECT_NAME).setText(projectName);
    
    Assert.assertFalse(bot.button(WizardConstants.NEXT_BUTTON).isEnabled());
    bot.button(WizardConstants.CANCEL_BUTTON).click();
    
    bot.viewByTitle(ViewConstants.PACKAGE_EXPLORER_VIEW_NAME).setFocus();
    Assert.assertTrue(bot.tree().hasItems());
  }
  
  // --- Fields
  
  private static SWTWorkbenchBot bot;

}
