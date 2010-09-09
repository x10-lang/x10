/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import x10dt.ui.launch.cpp.editors.EOpenMPIVersion;

/**
 * Contains the communication interface options for OpenMPI.
 * 
 * @author egeay
 */
public interface IOpenMPIInterfaceConf extends IMessagePassingInterfaceConf {
  
  /**
   * Returns the OpenMPI version to consider. This is valid only if the call to {@link #getServiceTypeId()} identifies an
   * OpenMPI communication interface.
   * 
   * @return A non-null value.
   */
  public EOpenMPIVersion getOpenMPIVersion();

}
