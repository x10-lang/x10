/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.tests.services.swbot.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * Utility methods for the Eclipse Problems View.
 * 
 * @author egeay
 */
public final class ProblemsViewUtils {
  
  /**
   * Returns the number of error markers that are currently present in the Problems view.
   * 
   * @param bot The current SWTBot instance to test the workbench.
   * @return A non-null array but possibly empty if no errors are present.
   */
  public static String[] getErrorMessages(final SWTWorkbenchBot bot) {
    return getMessages(bot, "Errors", 0); //$NON-NLS-1$
  }
  
  /**
   * Returns the resources on which there are compilation errors in the Problems view.
   * 
   * @param bot The current SWTBot instance to test the workbench.
   * @return A non-null array but possibly empty if no errors are present.
   */
  public static String[] getErrorResources(final SWTWorkbenchBot bot){
	  return getMessages(bot, "Errors", 1);
  }
  
  /**
   * Returns the number of warning markers that are currently present in the Problems view.
   * 
   * @param bot The current SWTBot instance to test the workbench.
   * @return A non-null array but possibly empty if no warnings are present.
   */
  public static String[] getWarningMessages(final SWTWorkbenchBot bot) {
    return getMessages(bot, "Warnings", 0); //$NON-NLS-1$
  }
  
  // --- Private code
  
  private ProblemsViewUtils() {}
  
  private static String[] getMessages(final SWTWorkbenchBot bot, final String messageType, final int col) {
    final SWTBotView view = bot.viewByTitle("Problems"); //$NON-NLS-1$
    view.setFocus();
    final List<String> messages = new ArrayList<String>();
    final SWTBotTree tree = view.bot().tree();
    if (! tree.hasItems()) {
      return new String[0];
    }
    for (final SWTBotTreeItem item : tree.getAllItems()) {
      if (item.getText().contains(messageType)) {
        for (final SWTBotTreeItem subItem : item.expand().getItems()) {
          messages.add(subItem.row().get(col));
        }
      }
    }
    return messages.toArray(new String[messages.size()]);
  }

}
