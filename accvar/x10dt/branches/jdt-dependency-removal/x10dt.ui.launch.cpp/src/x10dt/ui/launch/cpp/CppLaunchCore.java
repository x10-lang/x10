/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launch.cpp;

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
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.remotetools.environment.core.ITargetElement;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import x10dt.core.X10DTCorePlugin;
import x10dt.core.preferences.generated.X10Constants;
import x10dt.ui.launch.core.utils.CoreResourceUtils;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConf;
import x10dt.ui.launch.cpp.platform_conf.IX10PlatformConfWorkCopy;
import x10dt.ui.launch.cpp.platform_conf.X10PlatformConfFactory;
import x10dt.ui.launch.cpp.platform_conf.validation.IX10PlatformChecker;
import x10dt.ui.launch.cpp.platform_conf.validation.IX10PlatformValidationListener;
import x10dt.ui.launch.cpp.platform_conf.validation.PlatformCheckerFactory;
import x10dt.ui.launch.cpp.utils.PTPConfUtils;

/**
 * Controls the plug-in life cycle for this project and provides logging services.
 * 
 * @author egeay
 */
public class CppLaunchCore extends AbstractUIPlugin implements IResourceChangeListener, ISaveParticipant, 
                                                               IPreferenceChangeListener {

  /**
   * Unique id for this plugin.
   */
  public static final String PLUGIN_ID = "x10dt.ui.launch.cpp"; //$NON-NLS-1$

  /**
   * Unique id for the C++ Builder.
   */
  public static final String BUILDER_ID = PLUGIN_ID + ".X10CppBuilder"; //$NON-NLS-1$

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
            if (platformConf.isComplete(false)) {
              startResourceManager(project, platformConf);
            }
          }
          break;
        }
        case IResourceDelta.REMOVED: {
          final IX10PlatformConf platformConf = this.fProjectToPlatform.remove(project);
          if (platformConf != null) {
            try {
              PTPConfUtils.deleteResourceManager(platformConf);
            } catch (CoreException except) {
              log(IStatus.ERROR, NLS.bind(LaunchMessages.CLC_RMShutdownError, project.getName()), except);
            }
          }
          break;
        }
        case IResourceDelta.CHANGED: {
          final IPath path = X10PlatformConfFactory.getFile(resourceDelta.getResource().getProject()).getFullPath();
          final IResourceDelta platformConfDelta = rootResourceDelta.findMember(path);
          if ((platformConfDelta != null) && (platformConfDelta.getFlags() != IResourceDelta.MARKERS)) {
            final IFile platformConfFile = (IFile) platformConfDelta.getResource();
            final IX10PlatformConf platformConf = X10PlatformConfFactory.load(platformConfFile);
            this.fProjectToPlatform.put(project, platformConf);
            if (platformConf.isComplete(false)) {
              startResourceManager(project, platformConf);
            }
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
  
  // --- IPreferenceChangeListener's interface methods implementation
  
  public void preferenceChange(final PreferenceChangeEvent event) {
    if (X10Constants.P_OPTIMIZE.equals(event.getKey())) {
      for (final Map.Entry<IProject, IX10PlatformConf> entry : this.fProjectToPlatform.entrySet()) {
        final IX10PlatformConfWorkCopy workingCopy = entry.getValue().createWorkingCopy();
        workingCopy.updateCompilationCommands();
        workingCopy.applyChanges();
        try {
          X10PlatformConfFactory.save(X10PlatformConfFactory.getFile(entry.getKey()), workingCopy);
        } catch (CoreException except) {
          log(except.getStatus());
        }
      }
      
      final WorkspaceJob job = new WorkspaceJob(LaunchMessages.CLC_RebuildWorkspaceJobName) {
        
        public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
          ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, monitor);
          return Status.OK_STATUS;
        }
        
      };
      job.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
      job.setPriority(Job.BUILD);
      job.schedule();
    }
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
   * Returns the X10 platform configuration for the given project. The given project need to have a C++ X10 nature in order to
   * return a non-null value.
   * 
   * @param project
   *          The project for which one wants to access the X10 platform configuration.
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
   * @param status
   *          The status to log.
   */
  public static final void log(final IStatus status) {
    if (fPlugin != null) {
      fPlugin.getLog().log(status);
    }
  }

  /**
   * Logs (if the plugin is active) the outcome of an operation with the parameters provided.
   * 
   * @param severity
   *          The severity of the message. The code is one of {@link IStatus#OK}, {@link IStatus#ERROR}, {@link IStatus#INFO},
   *          {@link IStatus#WARNING} or {@link IStatus#CANCEL}.
   * @param code
   *          The plug-in-specific status code, or {@link IStatus#OK}.
   * @param message
   *          The human-readable message, localized to the current locale.
   * @param exception
   *          The exception to log, or <b>null</b> if not applicable.
   */
  public static final void log(final int severity, final int code, final String message, final Throwable exception) {
    if (fPlugin != null) {
      fPlugin.getLog().log(new Status(severity, fPlugin.getBundle().getSymbolicName(), code, message, exception));
    }
  }

  /**
   * Logs (if the plugin is active) the outcome of an operation with the parameters provided.
   * 
   * <p>
   * Similar to {@link #log(int, int, String, Throwable)} with <code>code = IStatus.OK</code>.
   * 
   * @param severity
   *          The severity of the message. The code is one of {@link IStatus#OK}, {@link IStatus#ERROR}, {@link IStatus#INFO},
   *          {@link IStatus#WARNING} or {@link IStatus#CANCEL}.
   * @param message
   *          The human-readable message, localized to the current locale.
   * @param exception
   *          The exception to log, or <b>null</b> if not applicable.
   */
  public static final void log(final int severity, final String message, final Throwable exception) {
    if (fPlugin != null) {
      fPlugin.getLog().log(new Status(severity, fPlugin.getBundle().getSymbolicName(), message, exception));
    }
  }

  /**
   * Logs (if the plugin is active) the outcome of an operation with the parameters provided.
   * 
   * <p>
   * Similar to {@link #log(int, int, String, Throwable)} with <code>code = IStatus.OK</code> and <code>exception = null</code>.
   * 
   * @param severity
   *          The severity of the message. The code is one of {@link IStatus#OK}, {@link IStatus#ERROR}, {@link IStatus#INFO},
   *          {@link IStatus#WARNING} or {@link IStatus#CANCEL}.
   * @param message
   *          The human-readable message, localized to the current locale.
   */
  public static final void log(final int severity, final String message) {
    if (fPlugin != null) {
      fPlugin.getLog().log(new Status(severity, fPlugin.getBundle().getSymbolicName(), message));
    }
  }

  // --- Overridden methods

  @SuppressWarnings("deprecation")
  // To use IWorkspace#addSaveParticipant(String, ISaveParticipant) once we move away from Galileo.
  public void start(final BundleContext context) throws Exception {
    super.start(context);

    initPlatformConfMap();
    ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    final ISavedState lastState = ResourcesPlugin.getWorkspace().addSaveParticipant(this, this);
    if (lastState != null) {
      lastState.processResourceChangeEvents(this);
    }
    X10DTCorePlugin.getInstance().getPreferencesService().getPreferences(IPreferencesService.INSTANCE_LEVEL)
                   .addPreferenceChangeListener(this);
    fPlugin = this;
  }

  @SuppressWarnings("deprecation")
  // To use IWorkspace#removeSaveParticipant(String) once we move away from Galileo.
  public void stop(final BundleContext context) throws Exception {
    fPlugin = null;
    ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    ResourcesPlugin.getWorkspace().removeSaveParticipant(this);
    this.fProjectToPlatform.clear();
    this.fProjectToPlatform = null;
    X10DTCorePlugin.getInstance().getPreferencesService().getPreferences(IPreferencesService.INSTANCE_LEVEL)
                   .removePreferenceChangeListener(this);

    super.stop(context);
  }

  // --- Private code

  private void initPlatformConfMap() {
    final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
    this.fProjectToPlatform = new HashMap<IProject, IX10PlatformConf>(projects.length);
    for (final IProject curProject : projects) {
      try {
        if (curProject.isOpen() && curProject.hasNature(X10DTCorePlugin.X10_CPP_PRJ_NATURE_ID)) {
          this.fProjectToPlatform.put(curProject, X10PlatformConfFactory.load(curProject));
        }
      } catch (CoreException except) {
        CppLaunchCore.log(except.getStatus());
      }
    }
  }

  private void safeRunForMarkers(final IResource resource, final IProgressMonitor monitor, final IWorkspaceRunnable runnable) {
    new Thread(new Runnable() {

      public void run() {
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        while (workspace.isTreeLocked())
          ;
        if (!monitor.isCanceled()) {
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

  private void startResourceManager(final IProject project, final IX10PlatformConf platformConf) {
    final WorkspaceJob job = new WorkspaceJob(LaunchMessages.CLC_CheckConnAndCommJobMsg) {

      public IStatus runInWorkspace(final IProgressMonitor monitor) {
        final IX10PlatformChecker checker = PlatformCheckerFactory.create();
        final IFile file = X10PlatformConfFactory.getFile(project);

        safeRunForMarkers(file, new NullProgressMonitor(), new IWorkspaceRunnable() {

          public void run(final IProgressMonitor localMonitor) throws CoreException {
            CoreResourceUtils.deletePlatformConfMarkers(file);
          }

        });

        final CommunicationInterfaceListener listener = new CommunicationInterfaceListener(file);
        checker.addValidationListener(listener);
        try {
          checker.validateCommunicationInterface(platformConf, monitor);
        } finally {
          checker.removeValidationListener(listener);
        }
        if (listener.getException() != null) {
          return listener.getException().getStatus();
        } else {
          return Status.OK_STATUS;
        }
      }

    };
    job.setRule(X10PlatformConfFactory.getFile(project));
    job.setPriority(Job.LONG);
    job.schedule();
  }

  // --- Private classes

  private final class CommunicationInterfaceListener implements IX10PlatformValidationListener {

    CommunicationInterfaceListener(final IFile platformConfFile) {
      this.fPlatformConfFile = platformConfFile;
    }

    // --- Interface methods implementation

    public void platformCommunicationInterfaceValidated() {
    }

    public void platformCommunicationInterfaceValidationFailure(final String message) {
      safeRunForMarkers(this.fPlatformConfFile, new NullProgressMonitor(), new IWorkspaceRunnable() {

        public void run(final IProgressMonitor localMonitor) throws CoreException {
          CoreResourceUtils.addPlatformConfMarker(CommunicationInterfaceListener.this.fPlatformConfFile,
                                                  NLS.bind(LaunchMessages.XPCFE_DiscoveryCmdFailedMarkerMsg, message),
                                                  IMarker.SEVERITY_ERROR, IMarker.PRIORITY_HIGH);
        }

      });
    }

    public void platformCppCompilationValidated() {
    }

    public void platformCppCompilationValidationError(final Exception exception) {
    }

    public void platformCppCompilationValidationFailure(final String message) {
    }

    public void remoteConnectionFailure(final Exception exception) {
      safeRunForMarkers(this.fPlatformConfFile, new NullProgressMonitor(), new IWorkspaceRunnable() {

        public void run(final IProgressMonitor localMonitor) throws CoreException {
          CoreResourceUtils.addPlatformConfMarker(CommunicationInterfaceListener.this.fPlatformConfFile,
                                                  NLS.bind(LaunchMessages.XPCFE_RemoteConnFailureMarkerMsg,
                                                           exception.getMessage()), IMarker.SEVERITY_ERROR, 
                                                           IMarker.PRIORITY_HIGH);
        }

      });
    }

    public void remoteConnectionUnknownStatus() {
    }

    public void remoteConnectionValidated(final ITargetElement targetElement) {
    }

    public void serviceProviderFailure(final CoreException exception) {
      this.fCoreException = exception;
    }

    // --- Internal services

    CoreException getException() {
      return this.fCoreException;
    }

    // --- Fields

    private final IFile fPlatformConfFile;

    private CoreException fCoreException;

  }

  // --- Fields

  private static CppLaunchCore fPlugin;

  private Map<IProject, IX10PlatformConf> fProjectToPlatform;

}
