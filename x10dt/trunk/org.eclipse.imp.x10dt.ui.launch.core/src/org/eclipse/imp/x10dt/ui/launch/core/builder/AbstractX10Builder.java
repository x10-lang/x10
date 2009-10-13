/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.imp.builder.DependencyInfo;
import org.eclipse.imp.x10dt.core.builder.PolyglotDependencyInfo;
import org.eclipse.imp.x10dt.ui.launch.core.Constants;
import org.eclipse.imp.x10dt.ui.launch.core.LaunchCore;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.builder.operations.IX10BuilderOp;
import org.eclipse.imp.x10dt.ui.launch.core.builder.operations.LocalX10BuilderOp;
import org.eclipse.imp.x10dt.ui.launch.core.builder.operations.RemoteX10BuilderOp;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.IX10PlatformConfiguration;
import org.eclipse.imp.x10dt.ui.launch.core.platform_conf.X10PlatformsManager;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IResourceUtils;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.IModelManager;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes;
import org.eclipse.ui.WorkbenchException;

import polyglot.frontend.Compiler;
import polyglot.frontend.FileSource;
import polyglot.frontend.Globals;
import polyglot.frontend.Source;
import x10.ExtensionInfo;

/**
 * X10 builder base class for all the different back-ends.
 * 
 * @author egeay
 */
public abstract class AbstractX10Builder extends IncrementalProjectBuilder {

  // --- Abstract methods definition
  
  protected abstract ExtensionInfo createExtensionInfo(final IJavaProject javaProject, final IContainer binaryContainer,
                                                       final IProgressMonitor monitor) throws CoreException;
  
  // --- Abstract methods implementation
  
  @SuppressWarnings("unchecked")
  protected final IProject[] build(final int kind, final Map args, final IProgressMonitor monitor) throws CoreException {
    try {
      if (this.fProjectWrapper == null) {
        return new IProject[0];
      }
      this.fDependencyInfo.clearAllDependencies();
      this.fSourcesToCompile.clear();
    
      monitor.beginTask(null, 100);
      
      final Set<IProject> dependentProjects = new HashSet<IProject>();
      collectSourceFilesToCompile(kind, dependentProjects, new SubProgressMonitor(monitor, 2));
      clean(new SubProgressMonitor(monitor, 2));
      clearMarkers(kind);

      compileX10Files(new SubProgressMonitor(monitor, 36));
      
      return compileGeneratedFiles(dependentProjects, new SubProgressMonitor(monitor, 60));
    } finally {
      monitor.done();
    }
  }
  
  // --- Overridden methods
  
  protected void clean(final IProgressMonitor monitor) throws CoreException {
    monitor.beginTask(null, 2);
    monitor.subTask(Messages.CPPB_CleanTaskName);
    try {
      if (getProject().isAccessible()) {
        if (this.fProjectWrapper == null) {
          this.fProjectWrapper = JavaCore.create(getProject());
        }
        final IContainer binaryContainer = getBinaryContainer();
        if (binaryContainer != null) {
          binaryContainer.refreshLocal(IResource.DEPTH_INFINITE, new SubProgressMonitor(monitor, 1));
          final IResource[] members = binaryContainer.members();
          monitor.beginTask(Messages.CPPB_DeletingTaskName, members.length);
          for (final IResource member : members) {
            member.delete(true /* force */, new SubProgressMonitor(monitor, 1));
          }
        }
      }
    } finally {
      monitor.done();
    }
  }
  
  protected void startupOnInitialize() {
    if (getProject().isAccessible()) {
      if (this.fProjectWrapper == null) {
        this.fProjectWrapper = JavaCore.create(getProject());
      }
      if (this.fDependencyInfo == null) {
        this.fDependencyInfo = new PolyglotDependencyInfo(getProject());
      }
    }
  }
  
  // --- Private code
  
  private void clearMarkers(final int kind) throws CoreException {
    if (kind == IncrementalProjectBuilder.INCREMENTAL_BUILD) {
      for (final IFile file : this.fSourcesToCompile) {
        file.deleteMarkers(org.eclipse.imp.x10dt.core.builder.X10Builder.PROBLEMMARKER_ID, false /* includeSubtypes */, 
                           IResource.DEPTH_ZERO);
      }
    } else {
      getProject().deleteMarkers(org.eclipse.imp.x10dt.core.builder.X10Builder.PROBLEMMARKER_ID, true /* includeSubtypes */,
                                 IResource.DEPTH_INFINITE);
    }
  }
  
  private void collectSourceFilesToCompile(final int kind, final Set<IProject> dependentProjects,
                                           final IProgressMonitor monitor) throws CoreException {
    try {
      monitor.beginTask(Messages.CPPB_CollectingSourcesTaskName, 1);
      getProject().accept(new SourceFilesCollector(this.fSourcesToCompile, this.fDependencyInfo,
                                                   getProject(), dependentProjects));
    } finally {
      monitor.done();
    }
  }
  
  private IProject[] compileGeneratedFiles(final Set<IProject> dependentProjects,
                                           final IProgressMonitor monitor) throws CoreException {
    try {
      final Map<String, IX10PlatformConfiguration> platforms = X10PlatformsManager.loadPlatformsConfiguration();
      final String platformConfName = getProject().getPersistentProperty(Constants.X10_PLATFORM_CONF);
      final IX10PlatformConfiguration platform = platforms.get(platformConfName);
      
      final String workspaceDir = getProject().getPersistentProperty(Constants.WORKSPACE_DIR);
      
      final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
      final String resManagerID = getProject().getPersistentProperty(Constants.RES_MANAGER_ID);
      final IResourceManager resourceManager = modelManager.getUniverse().getResourceManager(resManagerID);
      if (resourceManager == null) {
        IResourceUtils.addMarkerTo(getProject(), NLS.bind(Messages.CPPB_NoResManagerError, getProject().getName()), 
                                   IMarker.SEVERITY_ERROR, 
                                   getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
        return new IProject[0];
      }
      if (resourceManager.getState() != ResourceManagerAttributes.State.STARTED) {
        IResourceUtils.addMarkerTo(getProject(), NLS.bind(Messages.CPPB_ResManagerNotStarted, resourceManager.getName()), 
                                   IMarker.SEVERITY_ERROR, 
                                   getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
        return new IProject[0];
      }
      
      final IX10BuilderOp builderOp;
//      if (platform.isLocal()) {
//        builderOp = new LocalX10BuilderOp(getProject(), workspaceDir, resourceManager);
//      } else {
        builderOp = new RemoteX10BuilderOp(getProject(), workspaceDir, resourceManager);
//      }
      
      builderOp.transfer(this.fBinaryContainer, new SubProgressMonitor(monitor, 10));
      builderOp.compile(platform, new SubProgressMonitor(monitor, 70));
      builderOp.archive(platform, new SubProgressMonitor(monitor, 20));
    } catch (WorkbenchException except) {
      IResourceUtils.addMarkerTo(getProject(), Messages.XPCPP_LoadingErrorMsg,
                                 IMarker.SEVERITY_ERROR, getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
      LaunchCore.log(IStatus.ERROR, Messages.XPCPP_LoadingErrorLogMsg, except);
      return new IProject[0];
    } catch (IOException except) {
      IResourceUtils.addMarkerTo(getProject(), Messages.XPCPP_LoadingErrorMsg,
                                 IMarker.SEVERITY_ERROR, getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
      LaunchCore.log(IStatus.ERROR, Messages.XPCPP_LoadingErrorLogMsg, except);
      return new IProject[0];
    }
    
    return dependentProjects.toArray(new IProject[dependentProjects.size()]);
  }
  
  private void compileX10Files(final IProgressMonitor monitor) throws CoreException {
    final ExtensionInfo extInfo = createExtensionInfo(this.fProjectWrapper, this.fBinaryContainer, monitor);
    
    final Compiler compiler = new Compiler(extInfo, new X10ErrorQueue(1000000, getProject(), extInfo.compilerName()));
    Globals.initialize(compiler);
    
    compiler.compile(toSources(this.fSourcesToCompile));
  }
  
  private IContainer getBinaryContainer() {
    if (this.fBinaryContainer == null) {
      try {
        final IPath outputLoc = this.fProjectWrapper.getOutputLocation();
        this.fBinaryContainer = ResourcesPlugin.getWorkspace().getRoot().getFolder(outputLoc);
      } catch (JavaModelException except) {
        LaunchCore.log(except.getStatus());
        return null;
      }
    }
    return this.fBinaryContainer;
  }
  
  private Collection<Source> toSources(final Collection<IFile> sources) throws CoreException {
    final Collection<Source> pSources = new ArrayList<Source>(sources.size());
    for (final IFile file : sources) {
      try {
        pSources.add(new FileSource(new JavaModelFileResource(file)));
      } catch (IOException except) {
        throw new CoreException(new Status(IStatus.ERROR, LaunchCore.getInstance().getBundle().getSymbolicName(), 
                                           NLS.bind(Messages.CPPB_FileReadingErrorMessage, file),
                                           except));
      }
    }
    return pSources;
  }
  
  // --- Fields
  
  private DependencyInfo fDependencyInfo;
  
  private IJavaProject fProjectWrapper;
  
  private IContainer fBinaryContainer;
  
  private Collection<IFile> fSourcesToCompile = new HashSet<IFile>();  

}
