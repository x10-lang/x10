/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.tests.services.swbot.conditions;

import org.eclipse.core.resources.IResource;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotWorkbenchPart;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.ui.IWorkbenchPartReference;
import org.hamcrest.Matcher;

/**
 * Factory class that provide other {@link ICondition} implementations that can be useful for X10DT testing.
 * 
 * @author egeay
 */
public final class X10DTConditions {

  /**
   * Creates a condition that tests if a workbench part is active or not at a given time.
   * 
   * @param <T> The SWT widget type extending {@link IWorkbenchPartReference}.
   * @param widget The SWTBot widget to consider.
   * @return A non-null implementation of {@link ICondition}.
   */
  public static <T extends IWorkbenchPartReference> ICondition workbenchPartIsActive(final SWTBotWorkbenchPart<T> widget) {
    return new WorkbenchPartIsActiveCondition<T>(widget);
  }
  
  /**
   * Creates a condition that tests in all the resources of the current workspace if some satisfy the matcher provided. 
   * 
   * @param matcher The matcher instance to use to filter the resources of interest.
   * @return A non-null implementation of {@link ICondition}.
   */
  public static ICondition matchResource(final Matcher<IResource> matcher) {
    return new MatchResourceCondition(matcher);
  }
  
  /**
   * Creates a condition that tests in all the resources under a given root folder of the current workspace if 
   * some satisfy the matcher provided. 
   * 
   * @param matcher The matcher instance to use to filter the resources of interest.
   * @param rootFolder The absolute path to the root folder to consider as root. 
   * @return A non-null implementation of {@link ICondition}.
   */
  public static ICondition matchResource(final Matcher<IResource> matcher, final String rootFolder) {
    return new MatchResourceCondition(matcher, rootFolder);
  }
  
  // --- Fields
  
  private X10DTConditions() {}

}
