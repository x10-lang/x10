/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.pdb.facts.ISourceLocation;

import x10dt.search.core.elements.IFieldInfo;
import x10dt.search.core.elements.ITypeInfo;


final class FieldInfo extends AbstractMemberInfo implements IFieldInfo {
  
  FieldInfo(final String fieldName, final ITypeInfo fieldType, final ISourceLocation location, final int x10FlagsCode,
            final ITypeInfo declaringType) {
    super(location, fieldName, x10FlagsCode, declaringType);
    this.fFieldType = fieldType;
  }
  
  // --- IX10Element's interface methods implementation
  
  public boolean exists(final IProgressMonitor monitor) {
    return true;
  }

  // --- IFieldInfo's interface methods implementation
  
  public ITypeInfo getFieldTypeInfo() {
    return this.fFieldType;
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    if ((rhs == null) || ! (rhs instanceof IFieldInfo)) {
      return false;
    }
    final IFieldInfo rhsObj = (IFieldInfo) rhs;
    return super.equals(rhsObj) && this.fFieldType.equals(rhsObj.getFieldTypeInfo());
  }
  
  public int hashCode() {
    return super.hashCode() + this.fFieldType.hashCode();
  }
  
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(super.toString()).append("\nField Type: ").append(this.fFieldType.getName()); //$NON-NLS-1$
    return sb.toString();
  }
  
  // --- Private code
  
  private final ITypeInfo fFieldType;
  
}
