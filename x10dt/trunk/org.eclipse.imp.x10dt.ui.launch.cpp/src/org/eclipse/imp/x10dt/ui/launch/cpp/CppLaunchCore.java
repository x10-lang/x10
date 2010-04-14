/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IResourceUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;
import org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf.X10PlatformConfFactory;
import org.eclipse.imp.x10dt.ui.launch.cpp.utils.PTPConfUtils;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes.State;
import org.eclipse.ptp.remote.core.exception.RemoteConnectionException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * Controls the plug-in life cycle for this project and provides logging services.
 * 
 * @author egeay
 */
public class CppLaunchCore extends AbstractUIPlugin implements IResourceChangeListener, ISaveParticipant {
  
  /**
   * Unique id for this plugin.
   */
  public static final String PLUGIN_ID = "org.eclipse.imp.x10dt.ui.launch.cpp"; //$NON-NLS-1$
  
  /**
   * Unique id for the C++ Builder.
   */
  public static final String BUILDER_ID = PLUGIN_ID + ".X10CppBuilder"; //$NON-NLS-1$
  
  /**
   * Id for the X10DT C++ Project Nature.
   */
  public static final String X10_CPP_PRJ_NATURE_ID = PLUGIN_ID + ".x10nature"; //$NON-NLS-1$
  
  // --- IResourceChangeListener's interface methods implementation
  
  public void resourceChanged(final IResourceChangeEvent event) {
    if ((event.getType() != IResourceChangeEvent.POST_CHANGE) && (event.getType() != IResourceChangeEvent.POST_BUILD)) {
      return;
    }
    final IResourceDelta rootResourceDelta = event.getDelta();
    for (final IResourceDelta resourceDelta : rootResourceDelta.getAffectedChildren()) {
      if (resourceDelta.getResource().getType() == IResource.PROJECT) {
        final IProject project = resourceDelta.getResource().getProject();
        switch (resourceDelta.getKind()) {
          case IResourceDelta.ADDED: {
          	final IFile platformConfFile = X10PlatformConfFactory.getFile(project);
          	if (platformConfFile.exists()) {
          		final IX10PlatformConf platformConf = X10PlatformConfFactory.load(platformConfFile);
          		this.fProjectToPlatform.put(project, platformConf);
          		startResourceManager(project, platformConf);
          	}
            break;
          }
          case IResourceDelta.REMOVED:
            this.fProjectToPlatform.remove(project);
            break;
          case IResourceDelta.CHANGED: {
            final IPath path = X10PlatformConfFactory.getFile(resourceDelta.getResource().getProject()).getFullPath();
            final IResourceDelta platformConfDelta = rootResourceDelta.findMember(path);
            if ((platformConfDelta != null) && (platformConfDelta.getFlags() != IResourceDelta.MARKERS)) {
            	final IFile platformConfFile = (IFile) platformConfDelta.getResource();
            	final IX10PlatformConf platformConf = X10PlatformConfFactory.load(platformConfFile);
            	this.fProjectToPlatform.put(project, platformConf);
            	startResourceManager(project, platformConf);
            }
            break;
          }
          case IResourceDelta.ADDED_PHANTOM:
          case IResourceDelta.REMOVED_PHANTOM:
        }
      }
    }
  }
  
  // --- ISaveParticipant's interface methods implementation
  
  public void doneSaving(final ISaveContext context) {
  }

  public void prepareToSave(final ISaveContext context) throws CoreException {
  }

  public void rollback(final ISaveContext context) {
  }

  public void saving(final ISaveContext context) throws CoreException {
    // No state to be saved by the plug-in, but request a resource delta to be used on next activation.
    context.needDelta();
  }
  
  // --- Public services
  
  /**
   * Returns the current plugin instance.
   * 
   * @return A non-null value if the plugin is activated, or <b>null</b> if it is stopped.
   */
  public static CppLaunchCore getInstance() {
    return fPlugin;
  }
  
  /**
   * Returns the X10 platform configuration for the given project. The given project need to have a C++ X10 nature in order
   * to return a non-null value.
   * 
   * @param project The project for which one wants to access the X10 platform configuration.
   * @return A non-null platform configuration instance.
   */
  public IX10PlatformConf getPlatformConfiguration(final IProject project) {
    final IX10PlatformConf platformConf = this.fProjectToPlatform.get(project);
    if (platformConf == null) {
    	final IX10PlatformConf newPConf = X10PlatformConfFactory.load(project);
    	this.fProjectToPlatform.put(project, newPConf);
    	return newPConf;
    } else {
    	return platformConf;
    }
  }
  
  /**
   * Logs (if the plugin is active) the operation outcome provided through the status instance.
   * 
   * @param status The status to log.
   */
  public static final void log(final IStatus status) {
    if (fPlugin != null) {
      fPlugin.getLog().log(status);
    }
  }
  
  /**
   * Logs (if the plugin is active) the outcome of an operation with the parameters provided.
   * 
   * @param severity The severity of the message. The code is one of {@link IStatus#OK}, {@link IStatus#ERROR}, 
   * {@link IStatus#INFO}, {@link IStatus#WARNING} or {@link IStatus#CANCEL}.
   * @param code The plug-in-specific status code, or {@link IStatus#OK}.
   * @param message The human-readable message, localized to the current locale.
   * @param exception The exception to log, or <b>null</b> if not applicable.
   */
  public static final void log(final int severity, final int code, final String message, final Throwable exception) {
    if (fPlugin != null) {
      fPlugin.getLog().log(new Status(severity, fPlugin.getBundle().getSymbolicName(), code, message, exception));
    }
  }
  
  /**
   * Logs (if the plugin is active) the outcome of an operation with the parameters provided.
   * 
   * <p>Similar to {@link #log(int, int, String, Throwable)} with <code>code = IStatus.OK</code>.
   * 
   * @param severity The severity of the message. The code is one of {@link IStatus#OK}, {@link IStatus#ERROR}, 
   * {@link IStatus#INFO}, {@link IStatus#WARNING} or {@link IStatus#CANCEL}.
   * @param message The human-readable message, localized to the current locale.
   * @param exception The exception to log, or <b>null</b> if not applicable.
   */
  public static final void log(final int severity, final String message, final Throwable exception) {
    if (fPlugin != null) {
      fPlugin.getLog().log(new Status(severity, fPlugin.getBundle().getSymbolicName(), message, exception));
    }
  }
  
  /**
   * Logs (if the plugin is active) the outcome of an operation with the parameters provided.
   * 
   * <p>Similar to {@link #log(int, int, String, Throwable)} with <code>code = IStatus.OK</code> and 
   * <code>exception = null</code>.
   * 
   * @param severity The severity of the message. The code is one of {@link IStatus#OK}, {@link IStatus#ERROR}, 
   * {@link IStatus#INFO}, {@link IStatus#WARNING} or {@link IStatus#CANCEL}.
   * @param message The human-readable message, localized to the current locale.
   */
  public static final void log(final int severity, final String message) {
    if (fPlugin != null) {
      fPlugin.getLog().log(new Status(severity, fPlugin.getBundle().getSymbolicName(), message));
    }
  }
  
  //--- Overridden methods

  public void start(final BundleContext context) throws Exception {
    super.start(context);
    
    initPlatformConfMap();
    ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    final ISavedState lastState = ResourcesPlugin.getWorkspace().addSaveParticipant(this, this);
    if (lastState != null) {
      lastState.processResourceChangeEvents(this);
    }
    fPlugin = this;
  }

  public void stop(final BundleContext context) throws Exception {
    fPlugin = null;
    ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    ResourcesPlugin.getWorkspace().removeSaveParticipant(this);
    this.fProjectToPlatform.clear();
    this.fProjectToPlatform = null;
    
    super.stop(context);
  }
  
  // --- Private code
  
  private void initPlatformConfMap() {
    final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
    this.fProjectToPlatform = new HashMap<IProject, IX10PlatformConf>(projects.length);
    for (final IProject curProject : projects) {
      try {
        if (curProject.isOpen() && curProject.hasNature(X10_CPP_PRJ_NATURE_ID)) {
          this.fProjectToPlatform.put(curProject, X10PlatformConfFactory.load(curProject));
        }
      } catch (CoreException except) {
        CppLaunchCore.log(except.getStatus());
      }
    }
  }
  
  private void startResourceManager(final IProject project, final IX10PlatformConf platformConf) {
  	final WorkspaceJob job = new WorkspaceJob(LaunchMessages.CLC_CheckConnAndCommJobMsg) {
			
			public IStatus runInWorkspace(final IProgressMonitor monitor) {
				IResourceManager resourceManager = PTPConfUtils.findResourceManager(platformConf.getName());
				try {
					if (resourceManager == null) {
						resourceManager = PTPConfUtils.createResourceManager(platformConf);
					}
				} catch (RemoteConnectionException except) {
					return Status.CANCEL_STATUS;
				}
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				if (resourceManager.getState() == State.ERROR) {
					try {
						resourceManager.shutdown();
					} catch (CoreException except) {
						// Let's try to continue...
					}
				}
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				if (resourceManager.getState() != State.STARTED) {
					try {
						resourceManager.startUp(monitor);
					} catch (CoreException except) {
						safeRunForMarkerAdding(X10PlatformConfFactory.getFile(project), monitor, new IWorkspaceRunnable() {

							public void run(final IProgressMonitor localMonitor) throws CoreException {
								IResourceUtils.addPlatformConfMarker(X10PlatformConfFactory.getFile(project),
								                                     LaunchMessages.XPCFE_DiscoveryCmdFailedMarkerMsg,
								                                     IMarker.SEVERITY_ERROR, IMarker.PRIORITY_HIGH);
							}
						
						});
						return Status.CANCEL_STATUS;
					}
				}
				return Status.OK_STATUS;
			}
			
		};
		job.setRule(X10PlatformConfFactory.getFile(project));
		job.setPriority(Job.LONG);
		job.schedule();
	}
  
  // --- Private code
  
  private void safeRunForMarkerAdding(final IResource resource, final IProgressMonitor monitor,
                                      final IWorkspaceRunnable runnable) {
  	new Thread(new Runnable() {
			
			public void run() {
		  	final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		  	while (workspace.isTreeLocked()) ;
		  	if (! monitor.isCanceled()) {
		  		final IResourceRuleFactory ruleFactory = workspace.getRuleFactory();
		  		try {
		  			workspace.run(runnable, ruleFactory.markerRule(resource), IWorkspace.AVOID_UPDATE, monitor);
		  		} catch (CoreException except) {
		  			CppLaunchCore.log(except.getStatus());
		  		}
		  	}
			}
			
		}).start();
  }
  
  // --- Fields
  
  private static CppLaunchCore fPlugin;
  
  private Map<IProject, IX10PlatformConf> fProjectToPlatform;

}
