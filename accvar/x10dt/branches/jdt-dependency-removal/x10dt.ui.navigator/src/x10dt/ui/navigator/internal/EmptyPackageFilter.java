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
package x10dt.ui.navigator.internal;

import org.eclipse.imp.model.ISourceFolder;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


/**
 * Filters out all empty package fragments.
 */
public class EmptyPackageFilter extends ViewerFilter {

	/*
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parent, Object element) {
		if (element instanceof ISourceFolder) {
			ISourceFolder pkg= (ISourceFolder)element;
			try {
				return pkg.getChildren().length > 0 || hasUnfilteredResources(viewer, pkg);
			} catch (ModelException e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tells whether the given package has unfiltered resources.
	 *
	 * @param viewer the viewer
	 * @param pkg the package
	 * @return <code>true</code> if the package has unfiltered resources
	 * @throws JavaModelException if this element does not exist or if an exception occurs while
	 *             accessing its corresponding resource
	 * @since 3.4.1
	 */
	private boolean hasUnfilteredResources(Viewer viewer, ISourceFolder pkg) throws ModelException {
//		Object[] resources= pkg.getNonJavaResources();
//		int length= resources.length;
//		if (length == 0)
			return false;

//		if (!(viewer instanceof StructuredViewer))
//			return true;
//
//		ViewerFilter[] filters= ((StructuredViewer)viewer).getFilters();
//		resourceLoop: for (int i= 0; i < length; i++) {
//			for (int j= 0; j < filters.length; j++) {
//				if (!filters[j].select(viewer, pkg, resources[i]))
//					continue resourceLoop;
//			}
//			return true;
//
//		}
//		return false;
	}


}
