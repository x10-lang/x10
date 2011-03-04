/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launching;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;


final class X10FileElementWrapper implements ISourceEntity {
  
  X10FileElementWrapper(final IFile x10File) {
    this.fX10File = x10File;
  }
  
  // --- Interface methods implementation

  @SuppressWarnings("unchecked")
  public Object getAdapter(final Class adapter) {
    throw new UnsupportedOperationException();
  }

  public boolean exists() {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unchecked")
  public ISourceEntity getAncestor(Class ancestorType) {
    throw new UnsupportedOperationException();
  }

  public String getAttachedJavadoc(final IProgressMonitor monitor) throws ModelException {
    throw new UnsupportedOperationException();
  }

  public IResource getCorrespondingResource() throws ModelException {
    return this.fX10File;
  }

  public String getName() {
    throw new UnsupportedOperationException();
  }

  public int getElementType() {
    throw new UnsupportedOperationException();
  }

  public String getHandleIdentifier() {
    throw new UnsupportedOperationException();
  }

//  public IJavaModel getJavaModel() {
//    throw new UnsupportedOperationException();
//  }

  public ISourceProject getProject() {
    return ModelFactory.getProject(this.fX10File.getProject());
  }

//  public IOpenable getOpenable() {
//    throw new UnsupportedOperationException();
//  }

  public ISourceEntity getParent() {
    throw new UnsupportedOperationException();
  }

  public IPath getPath() {
    throw new UnsupportedOperationException();
  }

  public ISourceEntity getPrimaryElement() {
    throw new UnsupportedOperationException();
  }

  public IResource getResource() {
    return this.fX10File;
  }

  public ISchedulingRule getSchedulingRule() {
    throw new UnsupportedOperationException();
  }

  public IResource getUnderlyingResource() throws ModelException {
    return this.fX10File;
  }

  public boolean isReadOnly() {
    throw new UnsupportedOperationException();
  }

  public boolean isStructureKnown() throws ModelException {
    throw new UnsupportedOperationException();
  }
  
  public void commit(IProgressMonitor monitor) {
	  throw new UnsupportedOperationException();
  }
  
  // --- Fields
  
  private final IFile fX10File;

}
