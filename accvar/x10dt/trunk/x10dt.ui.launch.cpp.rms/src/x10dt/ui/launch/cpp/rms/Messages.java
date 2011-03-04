/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.rms;

import org.eclipse.osgi.util.NLS;

/**
 * Bundle class for all messages of "x10dt.ui.launch.cpp.rms".
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public class Messages extends NLS {
  
  public static String SSS_Sockets;

  public static String SSS_SocketsRMDescr;

  public static String SSS_Standalone;

  public static String SSS_StandaloneRMDescr;
  
  // --- Private code

  private Messages() {
  }
  
  // --- Fields
  
  private static final String BUNDLE_NAME = "x10dt.ui.launch.cpp.rms.messages"; //$NON-NLS-1$
  
  static {
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

}
