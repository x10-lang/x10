/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.java.launching;

import x10dt.ui.launch.java.Activator;

/**
 * Contains launching configuration attributes for MultiVM configuration tab.
 * 
 * @author egeay
 */
public final class MultiVMAttrConstants {
  
  static final String ATTR_IS_LOCAL = Activator.PLUGIN_ID + ".mvm.is_local"; //$NON-NLS-1$
  
  static final String ATTR_HOST = Activator.PLUGIN_ID + ".mvm.host"; //$NON-NLS-1$
  
  static final String ATTR_PORT = Activator.PLUGIN_ID + ".mvm.port"; //$NON-NLS-1$
  
  static final String ATTR_USERNAME = Activator.PLUGIN_ID + ".mvm.user_name"; //$NON-NLS-1$
  
  static final String ATTR_IS_PASSWORD_BASED = Activator.PLUGIN_ID + ".mvm.is_password_based"; //$NON-NLS-1$
  
  static final String ATTR_PASSWORD = Activator.PLUGIN_ID + ".mvm.password"; //$NON-NLS-1$
  
  static final String ATTR_PRIVATE_KEY_FILE = Activator.PLUGIN_ID + ".mvm.private_key_file"; //$NON-NLS-1$
  
  static final String ATTR_PASSPHRASE = Activator.PLUGIN_ID + ".mvm.passphrase"; //$NON-NLS-1$
  
  static final String ATTR_USE_PORT_FORWARDING = Activator.PLUGIN_ID + ".mvm.use.port_forwarding"; //$NON-NLS-1$
  
  static final String ATTR_TIMEOUT = Activator.PLUGIN_ID + ".mvm.use.timeout"; //$NON-NLS-1$
  
  static final String ATTR_LOCAL_ADDRESS = Activator.PLUGIN_ID + ".mvm.use.local_address"; //$NON-NLS-1$
  
  static final String ATTR_REMOTE_OUTPUT_FOLDER = Activator.PLUGIN_ID + ".mvm.output.folder"; //$NON-NLS-1$
  
  static final String ATTR_X10_DISTRIBUTION = Activator.PLUGIN_ID + ".mvm.x10.distrib"; //$NON-NLS-1$
  
  // --- Private code
  
  private MultiVMAttrConstants() {}

}
