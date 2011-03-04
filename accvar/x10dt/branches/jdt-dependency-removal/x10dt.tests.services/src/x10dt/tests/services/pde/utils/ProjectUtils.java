/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.tests.services.pde.utils;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.smapifier.builder.SmapiProjectNature;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;

import x10dt.core.utils.X10DTCoreConstants;
import x10dt.tests.services.TestsServicesActivator;
import x10dt.ui.launch.core.LaunchCore;

/**
 * Utility methods to create X10 project(s) for PDE test cases.
 * 
 * @author egeay
 */
public final class ProjectUtils {
  
  /**
   * Copies some file content into the project source folder root and adds it to the Eclipse project model. 
   * 
   * @param project The project name to use.
   * @param bundle The bundle to consider for accessing the resources.
   * @param bundleEntryPath The path to use for collecting the resources within the bundle.
   * @throws CoreException Occurs if we could not create the project or some its structures.
   * @throws URISyntaxException May occur when accessing the data folder location.
   * @throws IOException Occurs if we could not copy the sources to the target destination.
   */
  public static void addFileToProject(final IProject project, final Bundle bundle, 
                                      final String bundleEntryPath) throws URISyntaxException, IOException, CoreException {
   final URL url = bundle.getEntry(bundleEntryPath);
   assertNotNull(NLS.bind("Could not find ''{0}'' for bundle ''{1}''", bundleEntryPath, bundle.getSymbolicName()), url); //$NON-NLS-1$
   final URL finalURL;
   if (TestsServicesActivator.getContext() == null) {
     finalURL = url;
   } else {
     finalURL = FileLocator.resolve(url);
   }
   final File dataFile = new File(finalURL.toURI());
   final Collection<File> files = new ArrayList<File>();
   if (dataFile.isDirectory()) {
     for (final File file : dataFile.listFiles()) {
       files.add(file);
     }
   } else {
     files.add(dataFile);
   }
   final File srcFolder = new File(project.getLocation().toFile(), "src"); //$NON-NLS-1$
   for (final File file : files) {
     if (file.isFile()) {
       final InputStream in = new FileInputStream(file);
       final OutputStream out = new FileOutputStream(new File(srcFolder, file.getName()));
       try {
         byte[] buf = new byte[1024];
         int len;
         while ((len = in.read(buf)) > 0) {
           out.write(buf, 0, len);
         }
       } finally {
         in.close();
         out.close();
       }
     }
   }
 }
  
  /**
   * Creates an X10 project with C++ back-end with the name provided.
   * 
   * @param name The project name to use.
   * @return A non-null {@link IProject} instance once it has been created.
   * @throws CoreException Occurs if we could not create the project or some its structures.
   */
  public static IProject createX10ProjectCppBackEnd(final String name) throws CoreException {
    return createX10Project(name, LaunchCore.X10_CPP_PRJ_NATURE_ID);
  }
  
  /**
   * Creates an X10 project with C++ back-end with the name provided and with the sources collected from a given data
   * directory.
   * 
   * @param name The project name to use.
   * @param bundle The bundle to consider for accessing the resources.
   * @param bundleEntryPath The path to use for collecting the resources within the bundle.
   * @return A non-null {@link IProject} instance once it has been created and populated.
   * @throws CoreException Occurs if we could not create the project or some its structures.
   * @throws URISyntaxException May occur when accessing the data folder location.
   * @throws IOException Occurs if we could not copy the sources to the target destination.
   */
  public static IProject createX10ProjectCppBackEnd(final String name, final Bundle bundle,
                                                    final String bundleEntryPath) throws CoreException, URISyntaxException, 
                                                                                         IOException {
    final IProject project = createX10Project(name, LaunchCore.X10_CPP_PRJ_NATURE_ID);
    addFileToProject(project, bundle, bundleEntryPath);
    return project;
  }
  
  /**
   * Creates an X10 project with Java back-end with the name provided.
   * 
   * @param name The project name to use.
   * @return A non-null {@link IProject} instance once it has been created.
   * @throws CoreException
   */
  public static IProject createX10ProjectJavaBackEnd(final String name) throws CoreException {
    return createX10Project(name, LaunchCore.X10_PRJ_JAVA_NATURE_ID);
  }
  
  /**
   * Creates an X10 project with Java back-end with the name provided and with the sources collected from a given data
   * directory.
   * 
   * @param name The project name to use.
   * @param bundle The bundle to consider for accessing the resources.
   * @param bundleEntryPath The path to use for collecting the resources within the bundle.
   * @return A non-null {@link IProject} instance once it has been created and populated.
   * @throws CoreException Occurs if we could not create the project or some its structures.
   * @throws URISyntaxException May occur when accessing the data folder location.
   * @throws IOException Occurs if we could not copy the sources to the target destination.
   */
  public static IProject createX10ProjectJavaBackEnd(final String name, final Bundle bundle,
                                                     final String bundleEntryPath) throws CoreException, URISyntaxException, 
                                                                                          IOException {
    final IProject project = createX10Project(name, LaunchCore.X10_PRJ_JAVA_NATURE_ID);
    addFileToProject(project, bundle, bundleEntryPath);
    return project;
  }
  
  // --- Private code
  
  private static IProject createX10Project(final String name, final String backEndNature) throws CoreException {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
    if (! project.exists()) {
      project.create(null);
      project.open(null);
      
      final IProjectDescription description = project.getDescription();
      description.setNatureIds(new String[] {
        backEndNature, SmapiProjectNature.k_natureID                        
      });
      
      final IPath srcPath = new Path('/' + name + "/src"); //$NON-NLS-1$
      final IPath binPath = new Path('/' + name + "/bin"); //$NON-NLS-1$
      final IFolder srcFolder = project.getFolder(new Path("src")); //$NON-NLS-1$
      srcFolder.create(false /* force */, true /* local */, null);
      final IFolder binFolder = project.getFolder(new Path("bin")); //$NON-NLS-1$
      binFolder.create(false /* force */, true /* local */, null);
      
      project.setDescription(description, null);
      
      try {
		  final ISourceProject javaProject = ModelFactory.create(project);
		  final List<IPathEntry> entries = new ArrayList<IPathEntry>();
		  entries.add(ModelFactory.createSourceEntry(srcPath, null));
		  entries.add(ModelFactory.createContainerEntry(new Path(X10DTCoreConstants.X10_CONTAINER_ENTRY_ID)));
		  javaProject.setBuildPath(LanguageRegistry.findLanguage("X10"), entries, binPath, null);
		} catch (ModelException e) {
			throw ModelFactory.createCoreException(e);
		}
    }
    return project;
  }

}
