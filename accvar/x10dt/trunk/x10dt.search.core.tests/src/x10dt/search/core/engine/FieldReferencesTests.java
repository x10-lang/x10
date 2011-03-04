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

import org.eclipse.core.resources.IProject;
import org.junit.Test;

import x10dt.search.core.elements.IFieldInfo;
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
public final class FieldReferencesTests extends AbstractIndexerTestBase {
    
  // --- Test cases

  @Test public void refsForA_f() throws Exception {
    final IProject project = createProject(PROJECT_NAME, "data/references/FieldReferencesExample1.x10", EProjectBackEnd.JAVA);
    final IX10SearchScope scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, project);
    
    final ITypeInfo[] typeInfo = X10SearchEngine.getTypeInfo(scope, A_CLASS, null);
    assertEquals(1, typeInfo.length);
    
    final IFieldInfo fieldInfo = X10SearchEngine.getFieldInfo(scope, typeInfo[0], F_FIELD, null);
    assertNotNull(fieldInfo);
    
    final SearchResults sr1 = new SearchResults();
    X10SearchEngine.collectFieldUses(scope, fieldInfo, sr1, null);
    assertEquals(3, sr1.getNumberOfFindings());
    
    final SearchResults sr2 = new SearchResults();
    final IX10SearchScope runtimeScope = SearchScopeFactory.createSelectiveScope(X10SearchScope.RUNTIME, project);
    X10SearchEngine.collectFieldUses(runtimeScope, fieldInfo, sr2, null);
    assertEquals(0, sr2.getNumberOfFindings());
  }
  
  // --- Fields
  
  private static final String PROJECT_NAME = "fieldReferences";
  
  private static final String A_CLASS = "FieldReferencesExample1.A";
  
  private static final String F_FIELD = "f";

}
