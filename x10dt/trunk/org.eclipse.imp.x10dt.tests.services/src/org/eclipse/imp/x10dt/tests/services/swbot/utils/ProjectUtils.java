/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.tests.services.swbot.utils;

import static org.eclipse.imp.x10dt.tests.services.swbot.constants.PlatformConfConstants.*;
import static org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants.*;

import org.eclipse.imp.x10dt.tests.services.swbot.conditions.X10DTConditions;
import org.eclipse.imp.x10dt.tests.services.swbot.constants.ViewConstants;
import org.eclipse.imp.x10dt.tests.services.swbot.constants.WizardConstants;
import org.eclipse.imp.x10dt.tests.services.swbot.matcher.WithFullPath;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

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

  public static void createPackage(final SWTWorkbenchBot bot, String projName, String srcFolderName, String pkgName) {
      // look for the new project in the Package Explorer
      SWTBotView pkgView= bot.viewByTitle(ViewConstants.PACKAGE_EXPLORER_VIEW_NAME);
      SWTBotTree pkgTree= pkgView.bot().tree();
      SWTBotTreeItem projItem= pkgTree.getTreeItem(projName);

      // expose the source folder in the new project
      projItem.expand();
      SWTBotTreeItem srcItem= projItem.getNode(srcFolderName);
      srcItem.select();

      // create the new package in the source folder
      bot.menu(WizardConstants.FILE_MENU).menu(WizardConstants.NEW_MENU_ITEM).menu(WizardConstants.NEW_X10_PACKAGE_MENU_ITEM).click();
      SWTBotShell newPkgShell= bot.shell(WizardConstants.NEW_X10_PACKAGE_SHELL);
      newPkgShell.activate();
      bot.textWithLabel(WizardConstants.NEW_X10_PACKAGE_NAME_FIELD).setText(pkgName);
      bot.button(WizardConstants.FINISH_BUTTON).click();

      bot.waitUntil(Conditions.shellCloses(newPkgShell));
      srcItem.expand();
      SWTBotTreeItem newPkgItem= srcItem.getNode(pkgName);
      newPkgItem.select();
  }

  public static SWTBotShell createClass(SWTWorkbenchBot bot, String name) {
    // Shouldn't the following use the X10 folder/category to qualify the "Class" menu item???
    bot.menu(WizardConstants.FILE_MENU).menu(WizardConstants.NEW_MENU_ITEM).menu(WizardConstants.NEW_X10_CLASS_MENU_ITEM).click();

    SWTBotShell newClassShell= bot.shell(WizardConstants.NEW_X10_CLASS_SHELL);

    newClassShell.activate();
    bot.textWithLabel(WizardConstants.NEW_X10_CLASS_NAME_FIELD).setText(name);
//  bot.checkBoxWithLabel("Create sample program").deselect();
    bot.button(WizardConstants.FINISH_BUTTON).click();
    return newClassShell;
  }
  
  // --- Private code
  
  private ProjectUtils() {}

}
