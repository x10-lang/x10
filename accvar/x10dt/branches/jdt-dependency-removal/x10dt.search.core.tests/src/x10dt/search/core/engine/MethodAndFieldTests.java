/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static x10dt.search.core.utils.TestUtils.assertLocation;
import static x10dt.search.core.utils.TestUtils.assertPath;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import x10dt.search.core.elements.IFieldInfo;
import x10dt.search.core.elements.IMethodInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.search.core.engine.scope.SearchScopeFactory;
import x10dt.search.core.engine.scope.X10SearchScope;
import x10dt.search.core.pdb.X10FlagsEncoder;

/**
 * Test cases around {@link x10dt.search.core.elements.IMethodInfo} and {@link x10dt.search.core.elements.IFieldInfo}.
 * 
 * @author egeay
 */
@SuppressWarnings("nls")
public final class MethodAndFieldTests extends AbstractIndexerTestBase {
  
  // --- Test cases
  
  @Test public void testIntegrateTutorial() throws Exception {
    final IProject project = createProjectFromX10Dist(PROJECT_NAME, "samples/tutorial/IntegrateTutorial.x10", 
                                                      EProjectBackEnd.JAVA);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project);
    final IProgressMonitor nullMonitor = new NullProgressMonitor();
    
    final ITypeInfo[] typeInfo = X10SearchEngine.getTypeInfo(scope, INTEGRATE_CLASS, nullMonitor);
    assertEquals(1, typeInfo.length);
    assertLocation(typeInfo[0].getLocation(), INTEGRATE_LOC, 20, 55);
    assertPath(PROJECT_NAME, INTEGRATE_LOC, typeInfo[0].getCompilationUnit().getPath());
    assertNull(typeInfo[0].getDeclaringType());

    final IFieldInfo fieldInfo = X10SearchEngine.getFieldInfo(scope, INTEGRATE_CLASS, "epsilon", nullMonitor);
    assertNotNull(fieldInfo);
    assertLocation(fieldInfo.getLocation(), INTEGRATE_LOC, 21, 21);
    assertPath(PROJECT_NAME, INTEGRATE_LOC, fieldInfo.getCompilationUnit().getPath());
    assertEquals(INTEGRATE_CLASS, fieldInfo.getDeclaringType().getName());
    assertEquals(DOUBLE, fieldInfo.getFieldTypeInfo().getName());
    
    final IMethodInfo[] thisMthdInfo = X10SearchEngine.getMethodInfos(scope, INTEGRATE_CLASS, "this", nullMonitor);
    assertEquals(1, thisMthdInfo.length);
    assertLocation(thisMthdInfo[0].getLocation(), INTEGRATE_LOC, 25, 25);
    assertPath(PROJECT_NAME, INTEGRATE_LOC, thisMthdInfo[0].getCompilationUnit().getPath());
    assertEquals(INTEGRATE_CLASS, thisMthdInfo[0].getDeclaringType().getName());
    assertTrue(thisMthdInfo[0].isConstructor());
    assertEquals(VOID, thisMthdInfo[0].getReturnType().getName());
    
    final IMethodInfo[] recEvalInfo = X10SearchEngine.getMethodInfos(scope, INTEGRATE_CLASS, "recEval", nullMonitor);
    assertEquals(1, recEvalInfo.length);
    assertLocation(recEvalInfo[0].getLocation(), INTEGRATE_LOC, 31, 47);
    assertFalse(recEvalInfo[0].isConstructor());
    assertEquals(DOUBLE, recEvalInfo[0].getReturnType().getName());
    assertEquals(5, recEvalInfo[0].getParameters().length);
    assertEquals(DOUBLE, recEvalInfo[0].getParameters()[0].getName());
    assertEquals(DOUBLE, recEvalInfo[0].getParameters()[1].getName());
    assertEquals(DOUBLE, recEvalInfo[0].getParameters()[2].getName());
    assertEquals(DOUBLE, recEvalInfo[0].getParameters()[3].getName());
    assertEquals(DOUBLE, recEvalInfo[0].getParameters()[4].getName());
    assertTrue((recEvalInfo[0].getX10FlagsCode() & X10FlagsEncoder.X10.PRIVATE.getCode()) != 0);
    
    final IMethodInfo[] allMethods = X10SearchEngine.getAllMatchingMethodInfo(scope, INTEGRATE_CLASS, ".*", true, nullMonitor);
    assertEquals(4, allMethods.length);
  }
  
  // --- Fields
  
  private static final String PROJECT_NAME = "methodAndField";
  
  private static final String INTEGRATE_CLASS = "IntegrateTutorial";
  
  private static final String INTEGRATE_LOC = "src/IntegrateTutorial.x10";
  
  private static final String DOUBLE = "x10.lang.Double";
  
  private static final String VOID = "x10.lang.Void";

}
