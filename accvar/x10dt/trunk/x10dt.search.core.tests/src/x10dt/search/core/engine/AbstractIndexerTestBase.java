/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;

import x10dt.core.X10DTCorePlugin;
import x10dt.core.utils.X10DTCoreConstants;
import x10dt.tests.services.pde.utils.ProjectUtils;

@SuppressWarnings("nls")
abstract class AbstractIndexerTestBase implements IResourceChangeListener {
  
  // --- Interface methods implementation
  
  public final void resourceChanged(final IResourceChangeEvent event) {
    final IResourceDelta rootResourceDelta = event.getDelta();
    for (final IResourceDelta resourceDelta : rootResourceDelta.getAffectedChildren()) {
      if ((resourceDelta.getResource().getType() == IResource.PROJECT) && 
          (resourceDelta.getKind() == IResourceDelta.CHANGED)) {
        final IProject project = (IProject) resourceDelta.getResource();
        try {
          if (project.hasNature(X10DTCorePlugin.X10_CPP_PRJ_NATURE_ID) || 
              project.hasNature(X10DTCorePlugin.X10_PRJ_JAVA_NATURE_ID)) {
            final IJavaProject javaProject = JavaCore.create(project);
            for (final IClasspathEntry cpEntry : javaProject.getRawClasspath()) {
              if (X10DTCoreConstants.X10_CONTAINER_ENTRY_ID.equals(cpEntry.getPath().toString())) {
                this.fIsProjectReady = true;
                break;
              }
            }
          }
        } catch (CoreException except) {
          assertTrue(except.getMessage(), false);
        }
      }
    }
  }
  
  // --- Code for sub-classes
  
  protected final IProject createProjectFromX10Dist(final String projectName, final String data,
                                                    final EProjectBackEnd backEnd) throws CoreException, URISyntaxException, 
                                                                                          IOException {
    return createProject(projectName, "x10.dist", data, backEnd);
  }
  
  protected final IProject createProjectFromX10Tests(final String projectName, final String data,
                                                     final EProjectBackEnd backEnd) throws CoreException, URISyntaxException, 
                                                                                           IOException {
    return createProject(projectName, "x10.tests", data, backEnd);
  }
  
  protected final IProject createProject(final String projectName, final String data,
                                         final EProjectBackEnd backEnd) throws CoreException, URISyntaxException, IOException {
    return createProject(projectName, "x10dt.search.core.tests", data, backEnd);
  }
  
  @SuppressWarnings("null")
  protected final IProject createProject(final String projectName, final String bundleName, final String data,
                                         final EProjectBackEnd backEnd) throws CoreException, URISyntaxException, IOException {
    this.fIsProjectReady = false;
    ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    
    final Bundle bundle = Platform.getBundle(bundleName);
    assertNotNull(String.format("Unable to find bundle '%s'", bundleName), bundle);
    ResourcesPlugin.getWorkspace().getDescription().setAutoBuilding(false);
    final IProject project;
    switch (backEnd) {
      case JAVA:
        project = ProjectUtils.createX10ProjectJavaBackEnd(projectName, bundle, data);
        break;
      case CPP:
        project = ProjectUtils.createX10ProjectCppBackEnd(projectName, bundle, data);
        break;
      default:
        project = null;
    }
    assertNotNull("X10 Project Back-End not supported", project); 
    assertTrue(project.exists());
    
    while (! this.fIsProjectReady) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException except) {
        // Simply forgets.
      }
    }
    
    ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    
    return project;
  }
  
  // --- Fields
  
  private boolean fIsProjectReady;

}
