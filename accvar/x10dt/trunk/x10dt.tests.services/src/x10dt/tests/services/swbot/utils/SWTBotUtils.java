/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.tests.services.swbot.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.results.WidgetResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Some utilities that are missing in SWTBot.
 * 
 * @author egeay
 */
public final class SWTBotUtils {

  /**
   * Closes all editors attached to the SWTBot Workbench instance transmitted.
   * 
   * @param bot The current SWTBot instance to consider.
   */
  public static void closeAllEditors(final SWTWorkbenchBot bot) {
    for (final SWTBotEditor editor : bot.editors()) {
      editor.close();
    }
  }
  
  /**
   * Closes all shells attached to the SWTBot Workbench instance transmitted.
   * 
   * @param bot The current SWTBot instance to consider.
   */
  public static void closeAllShells(final SWTWorkbenchBot bot) {
    for (final SWTBotShell shell : bot.shells()) {
      if (! isEclipseShell(shell, bot)) {
        shell.close();
      }
    }
  }
  
  /**
   * Closes the welcome view if it is presents in the list of views from the SWTBot Workbench instance transmitted.
   * 
   * @param topLevelBot The current SWTBot instance to consider.
   */
  public static void closeWelcomeViewIfNeeded(final SWTWorkbenchBot topLevelBot) {
    for (final SWTBotView view : topLevelBot.views()) {
      if ("Welcome".equals(view.getTitle())) { //$NON-NLS-1$
        view.close();
        topLevelBot.shells()[0].activate();
        return;
      }
    }
  }
  
  /**
   * Finds a SWTBot sub menu from a given menu item.
   * 
   * @param parentMenu The SWTBot parent menu to consider.
   * @param menuItemName The name of the sub-menu to look for.
   * @return A non-null SWTBot menu identifying the sub menu.
   * @throws WidgetNotFoundException Occurs if we could not find the sub menu with the parameters provided.
   */
  public static SWTBotMenu findSubMenu(final SWTBotMenu parentMenu, final String menuItemName) {
    final MenuItem menuItem = UIThreadRunnable.syncExec(new WidgetResult<MenuItem>() {
      public MenuItem run() {
        final Menu bar = parentMenu.widget.getMenu();
        if (bar != null) {
          for (final MenuItem item : bar.getItems()) {
            if (item.getText().equals(menuItemName)) {
              return item;
            }
          }
        }
        return null;
      }
    });

    if (menuItem == null) {
      throw new WidgetNotFoundException(NLS.bind("MenuItem ''{0}'' not found.", menuItemName)); //$NON-NLS-1$
    } else {
      return new SWTBotMenu(menuItem);
    }
  }

  /**
   * Finds a SWTBot sub menu from a given menu item.
   * 
   * @param parentMenu The SWTBot parent menu to consider.
   * @param itemNamePattern The regexp pattern for the name of the sub-menu to look for.
   * @return A non-null SWTBot menu identifying the sub menu.
   * @throws WidgetNotFoundException Occurs if we could not find the sub menu with the parameters provided.
   */
  public static SWTBotMenu findSubMenu(final SWTBotMenu parentMenu, final Pattern itemNamePattern) {
//  System.out.println("looking for menu item pattern: " + itemNamePattern);
    final MenuItem menuItem = UIThreadRunnable.syncExec(new WidgetResult<MenuItem>() {
      public MenuItem run() {
        final Menu bar = parentMenu.widget.getMenu();
        if (bar != null) {
          for (MenuItem item : bar.getItems()) {
//          System.out.println(item.getText());
            Matcher m = itemNamePattern.matcher(item.getText());
            if (m.matches()) {
              return item;
            }
          }
        }
        return null;
      }
    });

    if (menuItem == null) {
      throw new WidgetNotFoundException(NLS.bind("MenuItem with pattern ''{0}'' not found.", itemNamePattern)); //$NON-NLS-1$
    } else {
      return new SWTBotMenu(menuItem);
    }
  }

  /**
   * Returns the shell of the active workbench window from the SWTBot Workbench instance transmitted.
   * 
   * @param bot The current SWTBot instance to consider.
   * @return The shell containing this active window's controls or <b>null</b> if the shell has not been created yet or 
   * if the window has been closed.
   */
  public static Widget getActiveWorkbenchWindowShell(final SWTWorkbenchBot bot) {
    return getActiveWorkbenchWindow(bot).getShell();
  }
  
  /**
   * Returns the active workbench window from the SWTBot Workbench instance transmitted.
   * 
   * @param bot The current SWTBot instance to consider.
   * @return A possibly <b>null</b> value if there is no active workbench window or if called from a non-UI thread.
   */
  public static IWorkbenchWindow getActiveWorkbenchWindow(final SWTWorkbenchBot bot) {
    return UIThreadRunnable.syncExec(bot.getDisplay(), new Result<IWorkbenchWindow>() {
      public IWorkbenchWindow run() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      }
    });
  }
  
  /**
   * Identifies if the SWTBot shell transmitted is the shell of the active worbench window.
   * 
   * @param shell The SWBot shell to compare.
   * @param bot The current SWTBot instance to consider.
   * @return True if the shell is the main Eclipse shell, false otherwise.
   */
  public static boolean isEclipseShell(final SWTBotShell shell, final SWTWorkbenchBot bot) {
    return getActiveWorkbenchWindowShell(bot) == shell.widget;
  }

  /**
   * Saves all the dirty editors present in the SWT Workbench instance transmitted.
   * 
   * @param bot The current SWTBot instance to consider.
   */
  public static void saveAllDirtyEditors(final SWTWorkbenchBot bot) {
    for (final SWTBotEditor editor : bot.editors()) {
      if (editor.isDirty()) {
        editor.save();
      }
    }
  }
  
  /**
   * Saves all the editors present in the SWT Workbench instance transmitted.
   * 
   * @param bot The current SWTBot instance to consider.
   */
  public static void saveAllEditors(final SWTWorkbenchBot bot) {
    for (final SWTBotEditor editor : bot.editors()) {
      editor.save();
    }
  }
  
  /**
   * Closes all shells, saves and closes all editors present in the SWT Workbench instance transmitted.
   * 
   * @param bot The current SWTBot instance to consider.
   */
  public static void resetWorkbench(final SWTWorkbenchBot bot) {
    closeAllShells(bot);
    saveAllEditors(bot);
    closeAllEditors(bot);
  }

  // --- Private code

  private SWTBotUtils() {
  }

}
