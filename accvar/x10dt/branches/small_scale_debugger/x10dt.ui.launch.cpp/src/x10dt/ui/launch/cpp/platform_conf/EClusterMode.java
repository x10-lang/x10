/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

/**
 * Defines the different options to run in multi-cluster or not.
 * 
 * @author egeay
 */
public enum EClusterMode {
  
  /**
   * Runs the system in default cluster mode.
   */
  DEFAULT,
  
  /**
   * Runs the system in local mode.
   */
  LOCAL,
  
  /**
   * Runs the system in multi-cluster mode.
   */
  MULTI_CLUSTER;

}
