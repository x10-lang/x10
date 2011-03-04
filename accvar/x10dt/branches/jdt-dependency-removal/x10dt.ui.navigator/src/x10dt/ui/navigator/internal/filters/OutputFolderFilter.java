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


import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.ISourceFolderEntry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import x10dt.ui.navigator.UINavigatorPlugin;


/**
 * Filters out all output folders.
 * <p>
 * Note: Folder which are direct children of a Java element
 * are already filtered by the Java Model.
 * </p>
 *
 * @since 3.0
 */
public class OutputFolderFilter extends ViewerFilter {

	/**
	 * Returns the result of this filter, when applied to the
	 * given element.
	 *
	 * @param viewer the viewer
	 * @param parent the parent
	 * @param element the element to test
	 * @return <code>true</code> if element should be included
	 * @since 3.0
	 */
	public boolean select(Viewer viewer, Object parent, Object element) {
		if (element instanceof IFolder) {
			IFolder folder= (IFolder)element;
			IProject proj= folder.getProject();
			try {
				Language lang = LanguageRegistry.findLanguage("X10");
				if (!proj.hasNature(UINavigatorPlugin.X10_CPP_PRJ_NATURE_ID) && !proj.hasNature(UINavigatorPlugin.X10_PRJ_JAVA_NATURE_ID))
					return true;

				ISourceProject jProject= ModelFactory.getProject(proj);
				if (jProject == null || !proj.exists())
					return true;

				// Check default output location
				IPath defaultOutputLocation = jProject.getOutputLocation(lang);
				IPath folderPath= folder.getFullPath();
				if (defaultOutputLocation != null && defaultOutputLocation.equals(folderPath))
					return false;
		
//				// Check output location for each class path entry
				List<IPathEntry> cpEntries= jProject.getBuildPath(lang);
				for (IPathEntry entry : cpEntries) {
					if(entry instanceof ISourceFolderEntry)
					{
						IPath outputLocation= ((ISourceFolderEntry)entry).getOutputLocation();
						if (outputLocation != null && outputLocation.equals(folderPath))
							return false;
					}	
				}
			} catch (Exception ex) {
				return true;
			}
		}
		return true;
	}
}
