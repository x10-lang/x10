/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package org.eclipse.imp.x10dt.ui.launch.cpp.platform_conf;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.imp.x10dt.ui.launch.core.utils.ReaderInputStream;
import org.eclipse.imp.x10dt.ui.launch.cpp.CppLaunchCore;
import org.eclipse.imp.x10dt.ui.launch.cpp.LaunchMessages;

/**
 * Factory methods to create instance(s) of {@link IX10PlatformConf} and provide services for this same interface.
 * 
 * @author egeay
 */
public final class X10PlatformConfFactory {
  
  /**
   * Creates a platform configuration that is a reference to a given one.
   * 
   * @param platformConfSource The platform configuration to reference.
   * @return A non-null implementation of {@link IX10PlatformConf}.
   */
  public static IX10PlatformConf createReferenceTo(final IX10PlatformConf platformConfSource) {
    return new ReferencedPlatformConf(platformConfSource);
  }
  
  /**
   * Returns the handle to the X10 platform configuration file for a given project.
   * 
   * @param project The project to consider.
   * @return A non-null {@link IFile} instance.
   */
  public static IFile getFile(final IProject project) {
    return project.getFile(X10_PLATFORM_CONF_FILE);
  }
  
  /**
   * Loads (or creates) the X10 platform configuration file from the JDT file provided and creates as a result 
   * {@link IX10PlatformConf}.
   * 
   * @param file The X10 Platform Configuration file to read.
   * @return A non-null implementation of {@link IX10PlatformConf}.
   * @throws CoreException Occurs if we could not read the file content properly.
   */
  public static IX10PlatformConf load(final IFile file) throws CoreException {
    return new X10PlatformConf(file);
  }
  
  /**
   * Loads (or creates) the X10 platform configuration present at the root of the project provided and creates as a 
   * result {@link IX10PlatformConf}.
   * 
   * @param project The project containing the X10 platform configuration file to read.
   * @return A non-null implementation of {@link IX10PlatformConf}.
   * @throws CoreException Occurs if we could not read the file content properly.
   */
  public static IX10PlatformConf load(final IProject project) throws CoreException {
    return new X10PlatformConf(getFile(project));
  }
  
  /**
   * Saves all the parameters encapsulated in the transmitted platform configuration into the file provided.
   * 
   * @param file The file where all the parameters will be stored.
   * @param platformConf The platform configuration of interest.
   * @throws CoreException Occurs if we could not transfer the stream to the file. Reasons include: 
   * <ul>
   * <li>This resource does not exist.</li>
   * <li>The corresponding location in the local file system is occupied by a directory.</li>
   * <li>The workspace is not in sync with the corresponding location in the local file system and FORCE is not specified.</li>
   * <li>Resource changes are disallowed during certain types of resource change event notification. 
   * See {@link IResourceChangeEvent} for more details.</li>
   * <li>The file modification validator disallowed the change.</li>
   * </ul> 
   * @throws IOException If an I/O error occurs.
   */
  public static void save(final IFile file, final IX10PlatformConf platformConf) throws CoreException, IOException {
    final PipedWriter writer = new PipedWriter();
    final PipedReader reader = new PipedReader(writer);

    final Job job = new WorkspaceJob(LaunchMessages.XPCF_SavePlatformConfFileJobMsg) {
      
      public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
        try {
          platformConf.save(writer);
          writer.close();
        } catch (IOException except) {
          throw new CoreException(new Status(IStatus.ERROR, CppLaunchCore.PLUGIN_ID, 
                                             LaunchMessages.XPCFE_ConfSavingErrorDlgMsg));
        }
        return Status.OK_STATUS;
      }
      
    };
    job.schedule();
  
    if (file.exists()) {
      file.setContents(new ReaderInputStream(reader), IResource.FORCE, new NullProgressMonitor());
    } else {
      file.create(new ReaderInputStream(reader), IResource.FORCE, new NullProgressMonitor());
    }
  }
  
  // --- Private code
  
  private X10PlatformConfFactory() {}
  
  // --- Fields
  
  private static final String X10_PLATFORM_CONF_FILE = "/x10_platform.conf"; //$NON-NLS-1$

}
