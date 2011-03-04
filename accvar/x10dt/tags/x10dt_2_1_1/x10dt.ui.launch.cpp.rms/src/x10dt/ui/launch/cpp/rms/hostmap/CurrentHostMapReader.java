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
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteProcess;
import org.eclipse.ptp.remote.core.IRemoteProcessBuilder;
import org.eclipse.ptp.remote.core.IRemoteServices;

import x10dt.ui.launch.cpp.rms.provider.IX10RuntimeSystem;


final class CurrentHostMapReader implements IHostMapReader {

  // --- Interface methods implementation
  
  public HostMap loadMap(final IX10RuntimeSystem runtimeSystem, final IRemoteConnection connection, 
                         final IRemoteServices remoteServices, final String machineId, 
                         final IProgressMonitor monitor) {
    try {
      final String hostName = getHostname(connection, remoteServices);
      if (hostName == null) {
        return new HostMap();
      } else {
        runtimeSystem.createNode(machineId, hostName, 0);
        return new HostMap(hostName);
      }
    } catch (IOException except) {
      // Simply forgets.
    }
    return new HostMap();
  }
  
   // --- Private code
  
  private String getHostname(final IRemoteConnection connection, final IRemoteServices remoteServices) throws IOException {
    final IRemoteProcessBuilder processBuilder = remoteServices.getProcessBuilder(connection, "hostname"); //$NON-NLS-1$
    final IRemoteProcess process = processBuilder.start();
    final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    try {
      process.waitFor();
    } catch (InterruptedException except) {
      // Simply ignore
    }
    return (process.exitValue() == 0) ? reader.readLine() : null;
  }

}
