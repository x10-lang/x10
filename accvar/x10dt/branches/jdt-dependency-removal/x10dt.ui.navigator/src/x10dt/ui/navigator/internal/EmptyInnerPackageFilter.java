/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
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
import org.eclipse.imp.utils.BuildPathUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


/**
 * Filters empty non-leaf package fragments
 */
public class EmptyInnerPackageFilter extends ViewerFilter {

	/*
	 * @see ViewerFilter
	 */
	public boolean select(Viewer viewer, Object parent, Object element) {
		if (element instanceof ISourceFolder) {
			ISourceFolder pkg= (ISourceFolder)element;
			try {
				if (BuildPathUtils.isDefaultPackage(pkg.getName()))
					return pkg.getChildren().length > 0;
				return !BuildPathUtils.hasSubpackages(pkg) || pkg.getChildren().length > 0 /*|| (pkg.getNonJavaResources().length > 0)*/;
			} catch (ModelException e) {
				return false;
			}
		}

		return true;
	}
}
