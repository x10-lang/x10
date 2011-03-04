/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.rms.launch_configuration;

import x10dt.ui.launch.cpp.rms.RMSActivator;


final class LaunchConfigConstants {
  
  // --- Attribute Constants
  
  static final String ATTR_USE_HOSTFILE = RMSActivator.PLUGIN_ID + ".launchAttrs.useHostFile";  //$NON-NLS-1$
  
  static final String ATTR_NUM_PLACES = RMSActivator.PLUGIN_ID + ".launchAttrs.numPlaces";  //$NON-NLS-1$
  
  static final String ATTR_HOSTFILE = RMSActivator.PLUGIN_ID + ".launchAttrs.hostFile";  //$NON-NLS-1$
  
  static final String ATTR_HOSTLIST = RMSActivator.PLUGIN_ID + ".launchAttrs.hostList";  //$NON-NLS-1$
  
  // --- Default attribute values
  
  static final int DEFAULT_NUM_PLACES = 1;
  
  // --- Private code
  
  private LaunchConfigConstants() {}

}
