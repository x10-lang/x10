/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.pdb.facts.IInteger;
import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.IString;
import org.eclipse.imp.pdb.facts.ITuple;

import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.search.core.engine.scope.SearchScopeFactory;
import x10dt.search.core.engine.scope.X10SearchScope;


final class TypeInfo extends AbstractMemberInfo implements ITypeInfo {
  
  TypeInfo(final ITuple tuple, final ITypeInfo declaringType) {
    this(((IString) tuple.get(0)).getValue(), (ISourceLocation) tuple.get(1), ((IInteger) tuple.get(2)).intValue(), 
         declaringType);
  }
  
  TypeInfo(final String typeName, final ISourceLocation location, final int x10FlagsCode, final ITypeInfo declaringType) {
    super(location, typeName, x10FlagsCode, declaringType);
  }

  // --- ITypeInfo's interface methods implementation
  
  public boolean exists(final IProgressMonitor monitor) {
    final IX10SearchScope scope;
    if (getCompilationUnit() == null) {
      scope = SearchScopeFactory.createWorkspaceScope(X10SearchScope.ALL);
    } else {
      final IFile file = getCompilationUnit().getFile();
      scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, file);
    }
    try {
      return X10SearchEngine.getTypeInfo(scope, getName(), monitor).length > 0;
    } catch (Exception except) {
      return false;
    }
  }

}
