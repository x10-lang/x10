/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.rms.provider;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ptp.core.attributes.AttributeManager;
import org.eclipse.ptp.core.elements.attributes.JobAttributes;

import x10dt.ui.launch.rms.core.launch_configuration.LaunchAttributes;
import x10dt.ui.launch.rms.core.provider.AbstractX10RuntimeSystem;
import x10dt.ui.launch.rms.core.provider.AbstractX10RuntimeSystemJob;
import x10dt.ui.launch.rms.core.provider.IX10RMConfiguration;
import x10dt.ui.launch.rms.core.provider.IX10RuntimeSystem;
import x10dt.ui.launch.rms.core.provider.SSHValidationJob;


final class SocketsX10RuntimeSystem extends AbstractX10RuntimeSystem implements IX10RuntimeSystem {

  SocketsX10RuntimeSystem(final int id, final IX10RMConfiguration rmConfig) {
    super(id, rmConfig);
  }

  // --- Abstract methods implementation
  
  protected Job createCheckRequirementsJob(final String hostName, final IProgressMonitor monitor) {
    return new SSHValidationJob(this, hostName, monitor);
  }
  
  protected Job createRuntimeSystemJob(final String jobID, final String queueID, 
                                       final AttributeManager attrMgr) throws CoreException {
    return new SocketsX10RuntimeSystemJob(jobID, queueID, "Sockets Run Job", this, attrMgr); //$NON-NLS-1$
  }
  
  // --- Private classes
  
  private static final class SocketsX10RuntimeSystemJob extends AbstractX10RuntimeSystemJob {

    protected SocketsX10RuntimeSystemJob(final String jobId, final String queueId, final String name, 
                                         final IX10RuntimeSystem runtimeSystem, final AttributeManager attrManager) {
      super(jobId, queueId, name, runtimeSystem, attrManager);
    }
    
    // --- Abstract methods implementation 
    
    protected void completeEnvironmentVariables(final Map<String, String> envMap) {
      final Integer procs = getAttrManager().getAttribute(JobAttributes.getNumberOfProcessesAttributeDefinition()).getValue();
      envMap.put("X10_NPLACES", String.valueOf(procs)); //$NON-NLS-1$
      final String hostFile = getAttrManager().getAttribute(LaunchAttributes.getHostFileAttr()).getValue();
      if (hostFile.length() == 0) {
        final List<String> hostList = getAttrManager().getAttribute(LaunchAttributes.getHostListAttr()).getValue();
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        for (final String hostName : hostList) {
          if (i == 1) {
            sb.append(',');
          } else {
            i = 1;
          }
          sb.append(hostName);
        }
        envMap.put("X10_HOSTLIST", sb.toString()); //$NON-NLS-1$
      } else {
        envMap.put("X10_HOSTFILE", hostFile); //$NON-NLS-1$
      }
    }

  }

}
