/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.core.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.window.IShellProvider;

/**
 * Responsible of two main tasks for the back-end conversion action on a given X10 Project.
 * <ul>
 * <li>Provide the target project nature id.</li>
 * <li>Perform some pre-processing if it is necessary before the project nature changes.
 * </ul>
 * 
 * @author egeay
 */
public interface IBackEndX10ProjectConverter {
  
  /**
   * Returns the target project nature id.
   * 
   * @return A non-null string identifying the id.
   */
  public String getProjectNatureId();
  
  /**
   * Called after the project description changes in order to allow some necessary post-processing.
   * 
   * @param shellProvider The shell provider that can be used for error reporting.
   * @param project The project considered for back-end change.
   */
  public void postProjectSetup(final IShellProvider shellProvider, final IProject project);
  
  /**
   * Called before the project description changes in order to allow some necessary pre-processing.
   * 
   * @param shellProvider The shell provider that can be used for error reporting.
   * @param project The project considered for back-end change.
   */
  public void preProjectSetup(final IShellProvider shellProvider, final IProject project);

}
