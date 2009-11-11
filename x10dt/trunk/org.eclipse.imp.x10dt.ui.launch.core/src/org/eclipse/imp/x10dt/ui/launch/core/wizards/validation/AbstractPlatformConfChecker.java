/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.wizards.validation;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.imp.utils.Pair;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.builder.ELanguage;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IInputListener;
import org.eclipse.imp.x10dt.ui.launch.core.utils.PTPUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.UIUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.WizardUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.X10BuilderUtils;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.ptp.remote.core.IRemoteProcess;
import org.eclipse.ptp.remote.core.IRemoteProcessBuilder;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;

/**
 * Common base for implementations of {@link IPlatformConfChecker}. Typical implementations will extend this base class.
 * 
 * @author egeay
 */
public abstract class AbstractPlatformConfChecker implements IPlatformConfChecker {
  
  // --- Abstract methods definition
  
  protected abstract Pair<String, String> compileX10File(final ELanguage language, final String path, 
                                                         final InputStream sourceInputStream, final String workspaceDir,
                                                         final String x10DistLoc, final String pgasDistLoc, 
                                                         final String[] x10LibsLocs, final IRemoteFileManager fileManager,
                                                         final SubMonitor monitor) throws Exception;
  
  protected abstract boolean isLocalChecker();
  
  // --- IPlatformConfChecker's interface methods implementation

  public final String validateArchiving(final String archiver, final String archivingOptions, 
                                        final SubMonitor monitor) throws Exception {
    monitor.beginTask(null, 10);
    monitor.subTask(Messages.APCC_ArchivingTaskMsg);
    
    final IRemoteFileManager fileManager = this.fRemoteServices.getFileManager(this.fRemoteConnection);
    try {
      return execute(getArchivingCommand(archiver, archivingOptions), fileManager, monitor);
    } catch (Exception except ) {
      fileManager.getResource(this.fWorkDir).delete(EFS.NONE, new NullProgressMonitor());
      throw except;
    }
  }
  
  public final String validateCompilation(final ELanguage language, final String compiler, final String compilingOptions,
                                          final String x10DistLoc, final String pgasDistLoc, final String[] x10HeadersLocs,
                                          final String[] x10LibsLocs, final SubMonitor monitor) throws Exception {
    monitor.beginTask(null, 20);
    monitor.subTask(Messages.APCC_CompilationTaskMsg);
    
    final IRemoteFileManager fileManager = this.fRemoteServices.getFileManager(this.fRemoteConnection);

    try {
      this.fWorkDir = createWorkDir(fileManager, getTempDirectory());
      final String testFilePath = createTestFile(fileManager, monitor.newChild(1));
    
      // X10 compilation
      final Pair<String, String> compilationResults = compileX10File(language, testFilePath, getContentSampleStream(),
                                                                     this.fWorkDir, x10DistLoc, pgasDistLoc,
                                                                     x10LibsLocs, fileManager, monitor.newChild(5));
      if (compilationResults.first != null) {
        fileManager.getResource(this.fWorkDir).delete(EFS.NONE, new NullProgressMonitor());
        return compilationResults.first;
      }
    
      // Compilation of generated file.
      final List<String> command = getCompilationCommand(compiler, compilingOptions, x10HeadersLocs, this.fWorkDir, 
                                                         compilationResults.second);
      return execute(command, fileManager, monitor.newChild(14));
    } catch (Exception except ) {
      fileManager.getResource(this.fWorkDir).delete(EFS.NONE, new NullProgressMonitor());
      throw except;
    }
  }

  public final String validateLinking(final String linker, final String linkingOptions, final String linkingLibs, 
                                      final String[] x10HeadersLocs, final String[] x10LibsLocs, 
                                      final SubMonitor monitor) throws Exception {
    monitor.beginTask(null, 20);
    monitor.subTask(Messages.APCC_LinkingTaskMsg);
    
    final IRemoteFileManager fileManager = this.fRemoteServices.getFileManager(this.fRemoteConnection);
    try {
      return execute(getLinkingCommand(linker, linkingOptions, linkingLibs, x10HeadersLocs, x10LibsLocs), 
                     fileManager, monitor);
    } finally {
      fileManager.getResource(this.fWorkDir).delete(EFS.NONE, new NullProgressMonitor());
    }
  }
  
  // --- Public services
  
  /**
   * Defines the remote services through the resource manager provided.
   * 
   * @param resourceManager The resource manager to consider.
   */
  public final void defineRemoteServices(final IResourceManager resourceManager) {
    final IResourceManagerControl rmControl = (IResourceManagerControl) resourceManager;
    final IResourceManagerConfiguration rmc = rmControl.getConfiguration();
    this.fRemoteServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmc.getRemoteServicesId());
    this.fRemoteConnection = this.fRemoteServices.getConnectionManager().getConnection(rmc.getConnectionName());
  }
  
  // --- Code for descendants
  
  protected final IRemoteProcessBuilder getProcessBuilder(final List<String> command) {
    return this.fRemoteServices.getProcessBuilder(this.fRemoteConnection, command);
  }
  
  protected final IRemoteProcessBuilder getProcessBuilder(final String ... command) {
    return this.fRemoteServices.getProcessBuilder(this.fRemoteConnection, command);
  }
  
  // --- Private code
    
  private String createTestFile(final IRemoteFileManager fileManager, final SubMonitor monitor) throws Exception {
    final String testFilePath = this.fWorkDir + '/' + TEST_FILENAME;
    
    final IFileStore fileStore = fileManager.getResource(testFilePath);
    final OutputStream os = fileStore.openOutputStream(EFS.NONE, monitor);
    final InputStream is = getContentSampleStream();
    try {
      final byte[] b = new byte[4 * 1024];  
      int read;  
      while ((read = is.read(b)) != -1) {  
        os.write(b, 0, read);
      }
    } finally {
      is.close();
      os.close();
      monitor.done();
    }
    return testFilePath;
  }
  
  private String createWorkDir(final IRemoteFileManager fileManager, final String tempDir) throws CoreException {
    final SimpleDateFormat format = new SimpleDateFormat("hh-mm-ss"); //$NON-NLS-1$
    final String dirPath = tempDir + "/x10-validation-" + format.format(new Date()); //$NON-NLS-1$
    fileManager.getResource(dirPath).mkdir(EFS.NONE, new NullProgressMonitor());
    return dirPath;
  }
  
  private String execute(final List<String> command, final IRemoteFileManager fileManager, 
                         final SubMonitor monitor) throws Exception {
    monitor.beginTask(null, 10);
    IRemoteProcess process = null;
    try {
      process = getProcessBuilder(command).directory(fileManager.getResource(this.fWorkDir)).start();

      final StringBuilder errMsgBuilder = new StringBuilder();
      UIUtils.printStream(process.getErrorStream(), new IInputListener() {
        
        public void read(final String line) {
          errMsgBuilder.append(line).append('\n');
        }
        
        public void before() {
        }
        
        public void after() {
        }
        
      });
      
      process.waitFor();
      final int exitValue = process.exitValue();
      monitor.worked(5);
      if (exitValue == 0) {
        return null;
      } else {
        monitor.subTask(Messages.APCC_ProcessErrorStreamMsg);
        monitor.worked(5);
        
        fileManager.getResource(this.fWorkDir).delete(EFS.NONE, new NullProgressMonitor());
        
        return errMsgBuilder.toString();
      }
    } finally {
      if (process != null) {
        process.destroy();
      }
      monitor.done();
    }
  }
  
  private List<String> getArchivingCommand(final String archiver, final String archivingOptions) {
    final List<String> command = new ArrayList<String>();
    command.add(archiver);
    command.addAll(X10BuilderUtils.getAllTokens(archivingOptions));
    // We only support Cygwin on Windows, so next line is safe at this time.
    command.add(this.fWorkDir + TEST_LIB);
    command.add(this.fCompiledFile);
    return command;
  }
  
  private List<String> getCompilationCommand(final String compiler, final String compilingOptions,
                                             final String[] x10HeaderLocs, final String workDirectory,
                                             final String generatedFilePath) {
    final List<String> command = new ArrayList<String>();
    command.add(compiler);
    command.addAll(X10BuilderUtils.getAllTokens(compilingOptions));
    command.add(INCLUDE_OPT + workDirectory);
    for (final String headerLoc : x10HeaderLocs) {
      command.add(INCLUDE_OPT + headerLoc);
    }
    command.add("-c"); //$NON-NLS-1$
    command.add(generatedFilePath);
    command.add("-o"); //$NON-NLS-1$
    this.fCompiledFile = generatedFilePath.replace(".cc", ".o"); //$NON-NLS-1$ //$NON-NLS-2$
    command.add(this.fCompiledFile);
    return command;
  }
  
  private InputStream getContentSampleStream() {
    final String typeName = TEST_FILENAME.substring(0, TEST_FILENAME.lastIndexOf('.'));
    return WizardUtils.createSampleContentStream(null /* packageName */, typeName);
  }
  
  private List<String> getLinkingCommand(final String linker, final String linkingOptions, final String linkingLibs,
                                         final String[] x10HeadersLocs, final String[] x10LibsLocs) {
    final List<String> command = new ArrayList<String>();
    command.add(linker);
    command.addAll(X10BuilderUtils.getAllTokens(linkingOptions));
    command.add(INCLUDE_OPT + this.fWorkDir);
    for (final String headerLoc : x10HeadersLocs) {
      command.add(INCLUDE_OPT + headerLoc);
    }
    command.add("-o"); //$NON-NLS-1$
    command.add(TEST_PROG_NAME);
    command.add(LIB_OPT + this.fWorkDir);
    for (final String libLoc : x10LibsLocs) {
      command.add(LIB_OPT + libLoc);
    }
    command.add(TEST_LIB_LINK);
    command.addAll(X10BuilderUtils.getAllTokens(linkingLibs));
    return command;
  }
  
  private String getTempDirectory() {
    if (isLocalChecker()) {
      return System.getProperty(TMP_DIR_PROP).replace('\\', '/');
    } else {
      final String tmp = PTPUtils.getTempDirectory(this.fRemoteConnection, 
                                                   this.fRemoteServices.getFileManager(this.fRemoteConnection));
      if (tmp == null) {
        throw new RuntimeException(Messages.APCC_NoTempDirError);
      } else {
        return tmp;
      }
    }
  }
  
  // --- Fields
  
  private IRemoteServices fRemoteServices;
  
  private IRemoteConnection fRemoteConnection;
  
  private String fWorkDir;
  
  private String fCompiledFile;
  

  private static final String INCLUDE_OPT = "-I"; //$NON-NLS-1$
  
  private static final String LIB_OPT = "-L"; //$NON-NLS-1$
  
  private static final String TMP_DIR_PROP = "java.io.tmpdir"; //$NON-NLS-1$
  
  private static final String TEST_FILENAME = "Hello.x10"; //$NON-NLS-1$
  
  private static final String TEST_PROG_NAME = "a.out"; //$NON-NLS-1$
  
  private static final String TEST_LIB = "/libHello.a"; //$NON-NLS-1$
  
  private static final String TEST_LIB_LINK = "-lHello"; //$NON-NLS-1$

}
