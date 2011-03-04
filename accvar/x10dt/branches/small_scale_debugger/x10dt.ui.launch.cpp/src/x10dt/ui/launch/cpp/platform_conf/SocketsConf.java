/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;

import x10dt.ui.launch.core.utils.PTPConstants;


final class SocketsConf extends AbstractCommunicationInterfaceConfiguration implements ICommunicationInterfaceConf {

  // --- Interface methods implementation
  
  public boolean hasSameCommunicationInterfaceInfo(final IResourceManagerConfiguration rmConfiguration) {
    return PTPConstants.SOCKETS_SERVICE_PROVIDER_ID.equals(rmConfiguration.getResourceManagerId());
  }

  public void visitInterfaceOptions(final ICIConfOptionsVisitor visitor) {
    // Nothing to do.
  }

  // --- Abstract methods implementation
  
  AbstractCommunicationInterfaceConfiguration copy() {
    return new SocketsConf(this);
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    return (rhs != null) && (rhs instanceof SocketsConf) && super.equals(rhs);
  }
  
  public int hashCode() {
    return 65747;
  }
  
  // --- Private code
  
  SocketsConf() {}
  
  SocketsConf(final AbstractCommunicationInterfaceConfiguration original) {
    super(original);
  }
  
  SocketsConf(final IResourceManagerConfiguration rmConf) {
    super(rmConf);
  }

}
