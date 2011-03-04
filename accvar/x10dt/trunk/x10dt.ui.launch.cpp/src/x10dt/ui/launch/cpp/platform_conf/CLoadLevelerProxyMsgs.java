/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

/**
 * Proxy message logging levels for IBM LoadLeveler.
 * 
 * @author egeay
 */
public class CLoadLevelerProxyMsgs {
  
  /**
   * Trace level for proxy messages.
   */
  public static int TRACE = 1;
  
  /**
   * Info level for proxy messages.
   */
  public static int INFO = 2;
  
  /**
   * Warning level for proxy messages.
   */
  public static int WARNING = 4;
  
  /**
   * Error level for proxy messages.
   */
  public static int ERROR = 8;
  
  /**
   * Fatal level for proxy messages.
   */
  public static int FATAL = 16;
  
  /**
   * Args level for proxy messages.
   */
  public static int ARGS = 32;

}
