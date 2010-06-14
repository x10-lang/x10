/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.tests.services.swbot.utils;

import org.eclipse.imp.x10dt.tests.services.swbot.conditions.X10DTConditions;
import org.eclipse.imp.x10dt.tests.services.swbot.matcher.WithFullPath;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

/**
 * 
 * @author egeay
 */
public final class ProjectUtils {
  
  /**
   * Creates an X10 project (C++ back-end) with the name provided.
   * 
   * @param bot The current SWTBot instance to test the workbench.
   * @param projectName The name to consider for the project.
   */
  @SuppressWarnings("nls")
  public static void createX10ProjectWithCppBackEnd(final SWTWorkbenchBot bot, final String projectName) {
    bot.menu("File").menu("New").menu("Project...").click();
    
    final SWTBotShell shell = bot.shell("New Project");
    shell.activate();
    bot.tree().expandNode("X10").select("X10 Project (C++ back-end)");
    bot.button("Next >").click();
 
    bot.textWithLabel("Project name:").setText(projectName);
 
    bot.button("Finish").click();
    
    bot.waitUntil(Conditions.shellIsActive("Open Associated Perspective?"));
    bot.button("Yes").click();
    
    bot.waitUntil(X10DTConditions.matchResource(new WithFullPath(NLS.bind("/{0}/x10_platform.conf", projectName))));
  }
  
  /**
   * Creates an X10 project (Java back-end) with the name provided.
   * 
   * @param bot The current SWTBot instance to test the workbench.
   * @param projectName The name to consider for the project.
   */
  @SuppressWarnings("nls")
  public static void createX10ProjectWithJavaBackEnd(final SWTWorkbenchBot bot, final String projectName) {
    bot.menu("File").menu("New").menu("Project...").click();
    
    final SWTBotShell shell = bot.shell("New Project");
    shell.activate();
    bot.tree().expandNode("X10").select("X10 Project (Java back-end)");
    bot.button("Next >").click();
 
    bot.textWithLabel("Name:").setText(projectName);
 
    bot.button("Finish").click();
  }
  
  // --- Private code
  
  private ProjectUtils() {}

}
