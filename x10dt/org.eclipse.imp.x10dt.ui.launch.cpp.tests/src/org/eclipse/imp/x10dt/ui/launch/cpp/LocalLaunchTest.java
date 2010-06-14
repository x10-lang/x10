/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp;

import org.eclipse.imp.x10dt.tests.services.swbot.conditions.X10DTConditions;
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
@SuppressWarnings("nls")
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
   * Tests compilation and run of an X10 program with C++ back-end.
   */
  @Test public void canRunX10World() {
    final String projectName = "demo";
    ProjectUtils.createX10ProjectWithCppBackEnd(bot, projectName);
    
    bot.menu("Run").menu("Run Configurations...").click();
    final SWTBotShell runShell = bot.shell("Run Configurations");
    runShell.activate();
    bot.tree().select("X10 Application (C++ back-end)").contextMenu("New").click();
    
    bot.cTabItem("Application").activate();
    bot.textInGroup("X10 Project", 0).setText(projectName);
    bot.textInGroup("Main class", 0).setText("Hello");
    
    runShell.bot().button("Run").click();
    
    final SWTBotView consoleView = bot.viewByTitle("Console");
    bot.waitUntil(X10DTConditions.workbenchPartIsActive(consoleView), 20000);
    consoleView.bot().styledText().getText().contains("Hello X10 world");
  }
  
  // --- Fields
  
  private static SWTWorkbenchBot bot;

}
