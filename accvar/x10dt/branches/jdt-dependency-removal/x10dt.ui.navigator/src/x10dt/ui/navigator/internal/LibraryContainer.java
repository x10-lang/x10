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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.IPathEntry.PathEntryType;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ISourceRoot;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.ui.navigator.PackageFragmentRootContainer;
import org.eclipse.imp.utils.BuildPathUtils;
import org.eclipse.jface.resource.ImageDescriptor;

import x10dt.search.ui.typeHierarchy.X10PluginImages;

public class LibraryContainer extends PackageFragmentRootContainer {

	public LibraryContainer(ISourceProject project) {
		super(project);
	}

	public boolean equals(Object obj) {
		if (obj instanceof LibraryContainer) {
			LibraryContainer other = (LibraryContainer)obj;
			return getJavaProject().equals(other.getJavaProject());
		}
		return false;
	}

	public int hashCode() {
		return getJavaProject().hashCode();
	}

	public Object[] getChildren() {
		List<ISourceRoot> roots = getPackageFragmentRoots();
		return roots.toArray(new Object[roots.size()]);
	}


	public ImageDescriptor getImageDescriptor() {
		return X10PluginImages.DESC_OBJS_LIBRARY;
	}

	public String getLabel() {
		return PackagesMessages.LibraryContainer_name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer#getPackageFragmentRoots()
	 */
	public List<ISourceRoot> getPackageFragmentRoots() {
		List<ISourceRoot> list= new ArrayList<ISourceRoot>();
		try {
			List<ISourceRoot> roots= getJavaProject().getSourceRoots(LanguageRegistry.findLanguage("X10"));
			for (ISourceRoot root : roots) {
				PathEntryType classpathEntryKind= BuildPathUtils.getRawClasspathEntry(root).getEntryType();
				if (classpathEntryKind == PathEntryType.ARCHIVE || classpathEntryKind == PathEntryType.VARIABLE) {
					list.add(root);
				}
			}
		} catch (ModelException e) {
			// fall through
		}
		return list;
	}
}
