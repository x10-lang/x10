/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.rms.core.hostmap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteServices;

import x10dt.ui.launch.rms.core.provider.IX10RuntimeSystem;


final class CurrentHostMapReader implements IHostMapReader {

  // --- Interface methods implementation
  
  public HostMap loadMap(final IX10RuntimeSystem runtimeSystem, final IRemoteConnection connection, 
                         final IRemoteServices remoteServices, final String machineId, 
                         final IProgressMonitor monitor) {
    return new HostMap("localhost"); //$NON-NLS-1$
  }

}
