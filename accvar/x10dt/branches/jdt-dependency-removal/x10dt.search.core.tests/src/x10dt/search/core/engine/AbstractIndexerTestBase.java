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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import x10dt.tests.services.pde.utils.ProjectUtils;

@SuppressWarnings("nls")
abstract class AbstractIndexerTestBase {
  
  // --- Code for descendants
  
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
    
    return project;
  }

}
