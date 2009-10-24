/*****************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                       *
 * All rights reserved. This program and the accompanying materials          *
 * are made available under the terms of the Eclipse Public License v1.0     *
 * which accompanies this distribution, and is available at                  *
 * http://www.eclipse.org/legal/epl-v10.html                                 *
 *****************************************************************************/
package org.eclipse.imp.x10dt.ui.cpp.launch.builder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.imp.builder.DependencyInfo;
import org.eclipse.imp.utils.ConsoleUtil;
import org.eclipse.imp.x10dt.core.builder.PolyglotDependencyInfo;
import org.eclipse.imp.x10dt.core.builder.X10Builder;
import org.eclipse.imp.x10dt.ui.cpp.launch.Constants;
import org.eclipse.imp.x10dt.ui.cpp.launch.LaunchCore;
import org.eclipse.imp.x10dt.ui.cpp.launch.LaunchMessages;
import org.eclipse.imp.x10dt.ui.cpp.launch.utils.IResourceUtils;
import org.eclipse.imp.x10dt.ui.cpp.launch.utils.RemoteProcessOutputListener;
import org.eclipse.imp.x10dt.ui.cpp.launch.utils.collections.AlwaysTrueFilter;
import org.eclipse.imp.x10dt.ui.cpp.launch.utils.collections.IdentityFunctor;
import org.eclipse.imp.x10dt.ui.cpp.launch.utils.collections.JavaProjectUtils;
import org.eclipse.imp.x10dt.ui.cpp.launch.utils.collections.ListUtils;
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
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.ptp.remote.core.IRemoteProcess;
import org.eclipse.ptp.remote.core.IRemoteProcessBuilder;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import polyglot.ext.x10.ExtensionInfo;
import polyglot.ext.x10cpp.X10CPPCompilerOptions;
import polyglot.frontend.Compiler;
import polyglot.frontend.FileSource;
import polyglot.frontend.Globals;
import polyglot.frontend.Source;
import polyglot.main.Report;
import polyglot.util.QuotedStringTokenizer;


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
  protected IProject[] build(final int kind, final Map args, 
                             final IProgressMonitor monitor) throws CoreException {
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
      final Map<String, String> remoteFiles = new HashMap<String, String>();
      if (transfer(resourceManager, remoteFiles, new SubProgressMonitor(monitor, 20))) {
        remoteCompilation(resourceManager, remoteFiles, new SubProgressMonitor(monitor, 40));
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
    options.post_compiler = null;
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
  /**
   * compile x10 to C++
   * @param monitor
   * @throws CoreException
   */
  private void compile(final IProgressMonitor monitor) throws CoreException {
    final ExtensionInfo extInfo = new CppBuilderExtensionInfo(monitor);
    buildOptions((X10CPPCompilerOptions) extInfo.getOptions());
    
    final Compiler compiler = new Compiler(extInfo, new X10ErrorQueue(1000000, getProject(), extInfo.compilerName()));
    Globals.initialize(compiler);
    
    compiler.compile(toSources(this.fSourcesToCompile));
  }
  
  private void copyGeneratedFiles(final IRemoteFileManager fileManager,
                                  final IFileStore destDir, final IPath srcDir,
                                  final Map<String, String> remoteFiles,
                                  final IProgressMonitor monitor) throws CoreException
  {
    if (!destDir.fetchInfo(EFS.NONE, monitor).exists()) {
      destDir.mkdir(EFS.NONE, monitor);
    }
    IFileStore curDirStore = EFS.getLocalFileSystem().getStore(srcDir);
    IFileStore[] files = curDirStore.childStores(EFS.NONE, monitor);
    monitor.beginTask(null, files.length);
    for (final IFileStore file : files) {
      String name = file.getName();
      if (file.fetchInfo(EFS.NONE, monitor).isDirectory()) {
        copyGeneratedFiles(fileManager, destDir.getChild(name), srcDir.append(name), remoteFiles, 
                           new SubProgressMonitor(monitor, 1));
      } else {
        IFileStore destFile = destDir.getChild(file.getName());
        file.copy(destFile, EFS.OVERWRITE, monitor);
        if (name.endsWith(".cc")) { //$NON-NLS-1$
          String srcPath = file.toURI().getPath();
          String destPath = destFile.toURI().getPath();
          if (srcPath.matches("/.:/.*")) { //$NON-NLS-1$
        	  // FIXME: HACK (bad things will happen with Unix and a path that starts with a /X:/
        	  // On Windows, a "/" is prepended to what would otherwise be an absolute path
        	  srcPath = srcPath.substring(1);
          }
          if (destPath.matches("/.:/.*")) { //$NON-NLS-1$
        	  // FIXME: HACK (bad things will happen with Unix and a path that starts with a /X:/
        	  // On Windows, a "/" is prepended to what would otherwise be an absolute path
        	  destPath = destPath.substring(1);
          }
          remoteFiles.put(srcPath, destPath);
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
        LaunchCore.log(except.getStatus());
        return null;
      }
    }
    return this.fBinaryContainer;
  }
  
  public static IRemoteFileManager getFileManager(final IResourceManager resManager) {
    final IResourceManagerControl rm = (IResourceManagerControl) resManager;
	final IResourceManagerConfiguration rmc = rm.getConfiguration();
	final IRemoteServices remServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmc.getRemoteServicesId());
    IRemoteConnection rmConn = remServices.getConnectionManager().getConnection(rmc.getConnectionName());
    return remServices.getFileManager(rmConn);
  }

  public static IRemoteProcessBuilder getProcessBuilder(final IResourceManager resManager, List<String> command) {
    final IResourceManagerControl rm = (IResourceManagerControl) resManager;
    final IResourceManagerConfiguration rmc = rm.getConfiguration();
    final IRemoteServices remServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmc.getRemoteServicesId());
    IRemoteConnection rmConn = remServices.getConnectionManager().getConnection(rmc.getConnectionName());
    return remServices.getProcessBuilder(rmConn, command);
  }
  
  public static List<String> getAllTokens(final String str) {
    final List<String> tokens = new ArrayList<String>();
    QuotedStringTokenizer qst = new QuotedStringTokenizer(str);
    while (qst.hasMoreTokens())
      tokens.add(qst.nextToken());
    return tokens;
  }

  public static String streamToString(InputStream is) throws IOException {
    byte[] bytes = new byte[is.available()];
    is.read(bytes);
    return new String(bytes);
  }

  /**
   * Build library remotely (compile C++ to .o)
   * @param resourceManager
   * @param remoteFiles
   * @param monitor
   * @throws CoreException
   */
  private void remoteCompilation(final IResourceManager resourceManager,
                                 final Map<String, String> remoteFiles,
                                 final IProgressMonitor monitor) throws CoreException {
	final String resourceManagerName = resourceManager.getName();
    monitor.beginTask(null, remoteFiles.size());
    monitor.subTask(LaunchMessages.CPPB_RemoteCompilTaskName);
    
    final String workspaceDir = getProject().getPersistentProperty(Constants.WORKSPACE_DIR) + '/' + getProject().getName();
    
    final IPreferenceStore store = LaunchCore.getInstance().getPreferenceStore();
    final String x10DistLoc = store.getString(Constants.P_CPP_BUILDER_X10_DIST_LOC);
    final String pgasLoc = store.getString(Constants.P_CPP_BUILDER_PGAS_LOC);
    String compileCmdStart = store.getString(Constants.P_CPP_BUILDER_COMPILE_CMD);
    compileCmdStart = compileCmdStart.replace("${X10_DIST_LOC}", x10DistLoc); //$NON-NLS-1$
    compileCmdStart = compileCmdStart.replace("${PGAS_LOC}", pgasLoc); //$NON-NLS-1$
    final List<String> allObjectFiles = new LinkedList<String>();
    try {
      for (final Map.Entry<String, String> entry : remoteFiles.entrySet()) {
        final String objectFile = entry.getValue().replace(".cc", ".o"); //$NON-NLS-1$ //$NON-NLS-2$
        allObjectFiles.add(objectFile);

        final List<String> command = new ArrayList<String>();
        command.addAll(getAllTokens(compileCmdStart));
        command.add("-I"+workspaceDir); //$NON-NLS-1$
        command.add("-c"); //$NON-NLS-1$
        command.add(entry.getValue());
        command.add("-o"); //$NON-NLS-1$
        command.add(objectFile);
        
        final IRemoteProcessBuilder processBuilder = getProcessBuilder(resourceManager, command);
        
        final IRemoteProcess process = processBuilder.start();
        
        final OutputStream errorStream = new ByteArrayOutputStream();
        final OutputStream outputStream = new ByteArrayOutputStream();
        new RemoteProcessOutputListener(process, outputStream, errorStream).start();
        
        process.waitFor();
        
        final int returnCode = process.exitValue();
        process.destroy();
        
        if (returnCode != 0) {
          IResourceUtils.addMarkerTo(getProject(), NLS.bind(LaunchMessages.CPPB_CompilErrorMsg, new Path(entry.getKey()).lastSegment()), 
                                     IMarker.SEVERITY_ERROR, entry.getKey(), IMarker.PRIORITY_HIGH);
          final MessageConsole messageConsole = ConsoleUtil.findConsole(LaunchMessages.CPPB_ConsoleName);
          final MessageConsoleStream mcStream = messageConsole.newMessageStream();
          mcStream.println(errorStream.toString());
        }
        
        monitor.worked(1);
      }
      
      final String rawArchiveCmd = store.getString(Constants.P_CPP_BUILDER_ARCHIVE_CMD);
      final String firstStep = rawArchiveCmd.replace("$workspace_dir", workspaceDir); //$NON-NLS-1$
      final String archiveCmd = firstStep.replace("$lib_name", "lib" + getProject().getName()); //$NON-NLS-1$ //$NON-NLS-2$

      final List<String> command = new ArrayList<String>();
      command.addAll(getAllTokens(archiveCmd));
      command.addAll(allObjectFiles);
      
      final IRemoteProcessBuilder archiveProcessBuilder = getProcessBuilder(resourceManager, command);
      final IRemoteProcess archiveProcess = archiveProcessBuilder.start();
      final OutputStream errorStream = new ByteArrayOutputStream();
      final OutputStream outputStream = new ByteArrayOutputStream();
      new RemoteProcessOutputListener(archiveProcess, outputStream, errorStream).start();
      archiveProcess.waitFor();
      final int returnCode = archiveProcess.exitValue();
      archiveProcess.destroy();
      if (returnCode != 0) {
        IResourceUtils.addMarkerTo(getProject(), NLS.bind(LaunchMessages.CB_LibCreationError, archiveCmd), 
                                   IMarker.SEVERITY_ERROR, getProject().getFullPath().toString(), IMarker.PRIORITY_HIGH);
        final MessageConsole messageConsole = ConsoleUtil.findConsole(LaunchMessages.CPPB_ConsoleName);
        final MessageConsoleStream mcStream = messageConsole.newMessageStream();
        mcStream.println(errorStream.toString());
      }
    } catch (IOException except) {
      IResourceUtils.addMarkerTo(getProject(), NLS.bind(LaunchMessages.CPPB_RemoteOpError, resourceManagerName), 
                                 IMarker.SEVERITY_ERROR, getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
      LaunchCore.log(IStatus.ERROR, NLS.bind(LaunchMessages.CPPB_RemoteOpError, resourceManagerName), except);
    } catch (InterruptedException except) {
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
        throw new CoreException(new Status(IStatus.ERROR, LaunchCore.PLUGIN_ID, 
                                           NLS.bind(LaunchMessages.CPPB_FileReadingErrorMessage, file),
                                           except));
      }
    }
    return pSources;
  }
  
  private boolean transfer(final IResourceManager resourceManager,
                           final Map<String, String> remoteFiles,
                           final IProgressMonitor monitor) throws CoreException {
	final String resourceManagerName = resourceManager.getName();
    try {
      IRemoteFileManager fileManager = getFileManager(resourceManager);
      final StringBuilder workspaceDir = new StringBuilder();
      workspaceDir.append(getProject().getPersistentProperty(Constants.WORKSPACE_DIR)).append('/')
                  .append(getProject().getName());
      IFileStore dir = fileManager.getResource(workspaceDir.toString());
      if (dir.fetchInfo(EFS.NONE, monitor).exists()) {
        monitor.subTask(LaunchMessages.CPPB_DeletionTaskName);
        dir.delete(EFS.NONE, monitor);
      }

      monitor.subTask(LaunchMessages.CPPB_TransferTaskName);
      final IPath rootDir = getBinaryContainer().getLocation();
      copyGeneratedFiles(fileManager, dir, rootDir, remoteFiles, monitor);
//      catch (IOException except) {
//      IResourceUtils.addMarkerTo(getProject(), NLS.bind(LaunchMessages.CPPB_RemoteOpError, resourceManagerName), 
//                                 IMarker.SEVERITY_ERROR, getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
//      LaunchCore.log(IStatus.ERROR, NLS.bind(LaunchMessages.CPPB_RemoteOpError, resourceManagerName), except);
//      return false;
    } 
    catch (CoreException except) {
      IResourceUtils.addMarkerTo(getProject(), NLS.bind(LaunchMessages.CPPB_RemoteOpError, resourceManagerName), 
                                 IMarker.SEVERITY_ERROR, getProject().getLocation().toString(), IMarker.PRIORITY_HIGH);
      LaunchCore.log(IStatus.ERROR, NLS.bind(LaunchMessages.CPPB_RemoteOpError, resourceManagerName), except);
      return false;
//    } catch (CancelException except) {
//      IResourceUtils.addMarkerTo(getProject(), NLS.bind(LaunchMessages.CPPB_CancelOpMsg, resourceManagerName), 
//                                 IMarker.SEVERITY_WARNING, getProject().getLocation().toString(), IMarker.PRIORITY_LOW);
//      return false;
    }
    return true;
  }

  // --- Fields
  
  private DependencyInfo fDependencyInfo;
  
  private IJavaProject fProjectWrapper;
  
  private IContainer fBinaryContainer;
  
  private Collection<IFile> fSourcesToCompile = new HashSet<IFile>();
    
}
