/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.tests.services.swbot.utils;

import static org.eclipse.imp.x10dt.tests.services.swbot.constants.PlatformConfConstants.PLATFORM_CONF_FILE_PATH;
import static org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants.FILE_MENU;
import static org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants.FINISH_BUTTON;
import static org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants.NEW_CPP_PROJECT_NAME;
import static org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants.NEW_JAVA_PROJECT_NAME;
import static org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants.NEW_MENU_ITEM;
import static org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants.NEW_PROJECT_DIALOG_TITLE;
import static org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants.NEXT_BUTTON;
import static org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants.OPEN_ASSOCIATED_PERSPECTIVE_DIALOG_TITLE;
import static org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants.PROJECTS_SUB_MENU_ITEM;
import static org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants.X10_FOLDER;
import static org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants.X10_PROJECT_CPP_BACKEND;
import static org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants.X10_PROJECT_JAVA_BACKEND;
import static org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants.YES_BUTTON;

import org.eclipse.imp.x10dt.tests.services.swbot.conditions.X10DTConditions;
import org.eclipse.imp.x10dt.tests.services.swbot.constants.ViewConstants;
import org.eclipse.imp.x10dt.tests.services.swbot.matcher.WithFullPath;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

/**
 * Provides utility methods for X10 project creation.
 * 
 * @author egeay
 */
public final class ProjectUtils {
  
  /**
   * Creates an X10 project (C++ back-end) from context menu of Package Explorer view with the name provided.
   * 
   * @param bot The current SWTBot instance to test the workbench.
   * @param projectName The name to consider for the project.
   */
  public static void createX10ProjectWithCppBackEndFromContextMenu(final SWTWorkbenchBot bot, final String projectName) {
    PerspectiveUtils.switchToX10Perspective(bot);
    bot.viewByTitle(ViewConstants.PACKAGE_EXPLORER_VIEW_NAME).setFocus();
    bot.sleep(1000);
    SWTBotUtils.findSubMenu(bot.tree().contextMenu(ViewConstants.NEW_MENU), X10_PROJECT_CPP_BACKEND).click();
    
    bot.textWithLabel(NEW_CPP_PROJECT_NAME).setText(projectName);
    
    bot.button(FINISH_BUTTON).click();
    bot.waitUntil(X10DTConditions.matchResource(new WithFullPath(NLS.bind(PLATFORM_CONF_FILE_PATH, projectName))));
  }
  
  /**
   * Creates an X10 project (C++ back-end) from File > New top menu with the name provided.
   * 
   * @param bot The current SWTBot instance to test the workbench.
   * @param projectName The name to consider for the project.
   */
  public static void createX10ProjectWithCppBackEndFromTopMenu(final SWTWorkbenchBot bot, final String projectName) {
    bot.menu(FILE_MENU).menu(NEW_MENU_ITEM).menu(PROJECTS_SUB_MENU_ITEM).click();
    
    final SWTBotShell shell = bot.shell(NEW_PROJECT_DIALOG_TITLE);
    shell.activate();
    bot.tree().expandNode(X10_FOLDER).select(X10_PROJECT_CPP_BACKEND);
    bot.button(NEXT_BUTTON).click();
 
    bot.textWithLabel(NEW_CPP_PROJECT_NAME).setText(projectName);
 
    bot.button(FINISH_BUTTON).click();
    
    bot.waitUntil(Conditions.shellIsActive(OPEN_ASSOCIATED_PERSPECTIVE_DIALOG_TITLE));
    bot.button(YES_BUTTON).click();
    
    bot.waitUntil(X10DTConditions.matchResource(new WithFullPath(NLS.bind(PLATFORM_CONF_FILE_PATH, projectName))));
  }
  
  /**
   * Creates an X10 project (Java back-end) from File > New top menu with the name provided.
   * 
   * @param bot The current SWTBot instance to test the workbench.
   * @param projectName The name to consider for the project.
   */
  public static void createX10ProjectWithJavaBackEndFromTopMenu(final SWTWorkbenchBot bot, final String projectName) {
    bot.menu(FILE_MENU).menu(NEW_MENU_ITEM).menu(PROJECTS_SUB_MENU_ITEM).click();
    
    final SWTBotShell shell = bot.shell(NEW_PROJECT_DIALOG_TITLE);
    shell.activate();
    bot.tree().expandNode(X10_FOLDER).select(X10_PROJECT_JAVA_BACKEND);
    bot.button(NEXT_BUTTON).click();
 
    bot.textWithLabel(NEW_JAVA_PROJECT_NAME).setText(projectName);
 
    bot.button(FINISH_BUTTON).click();
  }
  
  // --- Private code
  
  private ProjectUtils() {}

}
