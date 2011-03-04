/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.rms.hostmap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ptp.core.attributes.AttributeManager;
import org.eclipse.ptp.core.attributes.IllegalValueException;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteProcess;
import org.eclipse.ptp.remote.core.IRemoteProcessBuilder;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.rm.mpi.mpich2.core.MPICH2NodeAttributes;
import org.eclipse.ptp.rm.mpi.mpich2.core.rtsystem.MPICH2HostMap;
import org.eclipse.ptp.rm.mpi.mpich2.core.rtsystem.MPICH2TraceParser;

import x10dt.ui.launch.cpp.rms.provider.IX10RuntimeSystem;


final class MPICH2HostMapReader implements IHostMapReader {

  // --- Interface methods implementation
  
  public HostMap loadMap(final IX10RuntimeSystem runtimeSystem, final IRemoteConnection connection, 
                         final IRemoteServices remoteServices, final String machineId, 
                         final IProgressMonitor monitor) {
    final List<String> command = Arrays.asList("mpdtrace", "-l"); //$NON-NLS-1$ //$NON-NLS-2$
    final IRemoteProcessBuilder cmdBuilder = runtimeSystem.createProcessBuilder(command, null /* workingDirectory */);
    try {
      final IRemoteProcess process = cmdBuilder.start();
      final BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
      
      final MPICH2TraceParser parser = new MPICH2TraceParser();
      try {
        final MPICH2HostMap hostMap = parser.parse(stdout);
        if (hostMap == null) {
          return new HostMap();
        } else {
          final Collection<String> hostNames = new ArrayList<String>();
          processHostMap(hostMap, runtimeSystem, machineId, hostNames);
          return new HostMap(hostNames);
        }
      } finally {
        process.waitFor();
      }
    } catch (Exception except) {
      // Simply forgets
    }
    return new HostMap();
  }
  
  // --- Private code
  
  private void processHostMap(final MPICH2HostMap hostMap, final IX10RuntimeSystem runtimeSystem,
                              final String machineId, final Collection<String> hostNames) {
    int nodeCounter = 0;
    for (final MPICH2HostMap.Host host : hostMap.getHosts()) {
      final String nodeId = runtimeSystem.createNode(machineId, host.getName(), nodeCounter++);
      hostNames.add(host.getName());
      
      final AttributeManager attrManager = new AttributeManager();
      if (host.getNumProcessors() != 0) {
        try {
          attrManager.addAttribute(MPICH2NodeAttributes.getNumberOfNodesAttributeDefinition()
                                                       .create(Integer.valueOf(host.getNumProcessors())));
        } catch (IllegalValueException except) {
          assert false;
        }
      }
      runtimeSystem.changeNode(nodeId, attrManager);
    }
  }

}
