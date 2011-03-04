/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ptp.rm.ibm.ll.core.rmsystem.IIBMLLResourceManagerConfiguration;
import org.eclipse.ptp.rm.ibm.pe.core.rmsystem.IPEResourceManagerConfiguration;
import org.eclipse.ptp.rm.mpi.mpich2.core.rmsystem.IMPICH2ResourceManagerConfiguration;
import org.eclipse.ptp.rm.mpi.openmpi.core.rmsystem.IOpenMPIResourceManagerConfiguration;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;

import x10dt.ui.launch.core.utils.CodingUtils;
import x10dt.ui.launch.core.utils.PTPConstants;


final class CommInterfaceFactory {
  
  CommInterfaceFactory() {
    this.fCommInterfaceConfs = new HashMap<String, AbstractCommunicationInterfaceConfiguration>(5);
  }
  
  CommInterfaceFactory(final IResourceManagerConfiguration rmConf) {
    this();
    final String ciType = rmConf.getResourceManagerId();
    final AbstractCommunicationInterfaceConfiguration configuration;
    if (PTPConstants.OPEN_MPI_SERVICE_PROVIDER_ID.equals(ciType)) {
      configuration = new OpenMPIInterfaceConf((IOpenMPIResourceManagerConfiguration) rmConf);
    } else if (PTPConstants.MPICH2_SERVICE_PROVIDER_ID.equals(ciType)) {
      configuration = new MPICH2InterfaceConf((IMPICH2ResourceManagerConfiguration) rmConf);
    } else if (PTPConstants.LOAD_LEVELER_SERVICE_PROVIDER_ID.equals(ciType)) {
      configuration = new LoadLevelerConf((IIBMLLResourceManagerConfiguration) rmConf);
    } else if (PTPConstants.PARALLEL_ENVIRONMENT_SERVICE_PROVIDER_ID.equals(ciType)) {
      configuration = new ParallelEnvironmentConf((IPEResourceManagerConfiguration) rmConf);
    } else if (PTPConstants.SOCKETS_SERVICE_PROVIDER_ID.equals(ciType)) {
      configuration = new SocketsConf(rmConf);
    } else if (PTPConstants.STANDALONE_SERVICE_PROVIDER_ID.equals(ciType)) {
      configuration = new StandaloneConf(rmConf);
    } else {
      configuration = null;
    }
    this.fCommInterfaceConfs.put(ciType, configuration);
    this.fType = ciType;
  }
  
  CommInterfaceFactory(final CommInterfaceFactory source) {
    this();
    this.fType = source.fType;
    for (final Map.Entry<String, AbstractCommunicationInterfaceConfiguration> entry : source.fCommInterfaceConfs.entrySet()) {
      this.fCommInterfaceConfs.put(entry.getKey(), entry.getValue().copy());
    }
  }
  
  // --- Internal services
  
  void applyChanges(final CommInterfaceFactory source) {
    this.fCommInterfaceConfs.clear();
    for (final Map.Entry<String, AbstractCommunicationInterfaceConfiguration> entry : source.fCommInterfaceConfs.entrySet()) {
      this.fCommInterfaceConfs.put(entry.getKey(), entry.getValue().copy());
    }
  }
  
  void defineCurrentCommInterfaceType(final String type) {
    this.fType = type;
  }
  
  AbstractCommunicationInterfaceConfiguration getOrCreate(final String ciType) {
    final AbstractCommunicationInterfaceConfiguration conf = this.fCommInterfaceConfs.get(ciType);
    if (conf == null) {
      final AbstractCommunicationInterfaceConfiguration configuration;
      if (PTPConstants.OPEN_MPI_SERVICE_PROVIDER_ID.equals(ciType)) {
        configuration = new OpenMPIInterfaceConf();
      } else if (PTPConstants.MPICH2_SERVICE_PROVIDER_ID.equals(ciType)) {
        configuration = new MPICH2InterfaceConf();
      } else if (PTPConstants.LOAD_LEVELER_SERVICE_PROVIDER_ID.equals(ciType)) {
        configuration = new LoadLevelerConf();
      } else if (PTPConstants.PARALLEL_ENVIRONMENT_SERVICE_PROVIDER_ID.equals(ciType)) {
        configuration = new ParallelEnvironmentConf();
      } else if (PTPConstants.SOCKETS_SERVICE_PROVIDER_ID.equals(ciType)) {
        configuration = new SocketsConf();
      } else if (PTPConstants.STANDALONE_SERVICE_PROVIDER_ID.equals(ciType)) {
        configuration = new StandaloneConf();
      } else {
        configuration = null;
      }
      this.fCommInterfaceConfs.put(ciType, configuration);
      return configuration;
    } else {
      return conf;
    }
  }
  
  AbstractCommunicationInterfaceConfiguration getCurrentCommunicationInterface() {
    return this.fCommInterfaceConfs.get(this.fType);
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    final CommInterfaceFactory rhsObj = (CommInterfaceFactory) rhs;
    return CodingUtils.equals(this.fType, rhsObj.fType) && 
           CodingUtils.equals(this.fCommInterfaceConfs, rhsObj.fCommInterfaceConfs);
  }
  
  public int hashCode() {
    return CodingUtils.generateHashCode(34, this.fType, this.fCommInterfaceConfs);
  }
  
  public String toString() {
    if (this.fType == null) {
      return "No current Communication Interface defined"; //$NON-NLS-1$
    } else {
      return getCurrentCommunicationInterface().toString();
    }
  }
  
  // --- Fields
  
  private final Map<String, AbstractCommunicationInterfaceConfiguration> fCommInterfaceConfs;
  
  private String fType;

}
