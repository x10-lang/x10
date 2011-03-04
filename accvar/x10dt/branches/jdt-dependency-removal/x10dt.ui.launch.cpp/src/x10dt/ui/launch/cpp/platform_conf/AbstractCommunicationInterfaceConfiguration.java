/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.ptp.services.core.IServiceProvider;

import x10dt.ui.launch.core.utils.CodingUtils;


abstract class AbstractCommunicationInterfaceConfiguration implements ICommunicationInterfaceConf {
  
  // --- Abstract methods definition
  
  abstract AbstractCommunicationInterfaceConfiguration copy();

  // --- Interface methods implementation
  
  public final String getServiceModeId() {
    return this.fServiceModeId;
  }

  public final String getServiceTypeId() {
    return this.fServiceTypeId;
  }
  
  public boolean isComplete() {
    return hasData(this.fServiceTypeId) && hasData(this.fServiceModeId);
  }
    
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    final AbstractCommunicationInterfaceConfiguration rhsObj = (AbstractCommunicationInterfaceConfiguration) rhs;
    return CodingUtils.equals(this.fServiceTypeId, rhsObj.fServiceTypeId) && 
           CodingUtils.equals(this.fServiceModeId, rhsObj.fServiceModeId);
  }
  
  public int hashCode() {
    return CodingUtils.generateHashCode(123, this.fServiceTypeId, this.fServiceModeId);
  }
  
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Service Type Id: ").append(this.fServiceTypeId) //$NON-NLS-1$
      .append("\nService Mode Id: ").append(this.fServiceModeId); //$NON-NLS-1$
    return sb.toString();
  }
  
  // --- Internal code
  
  AbstractCommunicationInterfaceConfiguration() {}
  
  AbstractCommunicationInterfaceConfiguration(final AbstractCommunicationInterfaceConfiguration original) {
    this.fServiceTypeId = original.fServiceTypeId;
    this.fServiceModeId = original.fServiceModeId;
  }
  
  AbstractCommunicationInterfaceConfiguration(final IResourceManagerConfiguration rmConf) {
    this.fServiceTypeId = ((IServiceProvider) rmConf).getId();
    this.fServiceModeId = ((IServiceProvider) rmConf).getServiceId();
  }
  
  void applyChanges(final ICommunicationInterfaceConf source) {
    this.fServiceTypeId = source.getServiceTypeId();
    this.fServiceModeId = source.getServiceModeId();
  }
  
  protected final boolean hasData(final String data) {
    return (data != null) && (data.length() > 0);
  }
  
  // --- Fields
  
  String fServiceTypeId;
  
  String fServiceModeId;
  
}
