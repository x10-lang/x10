/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine;

import java.util.Arrays;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.pdb.facts.ISourceLocation;

import x10dt.search.core.elements.IMethodInfo;
import x10dt.search.core.elements.ITypeInfo;


final class MethodInfo extends AbstractMemberInfo implements IMethodInfo {
  
  MethodInfo(final String name, final ITypeInfo returnType, final ITypeInfo[] parameterTypes, 
             final ISourceLocation location, final int x10FlagsCode, final ITypeInfo declaringType) {
    super(location, name, x10FlagsCode, declaringType);
    this.fReturnType = returnType;
    this.fParameterTypes = parameterTypes;
  }
  
  // --- IX10Element's interface methods implementation
  
  public boolean exists(final IProgressMonitor monitor) {
    return true;
  }
    
  // --- IMethodInfo's interface methods implementation
  
  public ITypeInfo[] getParameters() {
    return this.fParameterTypes;
  }

  public ITypeInfo getReturnType() {
    return this.fReturnType;
  }
  
  public boolean isConstructor() {
    return "this".equals(getName()); //$NON-NLS-1$
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    if ((rhs == null) || ! (rhs instanceof IMethodInfo)) {
      return false;
    }
    final IMethodInfo rhsObj = (IMethodInfo) rhs;
    return super.equals(rhsObj) && this.fReturnType.equals(rhsObj.getReturnType()) &&
           Arrays.equals(this.fParameterTypes, rhsObj.getParameters());
  }
  
  public int hashCode() {
    return super.hashCode() + this.fReturnType.hashCode() + Arrays.hashCode(this.fParameterTypes);
  }
  
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(super.toString()).append("\nReturn type: ").append(this.fReturnType.getName()); //$NON-NLS-1$
    int i = 0;
    for (final ITypeInfo paramType : this.fParameterTypes) {
      sb.append("\nParam ").append(i).append(": ").append(paramType.getName()); //$NON-NLS-1$ //$NON-NLS-2$
    }
    return sb.toString();
  }
  
  // --- Fields
  
  private final ITypeInfo fReturnType;
  
  private final ITypeInfo[] fParameterTypes;

}
