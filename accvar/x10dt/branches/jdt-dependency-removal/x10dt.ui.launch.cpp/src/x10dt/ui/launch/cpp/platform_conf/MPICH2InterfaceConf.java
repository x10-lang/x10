/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import org.eclipse.ptp.rm.mpi.mpich2.core.rmsystem.IMPICH2ResourceManagerConfiguration;


final class MPICH2InterfaceConf extends MessagePassingInterfaceConf implements IMPICH2InterfaceConf {

  // --- ICommunicationInterfaceConf's interface methods implementation
  
  public void visitInterfaceOptions(final ICIConfOptionsVisitor visitor) {
    visitor.visit(this);
  }
  
  // --- Abstract methods implementation
  
  AbstractCommunicationInterfaceConfiguration copy() {
    return new MPICH2InterfaceConf(this);
  }
  
  // --- Overridden methods
  
  public boolean isComplete() {
    if (super.isComplete()) {
      boolean firstStep = false;
      if (super.fDefaultToolCmds) {
        firstStep = true;
      } else {
        firstStep = hasData(super.fLaunchCmd) && hasData(super.fDebugCmd) && hasData(super.fDiscoverCmd) && 
                    hasData(super.fMonitorCmd);
      }
      if (firstStep) {
        return (super.fDefaultIntallLocation ? true : hasData(super.fInstallLocation));
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
  
  // --- Internal code
  
  MPICH2InterfaceConf() {}
  
  MPICH2InterfaceConf(final IMPICH2ResourceManagerConfiguration rmConf) {
    super(rmConf);
  }
  
  MPICH2InterfaceConf(final MPICH2InterfaceConf source) {
    super(source);
  }

}
