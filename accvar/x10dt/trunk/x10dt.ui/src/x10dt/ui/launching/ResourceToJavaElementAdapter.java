/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation.                                         *
 * All rights reserved. This program and the accompanying materials            *
 * are made available under the terms of the Eclipse Public License v1.0       *
 * which accompanies this distribution, and is available at                    *
 * http://www.eclipse.org/legal/epl-v10.html                                   *
 *******************************************************************************/
package x10dt.ui.launching;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Adapts a given {@link IResource} into an {@link IJavaElement}.
 * 
 * <p>Should be used with care.
 * 
 * @author egeay
 */
public final class ResourceToJavaElementAdapter implements IJavaElement {
  
  /**
   * Creates the adapter from the given {@link IResource} instance.
   * 
   * @param resource The resource to adapt into an {@link IJavaElement}.
   */
  public ResourceToJavaElementAdapter(final IResource resource) {
    this.fResource = resource;
  }
  
  // --- Interface methods implementation

  @SuppressWarnings("rawtypes")
  public Object getAdapter(final Class adapter) {
    throw new UnsupportedOperationException();
  }

  public boolean exists() {
    throw new UnsupportedOperationException();
  }

  public IJavaElement getAncestor(final int ancestorType) {
    throw new UnsupportedOperationException();
  }

  public String getAttachedJavadoc(final IProgressMonitor monitor) throws JavaModelException {
    throw new UnsupportedOperationException();
  }

  public IResource getCorrespondingResource() throws JavaModelException {
    return this.fResource;
  }

  public String getElementName() {
    throw new UnsupportedOperationException();
  }

  public int getElementType() {
    throw new UnsupportedOperationException();
  }

  public String getHandleIdentifier() {
    throw new UnsupportedOperationException();
  }

  public IJavaModel getJavaModel() {
    throw new UnsupportedOperationException();
  }

  public IJavaProject getJavaProject() {
    return JavaCore.create(this.fResource.getProject());
  }

  public IOpenable getOpenable() {
    throw new UnsupportedOperationException();
  }

  public IJavaElement getParent() {
    throw new UnsupportedOperationException();
  }

  public IPath getPath() {
    throw new UnsupportedOperationException();
  }

  public IJavaElement getPrimaryElement() {
    throw new UnsupportedOperationException();
  }

  public IResource getResource() {
    return this.fResource;
  }

  public ISchedulingRule getSchedulingRule() {
    throw new UnsupportedOperationException();
  }

  public IResource getUnderlyingResource() throws JavaModelException {
    return this.fResource;
  }

  public boolean isReadOnly() {
    throw new UnsupportedOperationException();
  }

  public boolean isStructureKnown() throws JavaModelException {
    throw new UnsupportedOperationException();
  }
  
  // --- Fields
  
  private final IResource fResource;

}
