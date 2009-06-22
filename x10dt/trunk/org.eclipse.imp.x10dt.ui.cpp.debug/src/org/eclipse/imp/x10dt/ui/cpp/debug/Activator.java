/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Controls the plug-in life cycle for this project.
 * 
 * @author egeay
 */
public class Activator extends AbstractUIPlugin {

  // --- Overridden methods

  public void start(final BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  public void stop(final BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  /**
   * Returns the plugin instance.
   * 
   * @return A non-null value if the plugin has started, otherwise <b>null</b>.
   */
  public static Activator getDefault() {
    return plugin;
  }

  // --- Fields

  private static Activator plugin;

}
