/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.tests.services.swbot.utils;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

/**
 * Contains utilities for perspective manipulations.
 * 
 * @author egeay
 */
public final class PerspectiveUtils {
  
  /**
   * Switches to X10 perspective if it is not already active.
   * 
   * @param bot The current SWTBot instance to test the workbench.
   */
  public static void switchToX10Perspective(final SWTWorkbenchBot bot) {
    if (! X10_PERSPECTIVE_NAME.equals(bot.activePerspective().getLabel())) {
      bot.perspectiveByLabel(X10_PERSPECTIVE_NAME).activate();
    }
  }
  
  // --- Private code
  
  private PerspectiveUtils() {}
  
  // --- Fields
  
  private static final String X10_PERSPECTIVE_NAME = "X10"; //$NON-NLS-1$

}
