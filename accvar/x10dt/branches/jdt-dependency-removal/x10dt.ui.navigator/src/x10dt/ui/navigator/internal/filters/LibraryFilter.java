/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package x10dt.ui.navigator.internal.filters;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.imp.model.ISourceRoot;
import org.eclipse.imp.ui.navigator.ClassPathContainer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;



/**
 * The LibraryFilter is a filter used to determine whether
 * a Java library is shown
 */
public class LibraryFilter extends ViewerFilter {

	/* (non-Javadoc)
	 * Method declared on ViewerFilter.
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof ISourceRoot) {
			ISourceRoot root= (ISourceRoot)element;
			if (root.isArchive()) {
				// don't filter out JARs contained in the project itself
				IResource resource= root.getResource();
				if (resource != null) {
					IProject jarProject= resource.getProject();
					IProject container= root.getProject().getRawProject();
					return container.equals(jarProject);
				}
				return false;
			}
		} else if (element instanceof ClassPathContainer.RequiredProjectWrapper) {
			return false;
		}
		return true;
	}
}
