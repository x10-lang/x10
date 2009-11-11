/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.validation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.imp.utils.Pair;
import org.eclipse.imp.x10dt.core.builder.StreamSource;
import org.eclipse.imp.x10dt.ui.launch.core.builder.ELanguage;
import org.eclipse.imp.x10dt.ui.launch.core.builder.ICompilerX10ExtInfo;
import org.eclipse.imp.x10dt.ui.launch.core.utils.CollectionUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.FileUtils;
import org.eclipse.imp.x10dt.ui.launch.core.utils.IFilter;
import org.eclipse.imp.x10dt.ui.launch.core.utils.X10BuilderUtils;
import org.eclipse.imp.x10dt.ui.launch.core.wizards.validation.AbstractPlatformConfChecker;
import org.eclipse.imp.x10dt.ui.launch.core.wizards.validation.IPlatformConfChecker;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ptp.remote.core.IRemoteFileManager;
import org.osgi.framework.Bundle;

import polyglot.frontend.Compiler;
import polyglot.frontend.Globals;
import polyglot.frontend.Source;
import polyglot.util.ErrorInfo;
import polyglot.util.ErrorQueue;
import polyglot.util.Position;
import x10.ExtensionInfo;

/**
 * An implementation of {@link IPlatformConfChecker} for local validation.
 * 
 * @author egeay
 */
public final class LocalPlatformConfChecker extends AbstractPlatformConfChecker implements IPlatformConfChecker {

  // --- Abstract methods implementation
  
  @SuppressWarnings("unchecked")
  protected Pair<String, String> compileX10File(final ELanguage language, final String testFilePath, 
                                                final InputStream sourceInputStream, final String workspaceDir,
                                                final String x10DistLoc, final String pgasDistLoc, 
                                                final String[] x10LibsLocs, final IRemoteFileManager fileManager, 
                                                final SubMonitor monitor) throws Exception {
    monitor.beginTask(LaunchMessages.LPCC_GeneratingFilesMsg, 10);
    
    final ICompilerX10ExtInfo compilerExtInfo = X10BuilderUtils.createCompilerX10ExtInfo(language);
    
    final Bundle x10RuntimeBundle = Platform.getBundle(X10_RUNTIME_BUNDLE);
    final File x10RuntimeDir = getDirectory(x10RuntimeBundle);
    
    final StringBuilder classPathBuider = new StringBuilder();
    classPathBuider.append(workspaceDir);
    final IFilter<File> libFilter = new LibFilter();
    for (final String x10LibsLoc : x10LibsLocs) {
      for (final File libFile : FileUtils.collect(new File(x10LibsLoc), libFilter, false /* recurse */)) {
        classPathBuider.append(File.pathSeparatorChar).append(libFile.getAbsolutePath());
      }
    }
    classPathBuider.append(File.pathSeparatorChar).append(x10RuntimeDir.getAbsolutePath());
    
    final List<File> srcPath = new ArrayList<File>();
    srcPath.add(new File(workspaceDir));
    srcPath.add(x10RuntimeDir);
    
    final ExtensionInfo extInfo = compilerExtInfo.createExtensionInfo(classPathBuider.toString(), srcPath, workspaceDir, 
                                                                      true /* withMainMethod */, monitor);
    final CompilationErrorQueue errorQueue = new CompilationErrorQueue();
    final Compiler compiler = new Compiler(extInfo, errorQueue);
    Globals.initialize(compiler);
    
    compiler.compile(Arrays.<Source>asList(new StreamSource(sourceInputStream, new File(testFilePath).getAbsolutePath())));

    monitor.done();
    
    if (errorQueue.hasErrors()) {
      return new Pair<String, String>(errorQueue.getAllErrors(), null);
    } else {
      final Collection<String> ccFile = CollectionUtils.filter(compiler.outputFiles(), new CCFileFilter());
      if (ccFile.isEmpty()) {
        return new Pair<String, String>(LaunchMessages.LPCC_NoGeneratedFilesError, null);
      } else {
        return new Pair<String, String>(null, ccFile.iterator().next());
      }
    }
  }
  
  protected boolean isLocalChecker() {
    return true;
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
          this.fErrorBuilder.append(NLS.bind(LaunchMessages.LPCC_CompilErrorWithoutPos, errorInfo.getErrorString(), 
                                             errorInfo.getMessage()));
        } else {
          this.fErrorBuilder.append(NLS.bind(LaunchMessages.LPCC_CompilErrorWithPos, 
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
  
  static final class CCFileFilter implements IFilter<String> {
    
    // --- Interface methods implementation

    public boolean accepts(final String fileName) {
      return fileName.endsWith(".cc"); //$NON-NLS-1$
    }
    
  }
  
  // --- Fields
  
  private static final String X10_RUNTIME_BUNDLE = "x10.runtime"; //$NON-NLS-1$
  
  private static final String SRC_X10_DIR = "src-x10"; //$NON-NLS-1$

}
