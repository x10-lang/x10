/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.debug;

/**
 * Miscellaneous constants used within this plug-in.
 * 
 * @author egeay
 */
@SuppressWarnings("all")
public final class Constants {
  
  // --- Debugger Tab
  
  public static final String ATTR_REMOTE_DEBUGGER_PATH = DebugCore.PLUGIN_ID + ".debug.engine.daemon.path"; //$NON-NLS-1$
  
  public static final String ATTR_PORT = DebugCore.PLUGIN_ID + ".debug.port"; //$NON-NLS-1$
  
  // --- Default values
  
  public static int DEFAULT_PORT = 8002;

}
