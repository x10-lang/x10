/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.validation;

import static org.eclipse.imp.x10dt.ui.launch.core.utils.PTPConstants.LOCAL_CONN_SERVICE_ID;
import static org.eclipse.imp.x10dt.ui.launch.core.utils.PTPConstants.REMOTE_CONN_SERVICE_ID;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.EValidationStatus;
import org.eclipse.imp.x10dt.ui.launch.core.utils.StringUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IConnectionConf;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.ICppCompilationConf;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import org.eclipse.imp.x10dt.ui.launch.cpp.utils.PTPConfUtils;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes.State;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.remote.core.exception.RemoteConnectionException;
import org.eclipse.ptp.remotetools.environment.control.ITargetStatus;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;


final class X10PlatformChecker implements IX10PlatformChecker {
  
  // --- Interface methods implementation
  
  public void addValidationListener(final IX10PlatformValidationListener listener) {
    this.fListeners.add(listener);
  }
  
  public void removeAllValidationListeners() {
    this.fListeners.clear();
  }

  public void removeValidationListener(final IX10PlatformValidationListener listener) {
    this.fListeners.remove(listener);
  }
  
  public void validateCommunicationInterface(final IX10PlatformConfWorkCopy platormConf, final IProgressMonitor monitor) {
  	IResourceManager resourceManager = PTPConfUtils.findResourceManager(platormConf.getName());
  	if (resourceManager == null) {
      try {
        resourceManager = PTPConfUtils.createResourceManager(platormConf);
      } catch (RemoteConnectionException except) {
      	for (final IX10PlatformValidationListener listener : this.fListeners) {
          listener.remoteConnectionFailure(except);
        }
      	monitor.done();
      	return;
      }
  	}
  	if (monitor.isCanceled()) {
  		return;
  	}
  	if (resourceManager.getState() == State.ERROR) {
			try {
				resourceManager.shutdown();
			} catch (CoreException except) {
				for (final IX10PlatformValidationListener listener : this.fListeners) {
		  		listener.platformCommunicationInterfaceValidationFailure(except.getMessage());
		  	}
				return;
			}
		}
  	if (monitor.isCanceled()) {
  		return;
  	}
  	if (resourceManager.getState() != State.STARTED) {
			try {
				resourceManager.startUp(monitor);
			} catch (CoreException except) {
				for (final IX10PlatformValidationListener listener : this.fListeners) {
		  		listener.platformCommunicationInterfaceValidationFailure(except.getMessage());
		  	}
				return;
			}
  	}
  	for (final IX10PlatformValidationListener listener : this.fListeners) {
  		listener.platformCommunicationInterfaceValidated();
  	}
  }

  public void validateCppCompilationConf(final IX10PlatformConfWorkCopy platformConf, final IProgressMonitor monitor) {
    final SubMonitor subMonitor = SubMonitor.convert(monitor, 25);
   
    if (this.fRemoteConnection == null) {
      this.fRemoteConnection = getRemoteConnection(platformConf.getConnectionConf());
    } else {
      subMonitor.worked(5);
    }
    
    if ((this.fRemoteConnection != null) && ! monitor.isCanceled()) {
      final String rmServicesId = platformConf.getConnectionConf().isLocal() ? LOCAL_CONN_SERVICE_ID : REMOTE_CONN_SERVICE_ID;
      final ICppCompilationChecker checker = new CppCompilationChecker(rmServicesId, this.fRemoteConnection);
      
      final ICppCompilationConf cppCompConf = platformConf.getCppCompilationConf();
      
      try {
        final String returnCompilMsg = checker.validateCompilation(cppCompConf.getCompiler(), 
                                                                   cppCompConf.getCompilingOpts(true), 
                                                                   cppCompConf.getX10DistribLocation(), 
                                                                   cppCompConf.getPGASLocation(),
                                                                   cppCompConf.getX10HeadersLocations(), 
                                                                   cppCompConf.getX10LibsLocations(), subMonitor.newChild(10));
        if (returnCompilMsg == null) {
          final String returnArchivingMsg = checker.validateArchiving(cppCompConf.getArchiver(), 
                                                                      cppCompConf.getArchivingOpts(true), 
                                                                      subMonitor.newChild(3));
          if (returnArchivingMsg == null) {
            final String returnLinkMsg = checker.validateLinking(cppCompConf.getLinker(), cppCompConf.getLinkingOpts(true), 
                                                                 cppCompConf.getLinkingLibs(true), 
                                                                 cppCompConf.getX10HeadersLocations(),
                                                                 cppCompConf.getX10LibsLocations(), subMonitor.newChild(7));
            if (returnLinkMsg == null) {
              platformConf.setCppConfValidationStatus(EValidationStatus.VALID);
              for (final IX10PlatformValidationListener listener : this.fListeners) {
                listener.platformCppCompilationValidated();
              }
            } else {
              platformConf.setCppConfValidationStatus(EValidationStatus.FAILURE);
              platformConf.setCppConfValidationErrorMessage(NLS.bind(Messages.DF_LineBreak, 
                                                                     LaunchMessages.XPC_CompilationFailure, returnLinkMsg));
              for (final IX10PlatformValidationListener listener : this.fListeners) {
                listener.platformCppCompilationValidationFailure(cppCompConf.getValidationErrorMessage());
              }
            }
          } else {
            platformConf.setCppConfValidationStatus(EValidationStatus.FAILURE);
            platformConf.setCppConfValidationErrorMessage(NLS.bind(Messages.DF_LineBreak, LaunchMessages.XPC_ArchivingFailure, 
                                                                   returnArchivingMsg));
            for (final IX10PlatformValidationListener listener : this.fListeners) {
              listener.platformCppCompilationValidationFailure(cppCompConf.getValidationErrorMessage());
            }
          }
        } else {
          platformConf.setCppConfValidationStatus(EValidationStatus.FAILURE);
          platformConf.setCppConfValidationErrorMessage(NLS.bind(Messages.DF_LineBreak, LaunchMessages.XPC_LinkingFailure, 
                                                                 returnCompilMsg));
          for (final IX10PlatformValidationListener listener : this.fListeners) {
            listener.platformCppCompilationValidationFailure(cppCompConf.getValidationErrorMessage());
          }
        }
      } catch (Exception except) {
        platformConf.setCppConfValidationStatus(EValidationStatus.ERROR);
        platformConf.setCppConfValidationErrorMessage(StringUtils.getStackTrace(except));
        
        for (final IX10PlatformValidationListener listener : this.fListeners) {
          listener.platformCppCompilationValidationError(except);
        }
      } finally {
        monitor.done();
      }
    }
  }
  
  public void validateRemoteConnectionConf(final ITargetElement targetElement, final IProgressMonitor monitor) {
    try {
      if (targetElement.getControl().query() != ITargetStatus.STOPPED) {
        targetElement.getControl().kill(new NullProgressMonitor());
      }
      targetElement.getControl().create(monitor);
      for (final IX10PlatformValidationListener listener : this.fListeners) {
        listener.remoteConnectionValidated(targetElement);
      }
      
      final IRemoteServices remoteServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(REMOTE_CONN_SERVICE_ID);
      for (final IRemoteConnection rmConnection : remoteServices.getConnectionManager().getConnections()) {
        if (rmConnection.getName().equals(targetElement.getName())) {
          this.fRemoteConnection = rmConnection;
          break;
        }
      }
    } catch (Exception except) {
      for (final IX10PlatformValidationListener listener : this.fListeners) {
        listener.remoteConnectionFailure(except);
      }
    }
  }
  
  // --- Private code
  
  private IRemoteConnection getRemoteConnection(final IConnectionConf connectionConf) {
    if (connectionConf.isLocal()) {
      final IRemoteServices remoteServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(LOCAL_CONN_SERVICE_ID);
      return remoteServices.getConnectionManager().getConnection((String) null);
    } else {
      final IRemoteServices remoteServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(REMOTE_CONN_SERVICE_ID);
      for (final IRemoteConnection rmConnection : remoteServices.getConnectionManager().getConnections()) {
        if (rmConnection.getName().equals(connectionConf.getConnectionName())) {
          return rmConnection;
        }
      }
      return null;
    }
  }
  
  // --- Fields
  
  private final Collection<IX10PlatformValidationListener> fListeners = new ArrayList<IX10PlatformValidationListener>();
  
  private IRemoteConnection fRemoteConnection;

}
