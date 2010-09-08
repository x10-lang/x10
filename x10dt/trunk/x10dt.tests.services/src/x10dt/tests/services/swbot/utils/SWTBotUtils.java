/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.tests.services.swbot.utils;

import java.util.List;

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

  public static void closeWelcomeViewIfNeeded(SWTWorkbenchBot topLevelBot) {
    List<SWTBotView> views= topLevelBot.views();
    for(SWTBotView v: views) {
      if ("Welcome".equals(v.getTitle())) {
        v.close();
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
          for (MenuItem item : bar.getItems()) {
            System.out.println(item.getText());
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
  
  // --- Private code
  
  private SWTBotUtils() {}

  public static void resetWorkbench(SWTWorkbenchBot bot) {
    closeAllShells(bot);
    saveAllEditors(bot);
    closeAllEditors(bot);
  }

  public static void closeAllShells(SWTWorkbenchBot bot) {
    SWTBotShell[] shells = bot.shells();
    for (SWTBotShell shell : shells) {
        if (!isEclipseShell(shell, bot)) {
            shell.close();
        }
    }
  }

  public static void saveAllDirtyEditors(SWTWorkbenchBot bot) {
    List<? extends SWTBotEditor> editors = bot.editors();
    for (SWTBotEditor editor : editors) {
      if (editor.isDirty()) {
          editor.save();
      }
    }
  }

  public static void saveAllEditors(SWTWorkbenchBot bot) {
    List<? extends SWTBotEditor> editors = bot.editors();
    for (SWTBotEditor editor : editors) {
        editor.save();
    }
  }

  public static void closeAllEditors(SWTWorkbenchBot bot) {
    List<? extends SWTBotEditor> editors = bot.editors();
    for (SWTBotEditor editor : editors) {
        editor.close();
    }
  }

  public static boolean isEclipseShell(final SWTBotShell shell, SWTWorkbenchBot bot) {
    return getActiveWorkbenchWindowShell(bot) == shell.widget;
  }

  public static IWorkbenchWindow getActiveWorkbenchWindow(SWTWorkbenchBot bot) {
    return UIThreadRunnable.syncExec(bot.getDisplay(), new Result<IWorkbenchWindow>() {
        public IWorkbenchWindow run() {
            return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        }
    });
  }

  public static Widget getActiveWorkbenchWindowShell(SWTWorkbenchBot bot) {
    return getActiveWorkbenchWindow(bot).getShell();
  }

}
