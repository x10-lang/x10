/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.launch;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Base class storing plugin instance and providing logging shortcuts.
 *
 * @author egeay
 */
public abstract class AbstractUIBasePlugin extends AbstractUIPlugin {
  
  //--- Overridden methods

  public void start(final BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  public void stop(final BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }
  
  // --- Public services
  
  /**
   * Returns the current plugin instance.
   * 
   * @return A non-null value if the plugin is activated, or <b>null</b> if it is stopped.
   */
  public static AbstractUIPlugin getInstance() {
    return plugin;
  }
  
  /**
   * Logs (if the plugin is active) the operation outcome provided through the status instance.
   * 
   * @param status The status to log.
   */
  public static final void log(final IStatus status) {
    if (plugin != null) {
      plugin.getLog().log(status);
    }
  }
  
  /**
   * Logs (if the plugin is active) the outcome of an operation with the parameters provided.
   * 
   * @param severity The severity of the message. The code is one of {@link IStatus#OK}, {@link IStatus#ERROR}, 
   * {@link IStatus#INFO}, {@link IStatus#WARNING} or {@link IStatus#CANCEL}.
   * @param code The plug-in-specific status code, or {@link IStatus#OK}.
   * @param message The human-readable message, localized to the current locale.
   * @param exception The exception to log, or <b>null</b> if not applicable.
   */
  public static final void log(final int severity, final int code, final String message, final Throwable exception) {
    if (plugin != null) {
      plugin.getLog().log(new Status(severity, plugin.getBundle().getSymbolicName(), code, message, exception));
    }
  }
  
  /**
   * Logs (if the plugin is active) the outcome of an operation with the parameters provided.
   * 
   * <p>Similar to {@link #log(int, int, String, Throwable)} with <code>code = IStatus.OK</code>.
   * 
   * @param severity The severity of the message. The code is one of {@link IStatus#OK}, {@link IStatus#ERROR}, 
   * {@link IStatus#INFO}, {@link IStatus#WARNING} or {@link IStatus#CANCEL}.
   * @param message The human-readable message, localized to the current locale.
   * @param exception The exception to log, or <b>null</b> if not applicable.
   */
  public static final void log(final int severity, final String message, final Throwable exception) {
    if (plugin != null) {
      plugin.getLog().log(new Status(severity, plugin.getBundle().getSymbolicName(), message, exception));
    }
  }
  
  /**
   * Logs (if the plugin is active) the outcome of an operation with the parameters provided.
   * 
   * <p>Similar to {@link #log(int, int, String, Throwable)} with <code>code = IStatus.OK</code> and 
   * <code>exception = null</code>.
   * 
   * @param severity The severity of the message. The code is one of {@link IStatus#OK}, {@link IStatus#ERROR}, 
   * {@link IStatus#INFO}, {@link IStatus#WARNING} or {@link IStatus#CANCEL}.
   * @param message The human-readable message, localized to the current locale.
   */
  public static final void log(final int severity, final String message) {
    if (plugin != null) {
      plugin.getLog().log(new Status(severity, plugin.getBundle().getSymbolicName(), message));
    }
  }
  
  // --- Fields

  private static AbstractUIPlugin plugin;

}
