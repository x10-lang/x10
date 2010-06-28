/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.core.project;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.x10dt.core.Messages;
import org.eclipse.imp.x10dt.core.X10DTCorePlugin;
import org.eclipse.imp.x10dt.core.utils.X10DTCoreConstants;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;

/**
 * Defines the initializer for the X10 Runtime Classpath Container.
 * 
 * @author egeay
 */
public final class X10ClasspathContainerInitializer extends ClasspathContainerInitializer {

  // --- Abstract methods implementation
  
  public void initialize(final IPath containerPath, final IJavaProject project) throws CoreException {
    if (X10DTCoreConstants.X10_CONTAINER_ENTRY_ID.equals(containerPath.toString())) {
      JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { project }, 
                                     new IClasspathContainer[] { new X10Container(containerPath, resolveClassPathEntries()) },
                                     null /* monitor */);
    } else {
      throw new CoreException(new Status(IStatus.ERROR, X10DTCorePlugin.kPluginID, 
                                         NLS.bind(Messages.XCCI_WrongClassPathContainer, containerPath.toString())));
    }
  }
  
  // --- Private code
  
  private IClasspathEntry[] resolveClassPathEntries() throws CoreException {
    final List<IClasspathEntry> cpEntries = new ArrayList<IClasspathEntry>();
    if (! addClassPathEntries(cpEntries, X10_RUNTIME_BUNDLE, CLASSES_DIR)) {
      addClassPathEntries(cpEntries, X10_RUNTIME_BUNDLE, X10_JAR);
    }
    addClassPathEntries(cpEntries, X10_COMMON_BUNDLE, CLASSES_DIR);
    addClassPathEntries(cpEntries, X10_CONSTRAINTS_BUNDLE, CLASSES_DIR);
    return cpEntries.toArray(new IClasspathEntry[cpEntries.size()]);
  }
  
  private boolean addClassPathEntries(final List<IClasspathEntry> cpEntries, final String bundleName, 
                                      final String folder) throws CoreException {
    final Bundle bundle = Platform.getBundle(bundleName);
    if (bundle == null) {
      throw new CoreException(new Status(IStatus.ERROR, X10DTCorePlugin.kPluginID, 
                                         NLS.bind(Messages.XCCI_BundleNotFoundError, bundleName)));
    } else {
      URL wURL = bundle.getResource(folder);
      if (wURL == null) {
        // We access the root of the jar where the resources should be located.
        wURL = bundle.getResource(""); //$NON-NLS-1$
      }
      boolean deployed;
      IPath path;
      try {
        final URL url = FileLocator.resolve(wURL);
        if (url.getProtocol().equals("jar")) { //$NON-NLS-1$
          final JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
          path = new Path(jarConnection.getJarFileURL().getFile());
          deployed = true;
        } else {
          path = new Path(url.getFile());
          deployed = false;
        }
      } catch (IOException except) {
        throw new CoreException(new Status(IStatus.ERROR, X10DTCorePlugin.kPluginID, 
                                           Messages.XCCI_ClasspathResIOError, except));
      }
      cpEntries.add(JavaCore.newLibraryEntry(path, null /* sourceAttachmentPath */, null /* sourceAttachmentRootPath */));
      return deployed;
    }
  }
  
  // --- Fields
  
  private static final String X10_RUNTIME_BUNDLE = "x10.runtime"; //$NON-NLS-1$

  private static final String X10_COMMON_BUNDLE = "x10.common"; //$NON-NLS-1$

  private static final String X10_CONSTRAINTS_BUNDLE = "x10.constraints"; //$NON-NLS-1$

  private static final String CLASSES_DIR = "classes"; //$NON-NLS-1$

  private static final String X10_JAR = "classes/x10.jar"; //$NON-NLS-1$

}
