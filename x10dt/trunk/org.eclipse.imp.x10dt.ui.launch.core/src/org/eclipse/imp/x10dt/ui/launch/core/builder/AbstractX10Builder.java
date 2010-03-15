/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.imp.x10dt.ui.launch.core.utils.AlwaysTrueFilter;
import org.eclipse.imp.x10dt.ui.launch.core.utils.CollectionUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IResourceUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IdentityFunctor;
import org.eclipse.imp.x10dt.ui.launch.core.utils.JavaProjectUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.PTPUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.UIUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.X10BuilderUtils;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.IModelManager;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.ui.IWorkbench;
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
  
  protected abstract ELanguage getLanguage();
   
  // --- Abstract methods implementation
  
  @SuppressWarnings("unchecked")
  protected final IProject[] build(final int kind, final Map args, final IProgressMonitor monitor) throws CoreException {
    try {
      if (this.fProjectWrapper == null) {
        return new IProject[0];
      }
      if (this.fSourcesToCompile.isEmpty()) {
        collectSourceFilesToCompile(kind, this.fDependentProjects, monitor);
      }
      final IPath outpuLoc = this.fProjectWrapper.getOutputLocation();
      final IContainer binaryContainer = ResourcesPlugin.getWorkspace().getRoot().getFolder(outpuLoc);

      monitor.beginTask(null, 100);
      
      // Let's get the resource manager.
      final IWorkbench workbench = LaunchCore.getInstance().getWorkbench();
      final IResourceManager[] rmFound = new IResourceManager[1];
      workbench.getDisplay().syncExec(new Runnable() {

        public void run() {
          try {
            rmFound[0] = PTPUtils.getResourceManager(workbench.getDisplay().getActiveShell(), getProject());
          } catch (CoreException except) {
            // Let's forget that.
          }
        }
        
      });
      final IResourceManager resourceManager = rmFound[0];
      if (resourceManager == null) {
        IResourceUtils.addMarkerTo(getProject(), NLS.bind(Messages.CPPB_NoResManagerError, getProject().getName()), 
                                   IMarker.SEVERITY_ERROR, 
                                   getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
        UIUtils.showView(PROBLEMS_VIEW_ID);
        return new IProject[0];
      }
      
      if (resourceManager.getState() != ResourceManagerAttributes.State.STARTED) {
        try {
          resourceManager.startUp(new SubProgressMonitor(monitor, 3));
        } catch (CoreException except) {
          IResourceUtils.addMarkerTo(getProject(), NLS.bind(Messages.AXB_ResManagerStartFailure, resourceManager.getName()), 
                                     IMarker.SEVERITY_ERROR, 
                                     getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
          UIUtils.showView(PROBLEMS_VIEW_ID);
          return new IProject[0];
        }
      } else {
        monitor.worked(3);
      }
            
      // Let's get the platform configuration
      final Map<String, IX10PlatformConfiguration> platforms = X10PlatformsManager.loadPlatformsConfiguration();
      final String platformConfName = getProject().getPersistentProperty(Constants.X10_PLATFORM_CONF);
      final IX10PlatformConfiguration platform = platforms.get(platformConfName);
      if (platform == null) {
        IResourceUtils.addMarkerTo(getProject(), Messages.XB_NoPlatformConfError, IMarker.SEVERITY_ERROR, 
                                   getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
        UIUtils.showView(PROBLEMS_VIEW_ID);
        return new IProject[0];
      }
      
      // Let's clear the markers.
      clearMarkers(kind);
      
      // Let's compile the X10 files.
      final String workspaceDir = JavaProjectUtils.getWorkspaceDirValue(getProject());
      final File outputDir = new File(binaryContainer.getLocationURI());
      compileX10Files(outputDir.getAbsolutePath(), new SubProgressMonitor(monitor, 27));
      
      // Finally, let's compile the generated files.
      return compileGeneratedFiles(resourceManager, this.fDependentProjects, workspaceDir, platform, binaryContainer,
                                   new SubProgressMonitor(monitor, 60));
    } catch (IOException except) {
      IResourceUtils.addMarkerTo(getProject(), Messages.XPCPP_LoadingErrorMsg,
      										       IMarker.SEVERITY_ERROR, getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
      UIUtils.showView(PROBLEMS_VIEW_ID);
      LaunchCore.log(IStatus.ERROR, Messages.XPCPP_LoadingErrorLogMsg, except);
      return new IProject[0];
    } finally {
      this.fDependencyInfo.clearAllDependencies();
      this.fSourcesToCompile.clear();
      this.fRootFileNames.clear();

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
        collectSourceFilesToCompile(INCREMENTAL_BUILD, this.fDependentProjects, new SubProgressMonitor(monitor, 1));
        
        clearGeneratedAndCompiledFiles(new SubProgressMonitor(monitor, 1));
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
  
  private void clearGeneratedAndCompiledFiles(final IProgressMonitor monitor) throws CoreException {
    final String resManagerID = getProject().getPersistentProperty(Constants.RES_MANAGER_ID);
    final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
    final IResourceManager rmManager = modelManager.getResourceManagerFromUniqueName(resManagerID);
    final IResourceManagerControl rmControl = (IResourceManagerControl) rmManager;
    final IResourceManagerConfiguration rmc = rmControl.getConfiguration();
    final IRemoteServices remoteServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmc.getRemoteServicesId());
    final IRemoteConnection rmConnection = remoteServices.getConnectionManager().getConnection(rmc.getConnectionName());
    final IRemoteFileManager fileManager = remoteServices.getFileManager(rmConnection);
    
    final IProgressMonitor nullMonitor = new NullProgressMonitor();
    try {
      monitor.beginTask(null, this.fSourcesToCompile.size() + 1);
      final String workspaceDir = JavaProjectUtils.getWorkspaceDirValue(getProject());      
      final IPath wDirPath = new Path(workspaceDir);
      
      for (final IFile sourceFile : this.fSourcesToCompile) {
        final String rootName = sourceFile.getFullPath().removeFileExtension().lastSegment();
        
        this.fRootFileNames.add(rootName);
        
        fileManager.getResource(wDirPath.append(rootName + ".cc").toString()).delete(EFS.NONE, nullMonitor); //$NON-NLS-1$
        fileManager.getResource(wDirPath.append(rootName + ".h").toString()).delete(EFS.NONE, nullMonitor); //$NON-NLS-1$
        fileManager.getResource(wDirPath.append(rootName + ".inc").toString()).delete(EFS.NONE, nullMonitor); //$NON-NLS-1$
        fileManager.getResource(wDirPath.append(rootName + ".o").toString()).delete(EFS.NONE, nullMonitor); //$NON-NLS-1$
      
        monitor.worked(1);
      }
      
      final IFileStore parentStore = fileManager.getResource(workspaceDir);
      parentStore.getChild("xxx_main_xxx.cc").delete(EFS.NONE, nullMonitor); //$NON-NLS-1$
      parentStore.getChild("lib" + getProject().getName() + ".a").delete(EFS.NONE, nullMonitor); //$NON-NLS-1$ //$NON-NLS-2$
      
      final String execPath = getProject().getPersistentProperty(Constants.EXEC_PATH);
      if (execPath != null) {
        fileManager.getResource(execPath).delete(EFS.NONE, nullMonitor);
      }
      monitor.worked(1);
    } finally {
      monitor.done();
    }
  }
  
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
  
  private IProject[] compileGeneratedFiles(final IResourceManager resourceManager, final Set<IProject> dependentProjects, 
                                           final String workspaceDir, final IX10PlatformConfiguration platform,
  		                                     final IContainer binaryContainer, 
  		                                     final IProgressMonitor monitor) throws CoreException {
    try {
      final IX10BuilderOp builderOp;
      if (platform.isLocal()) {
        builderOp = new LocalX10BuilderOp(getProject(), workspaceDir, resourceManager, this.fRootFileNames);
      } else {
        builderOp = new RemoteX10BuilderOp(getProject(), workspaceDir, resourceManager, platform.getTargetOS(), 
                                           this.fRootFileNames);
      }
      
      builderOp.transfer(binaryContainer, new SubProgressMonitor(monitor, 10));
      if (builderOp.compile(platform, new SubProgressMonitor(monitor, 70))) {
        builderOp.archive(platform, new SubProgressMonitor(monitor, 20));
      }
    } catch (WorkbenchException except) {
      IResourceUtils.addMarkerTo(getProject(), Messages.XPCPP_LoadingErrorMsg,
                                 IMarker.SEVERITY_ERROR, getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
      UIUtils.showView(PROBLEMS_VIEW_ID);
      LaunchCore.log(IStatus.ERROR, Messages.XPCPP_LoadingErrorLogMsg, except);
      return new IProject[0];
    }
    
    return dependentProjects.toArray(new IProject[dependentProjects.size()]);
  }
  
  private void compileX10Files(final String outputDir, final IProgressMonitor monitor) throws CoreException {
    final ICompilerX10ExtInfo compilerExtInfo = X10BuilderUtils.createCompilerX10ExtInfo(getLanguage());
    
    final Set<String> cps = JavaProjectUtils.getFilteredCpEntries(this.fProjectWrapper, new CpEntryAsStringFunc(), 
                                                                  new AlwaysTrueFilter<IPath>());
    final StringBuilder cpBuilder = new StringBuilder();
    int i = -1;
    for (final String cpEntry : cps) {
      if (++i > 0) {
        cpBuilder.append(File.pathSeparatorChar);
      }
      cpBuilder.append(cpEntry);
    }
    
    final Set<IPath> srcPaths = JavaProjectUtils.getFilteredCpEntries(this.fProjectWrapper, new IdentityFunctor<IPath>(),
                                                                      new RuntimeFilter());
    final List<File> sourcePath = CollectionUtils.transform(srcPaths, new IPathToFileFunc());
    
    final ExtensionInfo extInfo = compilerExtInfo.createExtensionInfo(cpBuilder.toString(), sourcePath, outputDir, 
                                                                      false /* withMainMethod */, monitor);
    
    final Compiler compiler = new Compiler(extInfo, new X10ErrorQueue(1000000, getProject(), extInfo.compilerName()));
    Globals.initialize(compiler);
    
    compiler.compile(toSources(this.fSourcesToCompile));
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
  
  private Collection<IFile> fSourcesToCompile = new HashSet<IFile>();
  
  private Set<IProject> fDependentProjects = new HashSet<IProject>();
  
  private Set<String> fRootFileNames = new HashSet<String>();
  
  
  private static final String PROBLEMS_VIEW_ID = "org.eclipse.ui.views.ProblemView"; //$NON-NLS-1$

}
