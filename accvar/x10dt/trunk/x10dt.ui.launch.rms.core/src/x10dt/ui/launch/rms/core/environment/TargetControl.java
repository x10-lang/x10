/**
 * Copyright (c) 2006 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial Implementation
 *
 */
package x10dt.ui.launch.rms.core.environment;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ptp.remotetools.core.IAuthInfo;
import org.eclipse.ptp.remotetools.core.IRemoteExecutionManager;
import org.eclipse.ptp.remotetools.environment.control.ITargetConfig;
import org.eclipse.ptp.remotetools.environment.control.ITargetControl;
import org.eclipse.ptp.remotetools.environment.control.ITargetStatus;
import org.eclipse.ptp.remotetools.environment.control.SSHTargetControl;
import org.eclipse.ptp.remotetools.exception.RemoteConnectionException;

import x10dt.ui.launch.rms.core.Messages;
import x10dt.ui.launch.rms.core.RMSCoreActivator;

final class TargetControl extends SSHTargetControl implements ITargetControl {

  TargetControl(final ITargetConfig config, final IAuthInfo authInfo) {
    this.fState = NOT_OPERATIONAL;
    this.fTargetConfig = config;
    this.fAuthInfo = authInfo;
  }

  // --- ITargetControl's interface methods implementation

  public IRemoteExecutionManager createExecutionManager() throws RemoteConnectionException {
    if (! isConnected()) {
      throw new RemoteConnectionException(Messages.TC_ConnNotOpen);
    }
    return super.createRemoteExecutionManager();
  }

  public void destroy() throws CoreException {
    try {
      terminateJobs(null);
    } finally {
      disconnect();
    }
  }

  public ITargetConfig getConfig() {
    return this.fTargetConfig;
  }

  public IRemoteExecutionManager getExecutionManager() {
    return this.fExecutionManager;
  }

  public synchronized int query() {
    switch (this.fState) {
    case NOT_OPERATIONAL:
      return ITargetStatus.STOPPED;
    case CONNECTING:
    case DISCONNECTING:
      return ITargetStatus.STARTED;
    case CONNECTED:
      if (isConnected()) {
        return ITargetStatus.RESUMED;
      } else {
        return ITargetStatus.STARTED;
      }
    default:
      return ITargetStatus.STOPPED;
    }
  }

  public boolean resume(final IProgressMonitor monitor) throws CoreException {
    throw new CoreException(new Status(IStatus.ERROR, RMSCoreActivator.PLUGIN_ID, Messages.TC_NoResumeFromPause, null));
  }

  public boolean stop(final IProgressMonitor monitor) throws CoreException {
    throw new CoreException(new Status(IStatus.ERROR, RMSCoreActivator.PLUGIN_ID, Messages.TC_NoPauseAction, null));
  }

  public void updateConfiguration() throws CoreException {
    // Nothing required
  }
  
  // --- Overridden methods
  
  public boolean create(IProgressMonitor monitor) throws CoreException {
    monitor.beginTask(Messages.TC_Connecting, 1);

    setConnectionParameters(this.fTargetConfig, this.fAuthInfo);

    try {
      setState(CONNECTING);

      super.create(monitor);

      if (monitor.isCanceled()) {
        disconnect();
        setState(NOT_OPERATIONAL);
        monitor.done();
        return true;
      }

      setState(CONNECTED);

      monitor.worked(1);
    } catch (CoreException except) {
      disconnect();
      setState(NOT_OPERATIONAL);
      monitor.done();
      throw except;
    }
    try {
      this.fExecutionManager = super.createRemoteExecutionManager();
    } catch (RemoteConnectionException except) {
      disconnect();
      setState(NOT_OPERATIONAL);
      throw new CoreException(new Status(IStatus.ERROR, RMSCoreActivator.PLUGIN_ID, except.getMessage()));
    }
    monitor.done();
    return true;
  }
  
  public TargetSocket createTargetSocket(final int port) {
    Assert.isTrue(isConnected());
    final TargetSocket socket = new TargetSocket();
    socket.host = this.fTargetConfig.getConnectionAddress();
    socket.port = port;
    return socket;
  }

  public boolean kill(final IProgressMonitor monitor) throws CoreException {
    try {
      setState(DISCONNECTING);
      super.kill(monitor);
    } finally {
      setState(NOT_OPERATIONAL);
    }
    return true;
  }
  
  // --- Private code

  private synchronized void setState(final int state) {
    this.fState = state;
  }
  
  // --- Fields
  
  private final ITargetConfig fTargetConfig;
  
  private final IAuthInfo fAuthInfo;

  private IRemoteExecutionManager fExecutionManager;

  private int fState;
  
  

  private static final int NOT_OPERATIONAL = 1;

  private static final int CONNECTING = 2;

  private static final int CONNECTED = 3;

  private static final int DISCONNECTING = 4;

}
