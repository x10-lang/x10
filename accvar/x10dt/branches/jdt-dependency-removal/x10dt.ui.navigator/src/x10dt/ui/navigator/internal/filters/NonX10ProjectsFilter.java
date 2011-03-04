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
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


/**
 * Filters non-java projects
 */
public class NonX10ProjectsFilter extends ViewerFilter {

	/*
	 * @see ViewerFilter
	 */
	public boolean select(Viewer viewer, Object parent, Object element) {
		if (element instanceof ISourceProject)
		{
			Language lang = LanguageRegistry.findLanguage("X10");
			return ((ISourceProject)element).getRawProject().getFile(lang.getPathFileName()).exists();
		}
		
		else if (element instanceof IProject)
		{
			return !((IProject)element).isOpen();
		}
		
		return true;
	}
}
