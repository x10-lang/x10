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
import org.eclipse.imp.model.ISourceEntity;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;

/**
 * Adapts a given {@link IResource} into an {@link ISourceEntity}.
 * 
 * <p>
 * Should be used with care.
 * 
 * @author egeay
 */
public final class ResourceToJavaElementAdapter implements ISourceEntity {

	/**
	 * Creates the adapter from the given {@link IResource} instance.
	 * 
	 * @param resource
	 *            The resource to adapt into an {@link ISourceEntity}.
	 */
	public ResourceToJavaElementAdapter(final IResource resource) {
		this.fResource = resource;
	}

	// --- Interface methods implementation
	public String getName() {
		throw new UnsupportedOperationException();
	}

	public ISourceEntity getParent() {
		throw new UnsupportedOperationException();
	}

	public ISourceEntity getAncestor(Class ofType) {
		throw new UnsupportedOperationException();
	}

	public IResource getResource() {
		return fResource;
	}

	public ISourceProject getProject() {
		return ModelFactory.getProject(this.fResource.getProject());
	}

	public IPath getPath() {
		throw new UnsupportedOperationException();
	}

	public void commit(IProgressMonitor monitor) {
		throw new UnsupportedOperationException();
	}

	// --- Fields

	private final IResource fResource;

}
