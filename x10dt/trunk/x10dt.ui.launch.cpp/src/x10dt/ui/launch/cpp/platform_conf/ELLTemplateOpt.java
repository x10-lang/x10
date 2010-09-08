/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

/**
 * Defines the policy to write the LoadLeveler template file each time the proxy is started. 
 * 
 * @author egeay
 */
public enum ELLTemplateOpt {
  
  /**
   * Always write the template file to /tmp when the proxy is started.
   */
  EAlwaysWrite,
  
  /**
   * Never write the template file in the file system when the proxy is started.
   */
  ENeverWrite;

}
