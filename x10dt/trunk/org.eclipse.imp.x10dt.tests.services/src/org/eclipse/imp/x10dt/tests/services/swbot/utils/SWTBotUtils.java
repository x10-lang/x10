/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.tests.services.swbot.utils;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.WidgetResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;

/**
 * Some utilities that are missing in SWTBot.
 * 
 * @author egeay
 */
public final class SWTBotUtils {
  
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

}
