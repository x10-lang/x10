/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

/**
 * Encapsulates information for the debugging experience.
 * 
 * @author egeay
 */
public interface IDebuggingInfoConf {
  
  /**
   * Returns the folder path containing the debugger instance. 
   * 
   * @return A non-null string but possibly empty.
   */
  public String getDebuggerFolder();
  
  /**
   * Returns the port to use for the communication between the client and the remote debugger instance.
   * 
   * @return A positive number.
   */
  public int getPort();

}
