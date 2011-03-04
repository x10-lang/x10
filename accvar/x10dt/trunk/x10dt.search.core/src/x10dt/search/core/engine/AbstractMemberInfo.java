/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.search.core.engine;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.pdb.facts.ISourceLocation;

import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.core.elements.ITypeInfo;


abstract class AbstractMemberInfo extends AbstractX10Element implements IMemberInfo {
  
  AbstractMemberInfo(final ISourceLocation location, final String name, final int x10FlagsCode, 
                     final ITypeInfo declaringType) {
    super(location);
    this.fDeclaringType = declaringType;
    this.fName = name;
    this.fX10FlagsCode = x10FlagsCode;
    
    final IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(getLocation().getURI());
    if (files.length == 0) {
      this.fCompilationUnit = null;
    } else {
      int counter = 0;
      for (int i = 0; i < files.length; ++i) {
        try {
          files[i].refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
        } catch (CoreException except) {
          // Let's simply forget in such case.
        }
        if (files[i].exists()) {
          ++counter;
        } else {
          files[i] = null;
        }
      }
      if (counter == 1) {
        for (final IFile file : files) {
          if (file != null) {
            try {
              this.fCompilationUnit = ModelFactory.open(file, ModelFactory.open(file.getProject()));
            } catch (ModelException except) {
              // This should never occur since we already have tested the file existence.
            }
            break;
          }
        }
      } else {
        this.fCompilationUnit = null;
      }
    }
  }
  
  // --- IX10Element's interface methods implementation
  
  public final ISourceEntity getSourceEntity() {
    return this.fCompilationUnit;
  }
  
  // --- IMemberInfo's interface methods implementation
  
  public final ICompilationUnit getCompilationUnit() {
    return this.fCompilationUnit;
  }

  public final ITypeInfo getDeclaringType() {
    return this.fDeclaringType;
  }

  public final String getName() {
    return this.fName;
  }

  public final int getX10FlagsCode() {
    return this.fX10FlagsCode;
  }
  
  // --- Overridden methods
  
  public boolean equals(final Object rhs) {
    if ((rhs == null) || ! (rhs instanceof IMemberInfo)) {
      return false;
    }
    final IMemberInfo rhsObj = (IMemberInfo) rhs;
    return super.equals(rhsObj) && this.fName.equals(rhsObj.getName()) && (this.fX10FlagsCode == rhsObj.getX10FlagsCode()); 
  }
  
  public int hashCode() {
    return super.hashCode() + this.fName.hashCode() + this.fX10FlagsCode;
  }
  
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Name: ").append(this.fName) //$NON-NLS-1$
      .append("\nDeclaring Type: ").append((this.fDeclaringType == null) ? "null" : this.fDeclaringType.getName()) //$NON-NLS-1$ //$NON-NLS-2$
      .append("\nFlags code: ").append(this.fX10FlagsCode) //$NON-NLS-1$
      .append("\nLocation: ").append(getLocation()); //$NON-NLS-1$
    return sb.toString();
  }
  
  // --- Fields
  
  private final ITypeInfo fDeclaringType;
  
  private final String fName;
  
  private final int fX10FlagsCode;
  
  private ICompilationUnit fCompilationUnit;

}
