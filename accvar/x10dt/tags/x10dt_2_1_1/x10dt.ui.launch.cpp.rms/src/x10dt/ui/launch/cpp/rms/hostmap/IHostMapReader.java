/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.rms.hostmap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteServices;

import x10dt.ui.launch.cpp.rms.provider.IX10RuntimeSystem;

/**
 * Responsible for loading a host map and creating the relevant node(s) to a given machine.
 * 
 * <p>Use {@link HostMapReaderFactory} class to get implementations of this interface.
 * 
 * @author egeay
 */
public interface IHostMapReader {
  
  /**
   * Loads the host map and returns the list of host names we have managed to find.
   * 
   * <p>As a side effect, the host names found will be added to the given machine.
   * 
   * @param runtimeSystem The current runtime system.
   * @param connection The current remote connection encapsulated in the runtime system.
   * @param remoteServices The remote services instance for the current runtime system.
   * @param machineId The machine id identifying uniquely the machine to consider.
   * @param monitor The monitor to use in order to report progress or cancel the operation.
   * @return The object instance wrapping the host names.
   */
  public HostMap loadMap(final IX10RuntimeSystem runtimeSystem, final IRemoteConnection connection,
                         final IRemoteServices remoteServices, final String machineId, 
                         final IProgressMonitor monitor);

}
