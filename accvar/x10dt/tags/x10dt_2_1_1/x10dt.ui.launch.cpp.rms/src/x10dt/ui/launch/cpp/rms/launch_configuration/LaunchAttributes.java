/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.rms.launch_configuration;

import org.eclipse.ptp.core.attributes.ArrayAttributeDefinition;
import org.eclipse.ptp.core.attributes.StringAttributeDefinition;

import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.cpp.rms.Messages;

/**
 * Launch attributes for Sockets and Standalone transport.
 * 
 * @author egeay
 */
public final class LaunchAttributes {
  
  /**
   * Returns the host file attribute definition for X10 sockets transport.
   * 
   * @return A non-null instance.
   */
  public static StringAttributeDefinition getHostFileAttr() {
    return fHostFileAttr;
  }
  
  /**
   * Returns the host list attribute definition for X10 sockets transport.
   * 
   * @return A non-null instance.
   */
  public static ArrayAttributeDefinition<String> getHostListAttr() {
    return fHostListAttr;
  }
  
  // --- Private code
  
  private LaunchAttributes() {}
  
  // --- Fields
  
  private static final String HOST_FILE_ATTR_ID = "hostFileAttr"; //$NON-NLS-1$
  
  private static final String HOST_LIST_ATTR_ID = "hostListAttr"; //$NON-NLS-1$
  
  private static final StringAttributeDefinition fHostFileAttr;
  
  private static final ArrayAttributeDefinition<String> fHostListAttr;
  
  static {
    fHostFileAttr = new StringAttributeDefinition(HOST_FILE_ATTR_ID, Messages.LA_SocketsHostFileName, 
                                                  Messages.LA_SocketsHostFileDescr, false, Constants.EMPTY_STR);
    
    fHostListAttr = new ArrayAttributeDefinition<String>(HOST_LIST_ATTR_ID, Messages.LA_SocketsHostListName, 
                                                         Messages.LA_SocketsHostListDescr, false, new String[0]);
  }

}
