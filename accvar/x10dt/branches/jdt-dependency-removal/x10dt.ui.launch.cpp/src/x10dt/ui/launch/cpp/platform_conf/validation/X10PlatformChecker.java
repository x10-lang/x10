/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp.platform_conf.validation;

import static x10dt.ui.launch.core.utils.PTPConstants.LOCAL_CONN_SERVICE_ID;
import static x10dt.ui.launch.core.utils.PTPConstants.REMOTE_CONN_SERVICE_ID;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes.State;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.remote.core.exception.RemoteConnectionException;
import org.eclipse.ptp.remotetools.environment.control.ITargetStatus;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import x10dt.ui.launch.core.Constants;
import x10dt.ui.launch.core.Messages;
import x10dt.ui.launch.core.platform_conf.EValidationStatus;
import x10dt.ui.launch.core.utils.StringUtils;
import x10dt.ui.launch.core.utils.TimerThread;
import x10dt.ui.launch.cpp.LaunchMessages;
import x10dt.ui.launch.cpp.platform_conf.IConnectionConf;
import x10dt.ui.launch.cpp.platform_conf.ICppCompilationConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import x10dt.ui.launch.cpp.utils.PTPConfUtils;


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
  
  public void validateCommunicationInterface(final IX10PlatformConf platformConf, final IProgressMonitor monitor) {
  	IResourceManager resourceManager = null;
  	try {
  		resourceManager = PTPConfUtils.getResourceManager(platformConf);
  	} catch (RemoteConnectionException except) {
  		for (final IX10PlatformValidationListener listener : this.fListeners) {
  			listener.remoteConnectionFailure(except);
  		}
  		monitor.done();
  		return;
  	} catch (CoreException except) {
  		for (final IX10PlatformValidationListener listener : this.fListeners) {
  			listener.serviceProviderFailure(except);
  		}
  		monitor.done();
  		return;
		}
  	if (monitor.isCanceled()) {
  		return;
  	}
  	final String rmServicesId = ((IResourceManagerControl) resourceManager).getConfiguration().getRemoteServicesId();
  	final IRemoteServices rmServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmServicesId);
  	final String connectionName = platformConf.getConnectionConf().getConnectionName();
  	final IRemoteConnection rmConnection = rmServices.getConnectionManager().getConnection(connectionName);
  	try {
			rmConnection.open(monitor);
		} catch (RemoteConnectionException except) {
			for (final IX10PlatformValidationListener listener : this.fListeners) {
  			listener.remoteConnectionFailure(except);
  		}
  		monitor.done();
  		return;
		}
  	if (monitor.isCanceled()) {
  		return;
  	}
  	try {
  	  resourceManager.shutdown();
  	} catch (CoreException except) {
  	  for (final IX10PlatformValidationListener listener : this.fListeners) {
  	    listener.platformCommunicationInterfaceValidationFailure(except.getMessage());
  	  }
  	  return;
		}
  	if (monitor.isCanceled()) {
  		return;
  	}
  	try {
  	  resourceManager.startUp(monitor);
  	} catch (CoreException except) {
  	  for (final IX10PlatformValidationListener listener : this.fListeners) {
  	    listener.platformCommunicationInterfaceValidationFailure(except.getMessage());
  	  }
  	  return;
  	}
  	
  	// The following code is hack to handle the fact that PE resource manager does not follow the same contract that
  	// OpenMPI for startup. To fix!
  	if (resourceManager.getState() == State.STARTING) {
  	  final IResourceManager curRM = resourceManager;
  	  final TimerThread timerThread = new TimerThread(new Runnable() {
        
        public void run() {
          if (curRM.getState() == State.STARTING) {
            for (final IX10PlatformValidationListener listener : X10PlatformChecker.this.fListeners) {
              listener.platformCommunicationInterfaceValidationFailure(Constants.EMPTY_STR);
            }
          } else {
            for (final IX10PlatformValidationListener listener : X10PlatformChecker.this.fListeners) {
              listener.platformCommunicationInterfaceValidated();
            }
          }
        }
        
      }, 2000);
  	  timerThread.start();
  	  try {
        timerThread.join();
      } catch (InterruptedException except) {
        for (final IX10PlatformValidationListener listener : X10PlatformChecker.this.fListeners) {
          listener.platformCommunicationInterfaceValidationFailure(Constants.EMPTY_STR);
        }
      }
  	} else {
  	  for (final IX10PlatformValidationListener listener : this.fListeners) {
  	    listener.platformCommunicationInterfaceValidated();
  	  }
  	}
  }

  public void validateCppCompilationConf(final IX10PlatformConfWorkCopy platformConf, final IProgressMonitor monitor) {
    final SubMonitor subMonitor = SubMonitor.convert(monitor, 30);
   
    if (this.fRemoteConnection == null) {
      this.fRemoteConnection = getRemoteConnection(platformConf.getConnectionConf());
    } else {
      subMonitor.worked(5);
    }
    
    if ((this.fRemoteConnection != null) && ! monitor.isCanceled()) {
      if (! this.fRemoteConnection.isOpen()) {
        try {
          this.fRemoteConnection.open(subMonitor.newChild(5));
        } catch (RemoteConnectionException except) {
          PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            
            public void run() {
              final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
              MessageDialog.openInformation(shell, LaunchMessages.XPC_RemoteConnRequirementTitle, 
                                            LaunchMessages.XPC_RemoteConnRequirementMsg);
            }
            
          });
          for (final IX10PlatformValidationListener listener : this.fListeners) {
            listener.remoteConnectionFailure(except);
          }
          return;
        }
      }
      
      final String rmServicesId = platformConf.getConnectionConf().isLocal() ? LOCAL_CONN_SERVICE_ID : REMOTE_CONN_SERVICE_ID;
      final ICppCompilationConf cppCompConf = platformConf.getCppCompilationConf();
      final ICppCompilationChecker checker = new CppCompilationChecker(rmServicesId, this.fRemoteConnection, cppCompConf, platformConf.getConfFile().getProject());
      
      try {
        final String returnCompilMsg = checker.validateCompilation(subMonitor.newChild(10));
        if (returnCompilMsg == null) {
          final String returnArchivingMsg = checker.validateArchiving(subMonitor.newChild(3));
          if (returnArchivingMsg == null) {
            final String returnLinkMsg = checker.validateLinking(subMonitor.newChild(7));
            if (returnLinkMsg == null) {
              platformConf.setCppConfValidationStatus(EValidationStatus.VALID);
              for (final IX10PlatformValidationListener listener : this.fListeners) {
                listener.platformCppCompilationValidated();
              }
            } else {
              platformConf.setCppConfValidationStatus(EValidationStatus.FAILURE);
              platformConf.setCppConfValidationErrorMessage(NLS.bind(Messages.DF_LineBreak, 
                                                                     LaunchMessages.XPC_LinkingFailure, returnLinkMsg));
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
          platformConf.setCppConfValidationErrorMessage(NLS.bind(Messages.DF_LineBreak, LaunchMessages.XPC_CompilationFailure,
                                                                 returnCompilMsg));
          for (final IX10PlatformValidationListener listener : this.fListeners) {
            listener.platformCppCompilationValidationFailure(cppCompConf.getValidationErrorMessage());
          }
        }
      } catch (InterruptedException except) {
        // Nothing to do here.
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
