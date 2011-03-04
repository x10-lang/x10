/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.pdb.facts.ISourceLocation;

import x10dt.search.core.elements.ITypeInfo;


final class UnknownTypeInfo implements ITypeInfo {

  UnknownTypeInfo(final String typeName) {
    this.fTypeName = typeName;
  }
  
  // --- IX10Element's interface methods implementation
  
  public boolean exists(final IProgressMonitor monitor) {
    return true;
  }

  public ISourceLocation getLocation() {
    return null;
  }
  
  public final ISourceEntity getSourceEntity() {
    return null;
  }
  
  // --- IMemberInfo's interface methods implementation
  
  public ITypeInfo getDeclaringType() {
    return null;
  }
  
  public String getName() {
    return this.fTypeName;
  }
  
  public int getX10FlagsCode() {
    return 0;
  }
  
  // --- ITypeInfo's interface methods implementation
  
  public ICompilationUnit getCompilationUnit() {
    return null;
  }
  
  // --- Fields
  
  private final String fTypeName;

}
