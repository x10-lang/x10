/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.builder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
import org.eclipse.imp.utils.ConsoleUtil;
import org.eclipse.imp.x10dt.core.builder.PolyglotDependencyInfo;
import org.eclipse.imp.x10dt.core.builder.X10Builder;
import org.eclipse.imp.x10dt.ui.launch.cpp.Constants;
import org.eclipse.imp.x10dt.ui.launch.cpp.CppLaunchCore;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.imp.x10dt.ui.launch.cpp.utils.IResourceUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.utils.collections.AlwaysTrueFilter;
import org.eclipse.imp.x10dt.ui.launch.cpp.utils.collections.IdentityFunctor;
import org.eclipse.imp.x10dt.ui.launch.cpp.utils.collections.JavaProjectUtils;
import org.eclipse.imp.x10dt.ui.launch.cpp.utils.collections.ListUtils;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.IModelManager;
import org.eclipse.ptp.core.PTPCorePlugin;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.core.elements.attributes.ResourceManagerAttributes;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.remote.remotetools.core.RemoteToolsConnection;
import org.eclipse.ptp.remote.ui.IRemoteUIServices;
import org.eclipse.ptp.remote.ui.PTPRemoteUIPlugin;
import org.eclipse.ptp.remotetools.core.IRemoteCopyTools;
import org.eclipse.ptp.remotetools.core.IRemoteExecutionManager;
import org.eclipse.ptp.remotetools.core.IRemoteExecutionTools;
import org.eclipse.ptp.remotetools.core.IRemoteFileTools;
import org.eclipse.ptp.remotetools.core.IRemoteScript;
import org.eclipse.ptp.remotetools.core.IRemoteScriptExecution;
import org.eclipse.ptp.remotetools.exception.CancelException;
import org.eclipse.ptp.remotetools.exception.RemoteConnectionException;
import org.eclipse.ptp.remotetools.exception.RemoteExecutionException;
import org.eclipse.ptp.remotetools.exception.RemoteOperationException;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import polyglot.frontend.Compiler;
import polyglot.frontend.FileSource;
import polyglot.frontend.Globals;
import polyglot.frontend.Source;
import polyglot.main.Report;
import polyglot.frontend.ExtensionInfo;
import polyglot.ext.x10cpp.Configuration;
import polyglot.ext.x10cpp.X10CPPCompilerOptions;



/**
 * Builder responsible for launching the X10 CPP back-end.
 * 
 * <p>The current version is very "simplistic". It copies the C++ generated files to the remote location associated to this 
 * project, and then performs a remote separate compilation to create an archive library. It uses PTP utilities to perform 
 * the transfer and executes the remote commands.
 * 
 * <p>Incremental building is not supported and all the files will be recompiled each time the builder is called. Finally,
 * remote commands are supposed to be executed on a Unix-like machine.
 * 
 * @author egeay
 */
public final class CppBuilder extends IncrementalProjectBuilder {
  
  // --- Abstract methods implementation
  
  @SuppressWarnings("unchecked")
  protected IProject[] build(final int kind, final Map args, final IProgressMonitor monitor) throws CoreException {
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

      compile(new SubProgressMonitor(monitor, 36));
      
      final IModelManager modelManager = PTPCorePlugin.getDefault().getModelManager();
      final String resManagerID = getProject().getPersistentProperty(Constants.RES_MANAGER_ID);
      final IResourceManager resourceManager = modelManager.getUniverse().getResourceManager(resManagerID);
      if (resourceManager == null) {
        IResourceUtils.addMarkerTo(getProject(), NLS.bind(LaunchMessages.CB_NoResManagerError, getProject().getName()), 
                                   IMarker.SEVERITY_ERROR, 
                                   getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
        return new IProject[0];
      }
      if (resourceManager.getState() != ResourceManagerAttributes.State.STARTED) {
        IResourceUtils.addMarkerTo(getProject(), NLS.bind(LaunchMessages.CB_ResManagerNotStarted, resourceManager.getName()), 
                                   IMarker.SEVERITY_ERROR, 
                                   getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
        return new IProject[0];
      }
      try {
        final IRemoteExecutionManager rmExecManager = getExecutionManager(resourceManager);
        final Map<File, String> remoteFiles = new HashMap<File, String>();
        if (transfer(resourceManager.getName(), rmExecManager, remoteFiles, new SubProgressMonitor(monitor, 20))) {
          remoteCompilation(resourceManager.getName(), rmExecManager, remoteFiles, new SubProgressMonitor(monitor, 40));
        }
      } catch (RemoteConnectionException except) {
        IResourceUtils.addMarkerTo(getProject(), NLS.bind(LaunchMessages.CPPB_ConnError, resourceManager.getName()), 
                                   IMarker.SEVERITY_ERROR, getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
        CppLaunchCore.log(IStatus.ERROR, NLS.bind(LaunchMessages.CPPB_ConnError, resourceManager.getName()), except);
      }
      return dependentProjects.toArray(new IProject[dependentProjects.size()]);
    } finally {
      monitor.done();
    }
  }
  
  // --- Overridden methods
  
  protected void clean(final IProgressMonitor monitor) throws CoreException {
    monitor.beginTask(null, 2);
    monitor.subTask(LaunchMessages.CPPB_CleanTaskName);
    try {
      if (getProject().isAccessible()) {
        if (this.fProjectWrapper == null) {
          this.fProjectWrapper = JavaCore.create(getProject());
        }
        final IContainer binaryContainer = getBinaryContainer();
        if (binaryContainer != null) {
          binaryContainer.refreshLocal(IResource.DEPTH_INFINITE, new SubProgressMonitor(monitor, 1));
          final IResource[] members = binaryContainer.members();
          monitor.beginTask(LaunchMessages.CPPB_DeletingTaskName, members.length);
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

  private void buildOptions(final X10CPPCompilerOptions options) throws CoreException {
    // Sets the class path
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
    // Sets the source path.
    final Set<IPath> srcPaths = JavaProjectUtils.getFilteredCpEntries(this.fProjectWrapper, new IdentityFunctor<IPath>(),
                                                                      new RuntimeFilter());
    // Set the output dir.
    final File outputDir = new File(this.fBinaryContainer.getLocationURI());
   
    // Some useful Polyglot reports.
    //Report.addTopic("verbose", 1); //$NON-NLS-1$
    Report.addTopic("postcompile", 1); //$NON-NLS-1$
    
    // We can now set all the Polyglot options for our extension.
    options.assertions = true;
    options.classpath = cpBuilder.toString();
    options.output_classpath = options.classpath;
    options.serialize_type_info = false;
    options.output_directory = outputDir;
    options.source_path = ListUtils.transform(srcPaths, new IPathToFileFunc());
    options.compile_command_line_only = true;
    Configuration.MAIN_CLASS = ""; //$NON-NLS-1$ We do generate main class stub during partial compilation.
  }
  
  private void clearMarkers(final int kind) throws CoreException {
    if (kind == IncrementalProjectBuilder.INCREMENTAL_BUILD) {
      for (final IFile file : this.fSourcesToCompile) {
        file.deleteMarkers(X10Builder.PROBLEMMARKER_ID, false /* includeSubtypes */, IResource.DEPTH_ZERO);
      }
    } else {
      getProject().deleteMarkers(X10Builder.PROBLEMMARKER_ID, true /* includeSubtypes */, IResource.DEPTH_INFINITE);
    }
  }
  
  private void collectSourceFilesToCompile(final int kind, final Set<IProject> dependentProjects,
                                           final IProgressMonitor monitor) throws CoreException {
    try {
      monitor.beginTask(LaunchMessages.CPPB_CollectingSourcesTaskName, 1);
      getProject().accept(new SourceFilesCollector(this.fSourcesToCompile, this.fDependencyInfo,
                                                   getProject(), dependentProjects));
    } finally {
      monitor.done();
    }
  }
  
  private void compile(final IProgressMonitor monitor) throws CoreException {
    final ExtensionInfo extInfo = new CppBuilderExtensionInfo(monitor); 
    buildOptions((X10CPPCompilerOptions) extInfo.getOptions());
    
    final Compiler compiler = new Compiler(extInfo, new X10ErrorQueue(1000000, getProject(), extInfo.compilerName()));
    Globals.initialize(compiler);
    
    compiler.compile(toSources(this.fSourcesToCompile));
  }
  
  private void copyGeneratedFiles(final IRemoteFileTools rmFileTools, final IRemoteCopyTools rmCopyTools, 
                                  final String workspaceDir, final File rootDir, final File curDir,
                                  final Map<File, String> remoteFiles,
                                  final IProgressMonitor monitor) throws RemoteOperationException, RemoteConnectionException, 
                                                                         CancelException {
    final String destDir = curDir.getAbsolutePath().replace(rootDir.getAbsolutePath(), workspaceDir).replace('\\', '/');
    if (! rmFileTools.hasDirectory(destDir)) {
      rmFileTools.createDirectory(destDir);
    }
    final File[] files = curDir.listFiles();
    monitor.beginTask(null, files.length);
    for (final File file : files) {
      if (file.isDirectory()) {
        copyGeneratedFiles(rmFileTools, rmCopyTools, workspaceDir, rootDir, file, remoteFiles, 
                           new SubProgressMonitor(monitor, 1));
      } else {
        rmCopyTools.uploadFileToDir(file, destDir);
        if (file.getName().endsWith(".cc")) { //$NON-NLS-1$
          remoteFiles.put(file, destDir + '/' + file.getName());
        }
        monitor.worked(1);
      }
    }
  }
  
  private IContainer getBinaryContainer() {
    if (this.fBinaryContainer == null) {
      try {
        final IPath outputLoc = this.fProjectWrapper.getOutputLocation();
        this.fBinaryContainer = ResourcesPlugin.getWorkspace().getRoot().getFolder(outputLoc);
      } catch (JavaModelException except) {
        CppLaunchCore.log(except.getStatus());
        return null;
      }
    }
    return this.fBinaryContainer;
  }
  
  private IRemoteExecutionManager getExecutionManager(final IResourceManager resManager) throws RemoteConnectionException {
    final IResourceManagerControl rm = (IResourceManagerControl) resManager;
      
    final IResourceManagerConfiguration rmc = rm.getConfiguration();
    final IRemoteServices remServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmc.getRemoteServicesId());
    final IRemoteUIServices remUIServices = PTPRemoteUIPlugin.getDefault().getRemoteUIServices(remServices);
    if (remServices != null && remUIServices != null) {
      final org.eclipse.ptp.remote.core.IRemoteConnection rmConn;
      rmConn = remServices.getConnectionManager().getConnection(rmc.getConnectionName());
      if (rmConn instanceof RemoteToolsConnection) {
        return ((RemoteToolsConnection) rmConn).createExecutionManager();
      }
    }
    return null;
  }
  
  private void remoteCompilation(final String resourceManagerName, final IRemoteExecutionManager rmExecManager, 
                                 final Map<File, String> remoteFiles,
                                 final IProgressMonitor monitor) throws CoreException, RemoteConnectionException {
    final IRemoteExecutionTools execTools = rmExecManager.getExecutionTools();
    monitor.beginTask(null, remoteFiles.size());
    monitor.subTask(LaunchMessages.CPPB_RemoteCompilTaskName);
    
    final String workspaceDir = getProject().getPersistentProperty(Constants.WORKSPACE_DIR) + '/' + getProject().getName();
    
    final IPreferenceStore store = CppLaunchCore.getInstance().getPreferenceStore();
    final String x10DistLoc = store.getString(Constants.P_CPP_BUILDER_X10_DIST_LOC);
    final String pgasLoc = store.getString(Constants.P_CPP_BUILDER_PGAS_LOC);
    final String compileCmdStart = store.getString(Constants.P_CPP_BUILDER_COMPILE_CMD);
    final List<String> allObjectFiles = new LinkedList<String>();
    try {
      for (final Map.Entry<File, String> entry : remoteFiles.entrySet()) {
        final IRemoteScript script = execTools.createScript();
        script.addEnvironment("X10_DIST_LOC=" + x10DistLoc); //$NON-NLS-1$
        script.addEnvironment("PGAS_LOC=" + pgasLoc); //$NON-NLS-1$
        
        final String objectFile = entry.getValue().replace(".cc", ".o"); //$NON-NLS-1$ //$NON-NLS-2$
        allObjectFiles.add(objectFile);
        
        final StringBuilder commandBuilder = new StringBuilder();
        commandBuilder.append(compileCmdStart).append(" -I").append(workspaceDir) //$NON-NLS-1$
                      .append(" -c ").append(entry.getValue()) //$NON-NLS-1$
                      .append(" -o ").append(objectFile); //$NON-NLS-1$
        
        script.setScript(commandBuilder.toString());
        
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        script.setProcessOutputStream(outputStream);
        final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        script.setProcessErrorStream(errorStream);
        
        final IRemoteScriptExecution rmExec = execTools.executeScript(script);
        rmExec.waitForEndOfExecution();
        
        final int returnCode = rmExec.getReturnCode();
        rmExec.close();
        
        if (returnCode != 0) {
          IResourceUtils.addMarkerTo(getProject(), NLS.bind(LaunchMessages.CPPB_CompilErrorMsg, entry.getKey().getName()), 
                                     IMarker.SEVERITY_ERROR, entry.getKey().getAbsolutePath(), IMarker.PRIORITY_HIGH);
          final MessageConsole messageConsole = ConsoleUtil.findConsole(LaunchMessages.CPPB_ConsoleName);
          final MessageConsoleStream mcStream = messageConsole.newMessageStream();
          mcStream.println(errorStream.toString());
        }
        
        monitor.worked(1);
      }
      
      final String rawArchiveCmd = store.getString(Constants.P_CPP_BUILDER_ARCHIVE_CMD);
      final String firstStep = rawArchiveCmd.replace("$workspace_dir", workspaceDir); //$NON-NLS-1$
      final String archiveCmd = firstStep.replace("$lib_name", "lib" + getProject().getName()); //$NON-NLS-1$ //$NON-NLS-2$
      
      final int returnCode = execTools.executeWithExitValue(archiveCmd);
      if (returnCode != 0) {
        IResourceUtils.addMarkerTo(getProject(), NLS.bind(LaunchMessages.CB_LibCreationError, archiveCmd), 
                                   IMarker.SEVERITY_ERROR, getProject().getFullPath().toString(), IMarker.PRIORITY_HIGH);
      }
    } catch (RemoteExecutionException except) {
      IResourceUtils.addMarkerTo(getProject(), NLS.bind(LaunchMessages.CPPB_RemoteOpError, resourceManagerName), 
                                 IMarker.SEVERITY_ERROR, getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
      CppLaunchCore.log(IStatus.ERROR, NLS.bind(LaunchMessages.CPPB_RemoteOpError, resourceManagerName), except);
    } catch (CancelException except) {
      IResourceUtils.addMarkerTo(getProject(), NLS.bind(LaunchMessages.CPPB_CancelOpMsg, resourceManagerName), 
                                 IMarker.SEVERITY_WARNING, getProject().getLocation().toString(), IMarker.PRIORITY_LOW);
    } finally {
      monitor.done();
    }
  }
  
  private Collection<Source> toSources(final Collection<IFile> sources) throws CoreException {
    final Collection<Source> pSources = new ArrayList<Source>(sources.size());
    for (final IFile file : sources) {
      try {
        pSources.add(new FileSource(new JavaModelFileResource(file)));
      } catch (IOException except) {
        throw new CoreException(new Status(IStatus.ERROR, CppLaunchCore.PLUGIN_ID, 
                                           NLS.bind(LaunchMessages.CPPB_FileReadingErrorMessage, file),
                                           except));
      }
    }
    return pSources;
  }
  
  private boolean transfer(final String resourceManagerName, final IRemoteExecutionManager rmExecManager, 
                           final Map<File, String> remoteFiles, 
                           final IProgressMonitor monitor) throws CoreException, RemoteConnectionException {
    try {
      final IRemoteFileTools rmFileTools = rmExecManager.getRemoteFileTools();
      final StringBuilder workspaceDir = new StringBuilder();
      workspaceDir.append(getProject().getPersistentProperty(Constants.WORKSPACE_DIR)).append('/')
                  .append(getProject().getName());
      if (rmFileTools.hasDirectory(workspaceDir.toString())) {
        monitor.subTask(LaunchMessages.CPPB_DeletionTaskName);
        rmFileTools.removeFile(workspaceDir.toString());
      }
          
      monitor.subTask(LaunchMessages.CPPB_TransferTaskName);
      final File rootDir = getBinaryContainer().getLocation().toFile();
      copyGeneratedFiles(rmFileTools, rmExecManager.getRemoteCopyTools(), workspaceDir.toString(), rootDir, rootDir, 
                         remoteFiles, monitor);
    } catch (RemoteOperationException except) {
      IResourceUtils.addMarkerTo(getProject(), NLS.bind(LaunchMessages.CPPB_RemoteOpError, resourceManagerName), 
                                 IMarker.SEVERITY_ERROR, getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
      CppLaunchCore.log(IStatus.ERROR, NLS.bind(LaunchMessages.CPPB_RemoteOpError, resourceManagerName), except);
      return false;
    } catch (CancelException except) {
      IResourceUtils.addMarkerTo(getProject(), NLS.bind(LaunchMessages.CPPB_CancelOpMsg, resourceManagerName), 
                                 IMarker.SEVERITY_WARNING, getProject().getLocation().toString(), IMarker.PRIORITY_LOW);
      return false;
    }
    return true;
  }

  // --- Fields
  
  private DependencyInfo fDependencyInfo;
  
  private IJavaProject fProjectWrapper;
  
  private IContainer fBinaryContainer;
  
  private Collection<IFile> fSourcesToCompile = new HashSet<IFile>();
    
}
