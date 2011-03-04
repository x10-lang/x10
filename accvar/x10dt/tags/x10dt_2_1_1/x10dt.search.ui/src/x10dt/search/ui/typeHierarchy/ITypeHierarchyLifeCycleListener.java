package x10dt.search.ui.typeHierarchy;
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


import x10dt.search.core.elements.IMemberInfo;


/**
 * Used by the TypeHierarchyLifeCycle to inform listeners about a change in the
 * type hierarchy
 */
public interface ITypeHierarchyLifeCycleListener {

	/**
	 * A Java element changed.
	 * @param typeHierarchyProvider the type hierarchy that changed
	 * @param changedTypes the types that changed or <code>null</code> if the full hierarchy changed
	 */
	void typeHierarchyChanged(TypeHierarchyLifeCycle typeHierarchyProvider, IMemberInfo[] changedTypes);

}
