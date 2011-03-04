package x10dt.ui.typeHierarchy;
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


import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.ITypeHierarchy;


/**
  */
public class HierarchyViewerSorter extends AbstractHierarchyViewerSorter {

	private final TypeHierarchyLifeCycle fHierarchy;
	private boolean fSortByDefiningType;

	public HierarchyViewerSorter(TypeHierarchyLifeCycle cycle) {
		fHierarchy= cycle;
	}

	public void setSortByDefiningType(boolean sortByDefiningType) {
		fSortByDefiningType= sortByDefiningType;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.typehierarchy.AbstractHierarchyViewerSorter#getTypeKind(org.eclipse.jdt.core.IType)
	 */
	protected int getTypeFlags(ITypeInfo type) {
		return type.getX10FlagsCode();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.typehierarchy.AbstractHierarchyViewerSorter#isSortByDefiningType()
	 */
	public boolean isSortByDefiningType() {
		return fSortByDefiningType;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.typehierarchy.AbstractHierarchyViewerSorter#isSortAlphabetically()
	 */
	public boolean isSortAlphabetically() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.typehierarchy.AbstractHierarchyViewerSorter#getHierarchy(org.eclipse.jdt.core.IType)
	 */
	protected ITypeHierarchy getHierarchy(ITypeInfo type) {
		return fHierarchy.getHierarchy(); // hierarchy contains all types shown
	}

}
