/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import x10dt.search.core.elements.IMethodInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.search.core.engine.scope.SearchScopeFactory;
import x10dt.search.core.engine.scope.X10SearchScope;

/**
 * Test cases for search engine references methods.
 * 
 * @author egeay
 */
@SuppressWarnings("nls")
public final class MethodReferencesTests extends AbstractIndexerTestBase {
    
  // --- Test cases

  @Test public void refsForA_m() throws Exception {
    final IProject project = createProject(PROJECT_NAME, "data/references/MethodReferencesExample1.x10", EProjectBackEnd.JAVA);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project);
    
    final IProgressMonitor nullMonitor = new NullProgressMonitor();
    
    final ITypeInfo[] typeInfo = X10SearchEngine.getTypeInfo(scope, A_CLASS, nullMonitor);
    assertEquals(1, typeInfo.length);
    
    final IMethodInfo[] methodInfo = X10SearchEngine.getMethodInfos(scope, typeInfo[0], M_METHOD, nullMonitor);
    assertEquals(1, methodInfo.length);
    
    final SearchResults sr1 = new SearchResults();
    X10SearchEngine.collectMethodUses(scope, methodInfo[0], sr1, nullMonitor);
    assertEquals(1, sr1.getNumberOfFindings());
    
    final SearchResults sr2 = new SearchResults();
    final IX10SearchScope runtimeScope = SearchScopeFactory.createSelectiveScope(X10SearchScope.RUNTIME, project);
    X10SearchEngine.collectMethodUses(runtimeScope, methodInfo[0], sr2, nullMonitor);
    assertEquals(0, sr2.getNumberOfFindings());
  }
  
  @Test public void refsForMath_max() throws Exception {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project);
    
    final IProgressMonitor nullMonitor = new NullProgressMonitor();
    
    final ITypeInfo[] typeInfo = X10SearchEngine.getTypeInfo(scope, Math_CLASS, nullMonitor);
    assertEquals(1, typeInfo.length);
    
    final IMethodInfo[] methodInfo = X10SearchEngine.getMethodInfos(scope, typeInfo[0], Max_METHOD, nullMonitor);
    assertEquals(6, methodInfo.length);
    
    final SearchResults sr1 = new SearchResults();
    final IX10SearchScope applicationScope = SearchScopeFactory.createSelectiveScope(X10SearchScope.APPLICATION, project);
    X10SearchEngine.collectMethodUses(applicationScope, methodInfo[0], sr1, nullMonitor);
    assertEquals(2, sr1.getNumberOfFindings());
    
    final SearchResults sr2 = new SearchResults();
    final IX10SearchScope runtimeScope = SearchScopeFactory.createSelectiveScope(X10SearchScope.RUNTIME, project);
    X10SearchEngine.collectMethodUses(runtimeScope, methodInfo[0], sr2, nullMonitor);
    assertEquals(1, sr2.getNumberOfFindings());
  }
  
  @Test public void refsForB_m() throws Exception {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project);
    
    final IProgressMonitor nullMonitor = new NullProgressMonitor();
    
    final ITypeInfo[] typeInfo = X10SearchEngine.getTypeInfo(scope, B_CLASS, nullMonitor);
    assertEquals(1, typeInfo.length);
    
    final IMethodInfo[] methodInfo = X10SearchEngine.getMethodInfos(scope, typeInfo[0], M_METHOD, nullMonitor);
    assertEquals(1, methodInfo.length);
    
    final SearchResults sr1 = new SearchResults();
    X10SearchEngine.collectMethodUses(scope, methodInfo[0], sr1, nullMonitor);
    assertEquals(1, sr1.getNumberOfFindings());
    
    final SearchResults sr2 = new SearchResults();
    final IX10SearchScope runtimeScope = SearchScopeFactory.createSelectiveScope(X10SearchScope.RUNTIME, project);
    X10SearchEngine.collectMethodUses(runtimeScope, methodInfo[0], sr2, nullMonitor);
    assertEquals(0, sr2.getNumberOfFindings());
  }
  
  @Test public void refsForB_this() throws Exception {
    final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project);
    
    final IProgressMonitor nullMonitor = new NullProgressMonitor();
    
    final ITypeInfo[] typeInfo = X10SearchEngine.getTypeInfo(scope, B_CLASS, nullMonitor);
    assertEquals(1, typeInfo.length);
    
    final IMethodInfo[] methodInfo = X10SearchEngine.getMethodInfos(scope, typeInfo[0], THIS_CTOR, nullMonitor);
    assertEquals(1, methodInfo.length);
    
    final SearchResults sr1 = new SearchResults();
    X10SearchEngine.collectMethodUses(scope, methodInfo[0], sr1, nullMonitor);
    assertEquals(1, sr1.getNumberOfFindings());
    
    final SearchResults sr2 = new SearchResults();
    final IX10SearchScope runtimeScope = SearchScopeFactory.createSelectiveScope(X10SearchScope.RUNTIME, project);
    X10SearchEngine.collectMethodUses(runtimeScope, methodInfo[0], sr2, nullMonitor);
    assertEquals(0, sr2.getNumberOfFindings());
  }
  
  // --- Fields
  
  private static final String PROJECT_NAME = "methodReferences";
  
  private static final String A_CLASS = "MethodReferencesExample1.A";
  
  private static final String B_CLASS = "MethodReferencesExample1.B";
  
  private static final String M_METHOD = "m";
  
  private static final String THIS_CTOR = "this";
  
  private static final String Math_CLASS = "x10.lang.Math";
  
  private static final String Max_METHOD = "max";

}
