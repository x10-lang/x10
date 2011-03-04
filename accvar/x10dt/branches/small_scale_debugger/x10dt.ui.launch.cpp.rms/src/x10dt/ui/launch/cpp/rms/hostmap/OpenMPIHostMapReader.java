/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.rms.hostmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ptp.core.attributes.AttributeManager;
import org.eclipse.ptp.core.attributes.IllegalValueException;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.ptp.remote.core.IRemoteProcess;
import org.eclipse.ptp.remote.core.IRemoteProcessBuilder;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.rm.mpi.openmpi.core.OpenMPINodeAttributes;
import org.eclipse.ptp.rm.mpi.openmpi.core.parameters.OmpiInfo;
import org.eclipse.ptp.rm.mpi.openmpi.core.parameters.Parameters;
import org.eclipse.ptp.rm.mpi.openmpi.core.rtsystem.OpenMPIHostMap;
import org.eclipse.ptp.rm.mpi.openmpi.core.rtsystem.OpenMPIHostMapParser;

import x10dt.ui.launch.cpp.rms.provider.IX10RuntimeSystem;


final class OpenMPIHostMapReader implements IHostMapReader {

  // --- Interface methods implementation
  
  public HostMap loadMap(final IX10RuntimeSystem runtimeSystem, final IRemoteConnection connection, 
                         final IRemoteServices remoteServices, final String machineId, 
                         final IProgressMonitor monitor) {
    final OmpiInfo ompiInfo = new OmpiInfo();
    
    final List<String> command = Arrays.asList("ompi_info", "-a", "--parseable"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    final IRemoteProcessBuilder cmdBuilder = runtimeSystem.createProcessBuilder(command, null /* workingDirectory */);
    try {
      final IRemoteProcess process = cmdBuilder.start();
      final BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
      
      try {
        parseOmpiInfo(stdout, ompiInfo);
      
        final OpenMPIHostMap hostMap = readHostFile(connection, remoteServices, ompiInfo);
        if ((hostMap == null) || (hostMap.count() == 0)) {
          return new HostMap();
        } else {
          final List<String> hostNames = new ArrayList<String>();
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
  
  // --- Private
  
  private void parseOmpiInfo(final BufferedReader output, final OmpiInfo info) throws IOException {
    String line;
    while ((line = output.readLine()) != null) {
      if (line.indexOf("mca:") == 0) { //$NON-NLS-1$
        int nameStart = line.indexOf(":param:"); //$NON-NLS-1$
        if (nameStart >= 0) {
          nameStart += 7;
          int pos = line.indexOf(':', nameStart);
          if (pos >= 0) {
            // If parameter is already in list, then update, otherwise add.
            String name = line.substring(nameStart, pos);
            Parameters.Parameter param = info.getParameter(name);
            if (param == null) {
              param = info.addParameter(name);
            }
            int pos2;
            if ((pos2 = line.indexOf(":value:", pos)) >= 0) { //$NON-NLS-1$
              param.setValue(line.substring(pos2 + 7));
            } else if ((pos2 = line.indexOf(":status:", pos)) >= 0) { //$NON-NLS-1$
              if (line.substring(pos2 + 8).equals("read-only")) { //$NON-NLS-1$
                param.setReadOnly(true);
              }
            } else if ((pos2 = line.indexOf(":help:", pos)) >= 0) { //$NON-NLS-1$
              param.setHelp(line.substring(pos2 + 6));
            }
          }
        }
      } else {
        int valStart = line.lastIndexOf(':'); // will fail if value contains a colon!
        if (valStart >= 0) {
          info.add(line.substring(0, valStart), line.substring(valStart + 1));
        }
      }
    }
  }
  
  private void processHostMap(final OpenMPIHostMap hostMap, final IX10RuntimeSystem runtimeSystem,
                              final String machineId, final Collection<String> hostNames) {
    int nodeCounter = 0;
    if (! hostMap.hasParseErrors()) {
      for (final OpenMPIHostMap.Host host : hostMap.getHosts()) {
        final String nodeId = runtimeSystem.createNode(machineId, host.getName(), nodeCounter++);
        if (host.getErrors() == 0) {
          hostNames.add(host.getName());
          final AttributeManager attrManager = new AttributeManager();
          if (host.getNumProcessors() != 0) {
            try {
              attrManager.addAttribute(OpenMPINodeAttributes.getNumberOfNodesAttributeDefinition()
                                                            .create(Integer.valueOf(host.getNumProcessors())));
            } catch (IllegalValueException except) {
              assert false;
            }
          }
          if (host.getMaxNumProcessors() != 0) {
            try {
              attrManager.addAttribute(OpenMPINodeAttributes.getMaximalNumberOfNodesAttributeDefinition()
                                                            .create(Integer.valueOf(host.getMaxNumProcessors())));
            } catch (IllegalValueException except) {
              assert false;
            }
          }
          runtimeSystem.changeNode(nodeId, attrManager);
        }
      }
    }
  }
  
  private OpenMPIHostMap readHostFile(final IRemoteConnection connection, final IRemoteServices remoteServices,
                                      final OmpiInfo info) throws CoreException, IOException {
    // OpenMpi 1.2 uses rds_hostfile_path. Open 1.3 uses orte_default_hostfile. For 1.2, path must not be empty. 
    // For 1.3 it may be empty and default host is assumed.
    final Parameters.Parameter rds_param = info.getParameter("rds_hostfile_path"); //$NON-NLS-1$
    final Parameters.Parameter orte_param = info.getParameter("orte_default_hostfile"); //$NON-NLS-1$

    String hostFileName = null;
    IPath hostFilePath = null;
    if (rds_param != null) {
      hostFileName = rds_param.getValue();
      if (hostFileName.trim().length() != 0) {
        hostFilePath = new Path(hostFileName);
      }
    }

    if (hostFilePath == null && orte_param != null) {
      hostFileName = orte_param.getValue();
      if (hostFileName.trim().length() != 0) {
        hostFilePath = new Path(hostFileName);
      }
    }

    if ((hostFilePath != null) && hostFilePath.isAbsolute()) {
      final IRemoteFileManager fileManager = remoteServices.getFileManager(connection);
      final IFileStore hostFile = fileManager.getResource(hostFilePath.toString());

      final InputStream is = hostFile.openInputStream(EFS.NONE, new NullProgressMonitor());
      final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      return OpenMPIHostMapParser.parse(reader);
    }
    
    return null;
  }

}
