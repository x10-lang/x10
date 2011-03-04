/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.editors;

/**
 * Listener for getting notified when end-user changes PTP service provider type or mode.
 * 
 * @author egeay
 */
public interface IServiceProviderChangeListener {
  
  /**
   * Indicates that a service type id just changed.
   * 
   * @param serviceTypeId The new service type id.
   */
  public void serviceTypeChange(final String serviceTypeId);
  
  /**
   * Indicates that a service mode id just changed.
   * 
   * @param serviceModeId The new service mode id.
   */
  public void serviceModeChange(final String serviceModeId);

}
