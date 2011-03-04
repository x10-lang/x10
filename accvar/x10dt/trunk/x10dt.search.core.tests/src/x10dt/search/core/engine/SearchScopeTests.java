/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static x10dt.search.core.utils.TestUtils.assertLocation;
import static x10dt.search.core.utils.TestUtils.assertPath;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.search.core.engine.scope.SearchScopeFactory;
import x10dt.search.core.engine.scope.X10SearchScope;

/**
 * Test cases for {@link X10SearchEngine} class around the scoping abilities.
 * 
 * @author egeay
 */
@SuppressWarnings("nls")
public final class SearchScopeTests extends AbstractIndexerTestBase {
    
  // --- Test cases

  @Test public void searchInAppScope() throws Exception {
    final IProject project = createProjectFromX10Dist(PROJECT_TEST, "samples/MontyPi.x10", EProjectBackEnd.JAVA);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.APPLICATION, project);
    
    final ITypeInfo[] helloTypeInfo = X10SearchEngine.getTypeInfo(scope, MONTYPI_CLASS, new NullProgressMonitor());
    assertEquals(1, helloTypeInfo.length);
    assertLocation(helloTypeInfo[0].getLocation(), MONTYPI_LOC, 21, 41);
    assertPath(PROJECT_TEST, MONTYPI_LOC, helloTypeInfo[0].getCompilationUnit().getPath());
    assertNull(helloTypeInfo[0].getDeclaringType());
  }
  
  @Test public void searchInLibRuntimeScope() throws Exception {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_TEST);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.LIBRARY | X10SearchScope.RUNTIME, 
                                                                          project);
    
    final ITypeInfo[] helloTypeInfo = X10SearchEngine.getTypeInfo(scope, MONTYPI_CLASS, new NullProgressMonitor());
    assertEquals(0, helloTypeInfo.length);
  }
  
  @Test public void searchWithTwoProjectsSelected() throws Exception {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_TEST);
    final IProject project2 = createProjectFromX10Dist(PROJECT_TEST2, "samples/KMeans.x10", EProjectBackEnd.JAVA);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project, project2);
    
    final ITypeInfo[] kMeansTypeInfo = X10SearchEngine.getTypeInfo(scope, KMEANS_CLASS, new NullProgressMonitor());
    assertEquals(1, kMeansTypeInfo.length);
    assertLocation(kMeansTypeInfo[0].getLocation(), KMEANS_LOC, 22, 145);
    
    final ITypeInfo[] helloTypeInfo = X10SearchEngine.getTypeInfo(scope, MONTYPI_CLASS, new NullProgressMonitor());
    assertEquals(1, helloTypeInfo.length);
    assertLocation(helloTypeInfo[0].getLocation(), MONTYPI_LOC, 21, 41);
  }
  
  @Test public void searchWithInvalidFileSelected() throws Exception {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_TEST);
    final IProject project2 = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_TEST2);
    final IFile helloFile = project.getFile(new Path("src/WrongPath"));
    final IFile kMeansFile = project2.getFile(new Path(KMEANS_LOC));
    
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, helloFile, kMeansFile);
    
    final ITypeInfo[] kMeansTypeInfo = X10SearchEngine.getTypeInfo(scope, KMEANS_CLASS, new NullProgressMonitor());
    assertEquals(1, kMeansTypeInfo.length);
    assertLocation(kMeansTypeInfo[0].getLocation(), KMEANS_LOC, 22, 145);
    
    final ITypeInfo[] helloTypeInfo = X10SearchEngine.getTypeInfo(scope, MONTYPI_CLASS, new NullProgressMonitor());
    assertEquals(0, helloTypeInfo.length);
  }
  
  @Test public void searchWithValidFilesSelected() throws Exception {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_TEST);
    final IProject project2 = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_TEST2);
    final IFile helloFile = project.getFile(new Path(MONTYPI_LOC));
    final IFile kMeansFile = project2.getFile(new Path(KMEANS_LOC));
    
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, helloFile, kMeansFile);
    
    final ITypeInfo[] kMeansTypeInfo = X10SearchEngine.getTypeInfo(scope, KMEANS_CLASS, new NullProgressMonitor());
    assertEquals(1, kMeansTypeInfo.length);
    assertLocation(kMeansTypeInfo[0].getLocation(), KMEANS_LOC, 22, 145);
    
    final ITypeInfo[] helloTypeInfo = X10SearchEngine.getTypeInfo(scope, MONTYPI_CLASS, new NullProgressMonitor());
    assertEquals(1, helloTypeInfo.length);
    assertLocation(helloTypeInfo[0].getLocation(), MONTYPI_LOC, 21, 41);
  }
  
  @Test public void searchWithFileAndAllMask() throws Exception {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_TEST);
    final IFile helloFile = project.getFile(new Path(MONTYPI_LOC));
    
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, helloFile);
    
    final ITypeInfo[] helloTypeInfo = X10SearchEngine.getTypeInfo(scope, MONTYPI_CLASS, new NullProgressMonitor());
    assertEquals(1, helloTypeInfo.length);
    assertLocation(helloTypeInfo[0].getLocation(), MONTYPI_LOC, 21, 41);
    
    final ITypeInfo[] hashMapInfo = X10SearchEngine.getTypeInfo(scope, HASHMAP_TYPE, new NullProgressMonitor());
    assertEquals(1, hashMapInfo.length);
    assertNotNull(hashMapInfo[0].getLocation());
  }
  
  @Test public void searchWithFileAndAppMask() throws Exception {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_TEST);
    final IFile helloFile = project.getFile(new Path(MONTYPI_LOC));
    
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.APPLICATION, helloFile);
    
    final ITypeInfo[] helloTypeInfo = X10SearchEngine.getTypeInfo(scope, MONTYPI_CLASS, new NullProgressMonitor());
    assertEquals(1, helloTypeInfo.length);
    assertLocation(helloTypeInfo[0].getLocation(), MONTYPI_LOC, 21, 41);
    
    final ITypeInfo[] hashMapInfo = X10SearchEngine.getTypeInfo(scope, HASHMAP_TYPE, new NullProgressMonitor());
    assertEquals(0, hashMapInfo.length);
  }
  
  @Test public void searchWithWorkspaceScopeAndAllMask() throws Exception {
    final IX10SearchScope scope = SearchScopeFactory.createWorkspaceScope(X10SearchScope.ALL);
    
    final ITypeInfo[] kMeansTypeInfo = X10SearchEngine.getTypeInfo(scope, KMEANS_CLASS, new NullProgressMonitor());
    assertEquals(1, kMeansTypeInfo.length);
    assertLocation(kMeansTypeInfo[0].getLocation(), KMEANS_LOC, 22, 145);
    
    final ITypeInfo[] helloTypeInfo = X10SearchEngine.getTypeInfo(scope, MONTYPI_CLASS, new NullProgressMonitor());
    assertEquals(1, helloTypeInfo.length);
    assertLocation(helloTypeInfo[0].getLocation(), MONTYPI_LOC, 21, 41);
    
    final ITypeInfo[] hashMapInfo = X10SearchEngine.getTypeInfo(scope, HASHMAP_TYPE, new NullProgressMonitor());
    assertEquals(1, hashMapInfo.length);
    assertNotNull(hashMapInfo[0].getLocation());
  }
  
  @Test public void searchWithWorkspaceScopeAndAppMask() throws Exception {
    final IX10SearchScope scope = SearchScopeFactory.createWorkspaceScope(X10SearchScope.APPLICATION);
    
    final ITypeInfo[] kMeansTypeInfo = X10SearchEngine.getTypeInfo(scope, KMEANS_CLASS, new NullProgressMonitor());
    assertEquals(1, kMeansTypeInfo.length);
    assertLocation(kMeansTypeInfo[0].getLocation(), KMEANS_LOC, 22, 145);
    
    final ITypeInfo[] helloTypeInfo = X10SearchEngine.getTypeInfo(scope, MONTYPI_CLASS, new NullProgressMonitor());
    assertEquals(1, helloTypeInfo.length);
    assertLocation(helloTypeInfo[0].getLocation(), MONTYPI_LOC, 21, 41);
    
    final ITypeInfo[] hashMapInfo = X10SearchEngine.getTypeInfo(scope, HASHMAP_TYPE, new NullProgressMonitor());
    assertEquals(0, hashMapInfo.length);
  }
  
  @Test public void searchWithWorkspaceScopeAndRuntimeMask() throws Exception {
    final IX10SearchScope scope = SearchScopeFactory.createWorkspaceScope(X10SearchScope.RUNTIME);
    
    final ITypeInfo[] kMeansTypeInfo = X10SearchEngine.getTypeInfo(scope, KMEANS_CLASS, new NullProgressMonitor());
    assertEquals(0, kMeansTypeInfo.length);
    
    final ITypeInfo[] helloTypeInfo = X10SearchEngine.getTypeInfo(scope, MONTYPI_CLASS, new NullProgressMonitor());
    assertEquals(0, helloTypeInfo.length);
    
    final ITypeInfo[] hashMapInfo = X10SearchEngine.getTypeInfo(scope, HASHMAP_TYPE, new NullProgressMonitor());
    assertEquals(1, hashMapInfo.length);
    assertNotNull(hashMapInfo[0].getLocation());
  }
  
  // --- Fields
  
  private static final String PROJECT_TEST = "searchScopeTest";
  
  private static final String PROJECT_TEST2 = "searchScopeTest2";
  
  private static final String MONTYPI_CLASS = "MontyPi";
  
  private static final String MONTYPI_LOC = "src/MontyPi.x10";
  
  private static final String KMEANS_CLASS = "KMeans";
  
  private static final String KMEANS_LOC = "src/KMeans.x10";
  
  private static final String HASHMAP_TYPE = "x10.util.HashMap";

}
