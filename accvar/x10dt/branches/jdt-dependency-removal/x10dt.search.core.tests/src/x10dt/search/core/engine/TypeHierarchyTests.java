/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static x10dt.search.core.utils.TestUtils.assertHasElement;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.search.core.engine.scope.SearchScopeFactory;
import x10dt.search.core.engine.scope.X10SearchScope;
import x10dt.search.core.utils.GetNameFunc;

/**
 * Tests cases for the creation of type hierarchy via {@link X10SearchEngine}.
 * 
 * @author egeay
 */
@SuppressWarnings("nls")
public final class TypeHierarchyTests extends AbstractIndexerTestBase {
  
  // --- Test cases
  
  @Test public void helloWholeWorldWithAllMask() throws Exception {
    final IProject project = createProjectFromX10Dist(PROJECT_TEST, "samples/Histogram.x10", EProjectBackEnd.JAVA);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project);
    
    final IProgressMonitor monitor = new NullProgressMonitor();
    final ITypeHierarchy typeHierarchy = X10SearchEngine.createTypeHierarchy(scope, HISTOGRAM_CLASS, monitor);
    
    final ITypeInfo[] allInterfaces = typeHierarchy.getAllInterfaces();
    assertEquals(1, allInterfaces.length);
    assertEquals(ANY_INTERFACE, allInterfaces[0].getName());
    
    final ITypeInfo[] allClasses = typeHierarchy.getAllClasses();
    assertEquals(2, allClasses.length);
    assertHasElement(allClasses, OBJECT_CLASS, new GetNameFunc());
    assertHasElement(allClasses, HISTOGRAM_CLASS, new GetNameFunc());
    
    final ITypeInfo[] allSubTypes = typeHierarchy.getAllSubTypes(HISTOGRAM_CLASS);
    assertEquals(0, allSubTypes.length);
    
    final ITypeInfo[] allSuperTypes = typeHierarchy.getAllSuperTypes(HISTOGRAM_CLASS);
    assertEquals(2, allSuperTypes.length);
    assertHasElement(allSuperTypes, OBJECT_CLASS, new GetNameFunc());
    assertHasElement(allSuperTypes, ANY_INTERFACE, new GetNameFunc());
    
    final ITypeInfo[] superTypes = typeHierarchy.getSuperTypes(HISTOGRAM_CLASS);
    assertEquals(1, superTypes.length);
    assertHasElement(superTypes, OBJECT_CLASS, new GetNameFunc());
    
    final ITypeInfo superClass = typeHierarchy.getSuperClass(HISTOGRAM_CLASS);
    assertEquals(OBJECT_CLASS, superClass.getName());
    
    final ITypeInfo[] interfaces = typeHierarchy.getInterfaces(HISTOGRAM_CLASS);
    assertEquals(0, interfaces.length);
    
    final ITypeInfo[] allSuperInterfaces = typeHierarchy.getAllSuperInterfaces(HISTOGRAM_CLASS);
    assertEquals(1, allSuperInterfaces.length);
    assertHasElement(allSuperInterfaces, ANY_INTERFACE, new GetNameFunc());
  }
  
  @Test public void helloWholeWorldWithAppMask() throws Exception {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_TEST);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.APPLICATION, project);
    
    final IProgressMonitor monitor = new NullProgressMonitor();
    final ITypeHierarchy typeHierarchy = X10SearchEngine.createTypeHierarchy(scope, HISTOGRAM_CLASS, monitor);
    
    final ITypeInfo[] allInterfaces = typeHierarchy.getAllInterfaces();
    assertEquals(0, allInterfaces.length);
    
    final ITypeInfo[] allClasses = typeHierarchy.getAllClasses();
    assertEquals(1, allClasses.length);
    assertHasElement(allClasses, HISTOGRAM_CLASS, new GetNameFunc());
    
    final ITypeInfo[] allSubTypes = typeHierarchy.getAllSubTypes(HISTOGRAM_CLASS);
    assertEquals(0, allSubTypes.length);
    
    final ITypeInfo[] allSuperTypes = typeHierarchy.getAllSuperTypes(HISTOGRAM_CLASS);
    assertEquals(0, allSuperTypes.length);
    
    final ITypeInfo superClass = typeHierarchy.getSuperClass(HISTOGRAM_CLASS);
    assertNull(superClass);
    
    final ITypeInfo[] allSuperInterfaces = typeHierarchy.getAllSuperTypes(HISTOGRAM_CLASS);
    assertEquals(0, allSuperInterfaces.length);
  }
  
  // --- Fields
  
  private static final String PROJECT_TEST = "typeHierarchy";
  
  private static final String HISTOGRAM_CLASS = "Histogram";
  
  private static final String OBJECT_CLASS = "x10.lang.Object";
  
  private static final String ANY_INTERFACE = "x10.lang.Any";

}
