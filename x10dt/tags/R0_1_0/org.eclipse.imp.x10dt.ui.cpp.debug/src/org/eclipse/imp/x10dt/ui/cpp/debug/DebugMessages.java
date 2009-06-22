/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug;

import org.eclipse.osgi.util.NLS;

/**
 * Bundle class for all messages of "oorg.eclipse.imp.x10dt.ui.cpp.debug".
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public class DebugMessages extends NLS {

  public static String DT_BrowseBt;
  
  public static String DT_DbgEngineDaemonLoc;

  public static String DT_DbgEngineDaemonPathLabel;

  public static String DT_GetIPBt;
  
  public static String DT_GroupName;
  
  public static String DT_LocalAddressLabel;
  
  public static String DT_StopInMainBt;
  
  public static String DT_TabName;
  
  public static String DT_UnvalidIP;

  public static String DT_UnvalidIPFormat;

  public static String PDID_ServerSocketInitError;

  public static String PDID_SocketClosingError;

  public static String PDID_SocketListeningError;

  public static String XDbg_DebuggerInitError;

  public static String XDbg_NoRmtDbgPath;

  public static String XDbg_SessionFailed;

  // --- Private code

  private static final String BUNDLE_NAME = "org.eclipse.imp.x10dt.ui.cpp.debug.messages"; //$NON-NLS-1$

  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, DebugMessages.class);
  }

  private DebugMessages() {
  }
}
