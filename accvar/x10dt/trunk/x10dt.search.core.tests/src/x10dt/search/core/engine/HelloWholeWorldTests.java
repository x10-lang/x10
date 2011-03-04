/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import static x10dt.search.core.utils.TestUtils.assertLocation;
import static x10dt.search.core.utils.TestUtils.assertPath;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import x10dt.search.core.elements.IFieldInfo;
import x10dt.search.core.elements.IMethodInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.search.core.engine.scope.SearchScopeFactory;
import x10dt.search.core.engine.scope.X10SearchScope;

/**
 * Test cases for {@link X10SearchEngine} class around X10 HelloWholeWorld example.
 * 
 * @author egeay
 */
@SuppressWarnings("nls")
public final class HelloWholeWorldTests extends AbstractIndexerTestBase {
    
  // --- Test cases

  @Test public void testHelloWholeWorldTypeInfo() throws Exception {
    final IProject project = createProjectFromX10Dist(PROJECT_NAME, "samples/HelloWholeWorld.x10", EProjectBackEnd.CPP);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project);
    
    final ITypeInfo[] helloTypeInfo = X10SearchEngine.getTypeInfo(scope, HELLOWORLD_CLASS, new NullProgressMonitor());
    assertEquals(1, helloTypeInfo.length);
    assertLocation(helloTypeInfo[0].getLocation(), HELLOWORLD_LOC, 18, 24);
    assertPath(PROJECT_NAME, HELLOWORLD_LOC, helloTypeInfo[0].getCompilationUnit().getPath());
    assertNull(helloTypeInfo[0].getDeclaringType());

    final ITypeInfo[] hashMapInfo = X10SearchEngine.getTypeInfo(scope, "x10.util.HashMap", new NullProgressMonitor());
    assertEquals(1, hashMapInfo.length);
    assertNotNull(hashMapInfo[0].getLocation());
  }
  
  @Test public void testHelloWholeWorldMethodAndFields() throws Exception {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project);
    
    final ITypeInfo[] typeInfo = X10SearchEngine.getTypeInfo(scope, HELLOWORLD_CLASS, new NullProgressMonitor());
    assertEquals(1, typeInfo.length);
    
    final IMethodInfo[] mainMethods = X10SearchEngine.getMethodInfos(scope, typeInfo[0], "main", 
                                                                     new NullProgressMonitor());
    assertEquals(1, mainMethods.length);
    assertLocation(mainMethods[0].getLocation(), HELLOWORLD_LOC, 19, 23);
    assertEquals(1, mainMethods[0].getParameters().length);
    assertEquals(ARRAY_TYPE, mainMethods[0].getParameters()[0].getName());
    assertEquals(VOID_TYPE, mainMethods[0].getReturnType().getName());
    assertPath(PROJECT_NAME, HELLOWORLD_LOC, mainMethods[0].getCompilationUnit().getPath());
    assertEquals(HELLOWORLD_CLASS, mainMethods[0].getDeclaringType().getName());
    
    final IFieldInfo[] fields = X10SearchEngine.getAllMatchingFieldInfo(scope, typeInfo[0], ".*", false, 
                                                                        new NullProgressMonitor());
    assertEquals(0, fields.length);
  }
  
  @Test public void testHelloWholeWorldMethods1() throws Exception {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project);
    
    final ITypeInfo[] typeInfo = X10SearchEngine.getTypeInfo(scope, HELLOWORLD_CLASS, new NullProgressMonitor());
    assertEquals(1, typeInfo.length);
    
    final IMethodInfo[] methods = X10SearchEngine.getAllMatchingMethodInfo(scope, typeInfo[0], ".*", false, 
                                                                           new NullProgressMonitor());
    assertEquals(1, methods.length);
    
    assertLocation(methods[0].getLocation(), HELLOWORLD_LOC, 19, 23);
    assertEquals("main", methods[0].getName());
    assertEquals(1, methods[0].getParameters().length);
    assertEquals(ARRAY_TYPE, methods[0].getParameters()[0].getName());
    assertEquals(VOID_TYPE, methods[0].getReturnType().getName());
  }
  
  @Test public void testHelloWholeWorldMethods2() throws Exception {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project);
    
    final ITypeInfo[] typeInfo = X10SearchEngine.getTypeInfo(scope, HELLOWORLD_CLASS, new NullProgressMonitor());
    assertEquals(1, typeInfo.length);
    
    final IMethodInfo[] methods = X10SearchEngine.getAllMatchingMethodInfo(scope, typeInfo[0], "m.*", false, 
                                                                           new NullProgressMonitor());
    assertEquals(1, methods.length);
    assertLocation(methods[0].getLocation(), HELLOWORLD_LOC, 19, 23);
    assertEquals(1, methods[0].getParameters().length);
    assertEquals(ARRAY_TYPE, methods[0].getParameters()[0].getName());
    assertEquals(VOID_TYPE, methods[0].getReturnType().getName());
  }
  
  @Test public void testHelloWholeWorldMethods3() throws Exception {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project);
    
    final ITypeInfo[] typeInfo = X10SearchEngine.getTypeInfo(scope, HELLOWORLD_CLASS, new NullProgressMonitor());
    assertEquals(1, typeInfo.length);
    
    final IMethodInfo[] methods = X10SearchEngine.getAllMatchingMethodInfo(scope, typeInfo[0], "ma.*", false, 
                                                                           new NullProgressMonitor());
    assertEquals(1, methods.length);
    assertLocation(methods[0].getLocation(), HELLOWORLD_LOC, 19, 23);
    assertEquals(1, methods[0].getParameters().length);
    assertEquals(ARRAY_TYPE, methods[0].getParameters()[0].getName());
    assertEquals(VOID_TYPE, methods[0].getReturnType().getName());
  }
  
  @Test public void testHelloWholeWorldMethods4() throws Exception {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project);
    
    final ITypeInfo[] typeInfo = X10SearchEngine.getTypeInfo(scope, HELLOWORLD_CLASS, new NullProgressMonitor());
    assertEquals(1, typeInfo.length);
    
    final IMethodInfo[] methods = X10SearchEngine.getAllMatchingMethodInfo(scope, typeInfo[0], "mai.*", false, 
                                                                           new NullProgressMonitor());
    assertEquals(1, methods.length);
    assertLocation(methods[0].getLocation(), HELLOWORLD_LOC, 19, 23);
    assertEquals(1, methods[0].getParameters().length);
    assertEquals(ARRAY_TYPE, methods[0].getParameters()[0].getName());
    assertEquals(VOID_TYPE, methods[0].getReturnType().getName());
  }
  
  @Test public void testHelloWholeWorldMethods5() throws Exception {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project);
    
    final ITypeInfo[] typeInfo = X10SearchEngine.getTypeInfo(scope, HELLOWORLD_CLASS, new NullProgressMonitor());
    assertEquals(1, typeInfo.length);
    
    final IMethodInfo[] methods = X10SearchEngine.getAllMatchingMethodInfo(scope, typeInfo[0], "main.*", false, 
                                                                           new NullProgressMonitor());
    assertEquals(1, methods.length);
    assertLocation(methods[0].getLocation(), HELLOWORLD_LOC, 19, 23);
    assertEquals(1, methods[0].getParameters().length);
    assertEquals(ARRAY_TYPE, methods[0].getParameters()[0].getName());
    assertEquals(VOID_TYPE, methods[0].getReturnType().getName());
  }
  
  // --- Fields
  
  private static final String PROJECT_NAME = "test";
  
  private static final String HELLOWORLD_CLASS = "HelloWholeWorld";
  
  private static final String HELLOWORLD_LOC = "src/HelloWholeWorld.x10";
  
  private static final String ARRAY_TYPE = "x10.array.Array";
  
  private static final String VOID_TYPE = "void";

}
