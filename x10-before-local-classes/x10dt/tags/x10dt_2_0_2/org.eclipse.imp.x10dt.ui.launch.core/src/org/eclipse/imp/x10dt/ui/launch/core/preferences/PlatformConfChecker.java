/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.core.preferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.imp.utils.Pair;
import org.eclipse.imp.x10dt.core.builder.StreamSource;
import org.eclipse.imp.x10dt.ui.launch.core.Messages;
import org.eclipse.imp.x10dt.ui.launch.core.builder.ELanguage;
import org.eclipse.imp.x10dt.ui.launch.core.builder.ICompilerX10ExtInfo;
import org.eclipse.imp.x10dt.ui.launch.core.utils.CollectionUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.FileUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IFilter;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IInputListener;
import org.eclipse.imp.x10dt.ui.launch.core.utils.PTPUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.UIUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.WizardUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.X10BuilderUtils;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.core.elementcontrols.IResourceManagerControl;
import org.eclipse.ptp.core.elements.IResourceManager;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.eclipse.ptp.remote.core.IRemoteProcess;
import org.eclipse.ptp.remote.core.IRemoteProcessBuilder;
import org.eclipse.ptp.remote.core.IRemoteServices;
import org.eclipse.ptp.remote.core.PTPRemoteCorePlugin;
import org.eclipse.ptp.rmsystem.IResourceManagerConfiguration;
import org.osgi.framework.Bundle;

import polyglot.frontend.Compiler;
import polyglot.frontend.Globals;
import polyglot.frontend.Source;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;
import x10.ExtensionInfo;


final class PlatformConfChecker implements IPlatformConfChecker {
  
  PlatformConfChecker(final IResourceManager resourceManager) {
    final IResourceManagerControl rmControl = (IResourceManagerControl) resourceManager;
    final IResourceManagerConfiguration rmc = rmControl.getConfiguration();
    this.fRemoteServices = PTPRemoteCorePlugin.getDefault().getRemoteServices(rmc.getRemoteServicesId());
    this.fRemoteConnection = this.fRemoteServices.getConnectionManager().getConnection(rmc.getConnectionName());
  }
  
  // --- IPlatformConfChecker's interface methods implementation

  public String validateArchiving(final String archiver, final String archivingOptions, 
                                  final SubMonitor monitor) throws Exception {
    monitor.beginTask(null, 10);
    monitor.subTask(Messages.APCC_ArchivingTaskMsg);
    
    final IRemoteFileManager fileManager = this.fRemoteServices.getFileManager(this.fRemoteConnection);
    try {
      return execute(getArchivingCommand(archiver, archivingOptions), fileManager, monitor);
    } catch (Exception except) {
      fileManager.getResource(this.fWorkDir).delete(EFS.NONE, new NullProgressMonitor());
      throw except;
    }
  }
  
  public String validateCompilation(final ELanguage language, final String compiler, final String compilingOptions,
                                    final String x10DistLoc, final String pgasDistLoc, final String[] x10HeadersLocs,
                                    final String[] x10LibsLocs, final SubMonitor monitor) throws Exception {
    monitor.beginTask(null, 20);
    monitor.subTask(Messages.APCC_CompilationTaskMsg);
    
    final IRemoteFileManager fileManager = this.fRemoteServices.getFileManager(this.fRemoteConnection);

    try {
      final String uniqueDirName = createUniqueDirName();
      this.fWorkDir = String.format(PATH_SEP_STRFORMAT, getTempDirectory(), uniqueDirName);
      fileManager.getResource(this.fWorkDir).mkdir(EFS.NONE, new NullProgressMonitor());
      final File testFilePath = createTestFile(fileManager, uniqueDirName, monitor.newChild(1));

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
    } catch (Exception except) {
      fileManager.getResource(this.fWorkDir).delete(EFS.NONE, new NullProgressMonitor());
      throw except;
    }
  }

  public String validateLinking(final String linker, final String linkingOptions, final String linkingLibs, 
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
  
  // --- Private code
  
  @SuppressWarnings("unchecked")
  private Pair<String, String> compileX10File(final ELanguage language, final File testFilePath, 
                                              final InputStream sourceInputStream, final String workspaceDir,
                                              final String x10DistLoc, final String pgasDistLoc, 
                                              final String[] x10LibsLocs, final IRemoteFileManager fileManager, 
                                              final SubMonitor monitor) throws Exception {
    monitor.beginTask(Messages.APCC_GeneratingFilesMsg, 10);
    
    final Bundle x10RuntimeBundle = Platform.getBundle(X10_RUNTIME_BUNDLE);
    final File x10RuntimeDir = getDirectory(x10RuntimeBundle);
    
    final File localTestDir = testFilePath.getParentFile();
    final StringBuilder classPathBuider = new StringBuilder();
    classPathBuider.append(localTestDir.getAbsolutePath());
    final IFilter<File> libFilter = new LibFilter();
    for (final String x10LibsLoc : x10LibsLocs) {
      for (final File libFile : FileUtils.collect(new File(x10LibsLoc), libFilter, false /* recurse */)) {
        classPathBuider.append(File.pathSeparatorChar).append(libFile.getAbsolutePath());
      }
    }
    classPathBuider.append(File.pathSeparatorChar).append(x10RuntimeDir.getAbsolutePath());
    
    final List<File> srcPath = new ArrayList<File>();
    srcPath.add(localTestDir);
    srcPath.add(x10RuntimeDir);
    
    final ICompilerX10ExtInfo compilerExtInfo = X10BuilderUtils.createCompilerX10ExtInfo(language);
    final ExtensionInfo extInfo = compilerExtInfo.createExtensionInfo(classPathBuider.toString(), srcPath, 
                                                                      localTestDir.getAbsolutePath(), 
                                                                      true /* withMainMethod */, monitor);
    final CompilationErrorQueue errorQueue = new CompilationErrorQueue();
    final Compiler compiler = new Compiler(extInfo, errorQueue);
    Globals.initialize(compiler);
    
    compiler.compile(Arrays.<Source>asList(new StreamSource(sourceInputStream, testFilePath.getAbsolutePath())));
    
    final boolean isLocal = PTPRemoteCorePlugin.getDefault().getDefaultServices().equals(this.fRemoteServices);
    if (! isLocal) {
      final IFileSystem fileSystem = EFS.getLocalFileSystem();
      final IFileStore destDir = fileManager.getResource(workspaceDir);
      try {
        for (final Object fileName : compiler.outputFiles()) {
          for (final File generatedFile : localTestDir.listFiles(new CppFileNameFilter((String) fileName))) {
            final IFileStore curFileStore = fileSystem.getStore(new Path(generatedFile.getAbsolutePath()));
            curFileStore.copy(destDir.getChild(curFileStore.getName()), EFS.OVERWRITE, null);            
          }
        }
      } finally {
        FileUtils.deleteDirectory(localTestDir);
      }
    }

    monitor.done();
    
    if (errorQueue.hasErrors()) {
      return new Pair<String, String>(errorQueue.getAllErrors(), null);
    } else {
      final Collection<String> ccFile = CollectionUtils.filter(compiler.outputFiles(), new CCFileFilter());
      if (ccFile.isEmpty()) {
        return new Pair<String, String>(Messages.APCC_NoGeneratedFilesError, null);
      } else {
        return new Pair<String, String>(null, ccFile.iterator().next());
      }
    }
  }
  
  private File createTestFile(final IRemoteFileManager fileManager, final String uniqueDirName,
                              final SubMonitor monitor) throws Exception {
    final String dirPath = String.format(PATH_SEP_STRFORMAT, System.getProperty(TMPDIR_JAVA_ENV_VAR), uniqueDirName);
    final String testFilePath = String.format(PATH_SEP_STRFORMAT, dirPath, TEST_FILENAME);
    final File localTestFileDir = new File(dirPath);
    localTestFileDir.mkdirs();
    
    final OutputStream os = new FileOutputStream(new File(testFilePath));
    final InputStream is = getContentSampleStream();
    try {
      final byte[] b = new byte[1024];  
      int read;  
      while ((read = is.read(b)) != -1) {  
        os.write(b, 0, read);
      }
    } finally {
      is.close();
      os.close();
      monitor.done();
    }
    return new File(testFilePath);
  }
  
  private String createUniqueDirName() {
    final SimpleDateFormat format = new SimpleDateFormat("hh-mm-ss"); //$NON-NLS-1$
    final String userName = System.getProperty("user.name").replace(" ", "_"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    return String.format("x10-validation-%s-%s", userName, format.format(new Date())); //$NON-NLS-1$
  }
  
  private String execute(final List<String> command, final IRemoteFileManager fileManager, 
                         final SubMonitor monitor) throws Exception {
    monitor.beginTask(null, 1);
    IRemoteProcess process = null;
    try {
      process = getProcessBuilder(command).directory(fileManager.getResource(this.fWorkDir)).start();

      final StringBuilder errMsgBuilder = new StringBuilder();
      UIUtils.printStream(process.getInputStream(), process.getErrorStream(), new IInputListener() {

        public void after() {
        }
        
        public void before() {
          this.fCounter = 0;
        }

        public void read(final String line) {
        }
        
        public void readError(final String line) {
          if (this.fCounter == 0) {
            errMsgBuilder.append(getCommandAsString(command));
            this.fCounter = 1;
          }
          errMsgBuilder.append(line).append('\n');
        }
        
        // --- Fields
        
        int fCounter;
        
      });
      
      process.waitFor();
      final int exitValue = process.exitValue();
      monitor.worked(1);
      return (exitValue == 0) ? null : errMsgBuilder.toString();
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
  
  private String getCommandAsString(final List<String> command) {
    final StringBuilder sb = new StringBuilder();
    int i = 0;
    for (final String str : command) {
      if (i == 0) {
        i = 1;
        sb.append(Messages.APCC_ExecuteCommand);
      } else {
        sb.append(' ');
      }
      sb.append(str);
    }
    sb.append('\n').append('\n');
    return sb.toString();
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
  
  private File getDirectory(final Bundle bundle) throws IOException {
    URL wURL = bundle.getResource(SRC_X10_DIR);
    if (wURL == null) {
      // We access the root of the jar where the resources should be located.
      wURL = bundle.getResource(""); //$NON-NLS-1$
    }
    final URL url = FileLocator.resolve(wURL);
    if (url.getProtocol().equals("jar")) { //$NON-NLS-1$
      final JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
      return new File(jarConnection.getJarFileURL().getFile());
    } else {
      return new File(url.getFile());
    }
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
  
  private IRemoteProcessBuilder getProcessBuilder(final List<String> command) {
    return this.fRemoteServices.getProcessBuilder(this.fRemoteConnection, command);
  }
  
  private String getTempDirectory() {
    final String tmp = PTPUtils.getTempDirectory(this.fRemoteConnection, 
                                                 this.fRemoteServices.getFileManager(this.fRemoteConnection));
    if (tmp == null) {
      throw new RuntimeException(Messages.APCC_NoTempDirError);
    } else {
      return tmp;
    }
  }
  
  // --- Private classes
  
  private static final class CompilationErrorQueue implements ErrorQueue {
    
    // --- Interface methods implementation

    public void enqueue(final int type, final String message) {
      enqueue(new ErrorInfo(type, message, null));
    }

    public void enqueue(final int type, final String message, final Position position) {
      enqueue(new ErrorInfo(type, message, position));
    }

    public void enqueue(final ErrorInfo errorInfo) {
      ++this.fCounter;
      if (errorInfo.getErrorKind() >= 1 && errorInfo.getErrorKind() <= 6) {
        if (errorInfo.getPosition() == null) {
          this.fErrorBuilder.append(NLS.bind(Messages.APCC_CompilErrorWithoutPos, errorInfo.getErrorString(), 
                                             errorInfo.getMessage()));
        } else {
          this.fErrorBuilder.append(NLS.bind(Messages.APCC_CompilErrorWithPos, 
                                             new String[] { errorInfo.getErrorString(), errorInfo.getPosition().toString(), 
                                                            errorInfo.getMessage() }));
        }
      }
    }

    public int errorCount() {
      return this.fCounter;
    }

    public void flush() {
      // Do nothing. We take all the errors.
    }

    public boolean hasErrors() {
      return this.fCounter > 0;
    }
    
    // --- Internal services
    
    String getAllErrors() {
      return this.fErrorBuilder.toString();
    }
    
    // --- Fields
    
    private int fCounter;
    
    private final StringBuilder fErrorBuilder = new StringBuilder();
    
  }
  
  private static final class LibFilter implements IFilter<File> {
    
    // --- Interface methods implementation

    public boolean accepts(final File file) {
      return file.getName().endsWith(".a"); //$NON-NLS-1$
    }
    
  }
  
  private static final class CCFileFilter implements IFilter<String> {
    
    // --- Interface methods implementation

    public boolean accepts(final String fileName) {
      return fileName.endsWith(CC_EXT);
    }
    
  }
  
  private static final class CppFileNameFilter implements FilenameFilter {

    CppFileNameFilter(final String fileName) {
      this.fFileNameStart = fileName.substring(0, fileName.lastIndexOf('.'));
    }
    
    // --- Interface methods implementation
    
    public boolean accept(final File dir, final String name) {
      return name.startsWith(this.fFileNameStart) && (name.endsWith(CC_EXT) || name.endsWith(H_EXT) || 
                             name.endsWith(INC_EXT));
    }
    
    // --- Fields
    
    private final String fFileNameStart;
    
  }
  
  // --- Fields
  
  private final IRemoteServices fRemoteServices;
  
  private final IRemoteConnection fRemoteConnection;
  
  private String fWorkDir;
  
  private String fCompiledFile;
  

  private static final String INCLUDE_OPT = "-I"; //$NON-NLS-1$
  
  private static final String LIB_OPT = "-L"; //$NON-NLS-1$
  
  private static final String TEST_FILENAME = "Hello.x10"; //$NON-NLS-1$
  
  private static final String TEST_PROG_NAME = "a.out"; //$NON-NLS-1$
  
  private static final String TEST_LIB = "/libHello.a"; //$NON-NLS-1$
  
  private static final String TEST_LIB_LINK = "-lHello"; //$NON-NLS-1$
  
  private static final String X10_RUNTIME_BUNDLE = "x10.runtime"; //$NON-NLS-1$
  
  private static final String SRC_X10_DIR = "src-x10"; //$NON-NLS-1$
  
  private static final String TMPDIR_JAVA_ENV_VAR = "java.io.tmpdir"; //$NON-NLS-1$
  
  private static final String CC_EXT = ".cc"; //$NON-NLS-1$
  
  private static final String H_EXT = ".h"; //$NON-NLS-1$
  
  private static final String INC_EXT = ".inc"; //$NON-NLS-1$

  private static final String PATH_SEP_STRFORMAT = "%s/%s"; //$NON-NLS-1$

}
