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


import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import x10dt.search.core.elements.IFieldInfo;
import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.pdb.X10FlagsEncoder.X10;



/**
 * Filter for the methods viewer.
 * Changing a filter property does not trigger a refiltering of the viewer
 */
public class MemberFilter extends ViewerFilter {

	public static final int FILTER_NONPUBLIC= 1;
	public static final int FILTER_STATIC= 2;
	public static final int FILTER_FIELDS= 4;
	public static final int FILTER_LOCALTYPES= 8;

	private int fFilterProperties;


	/**
	 * Modifies filter and add a property to filter for
	 * @param filter the filter to add
	 */
	public final void addFilter(int filter) {
		fFilterProperties |= filter;
	}
	/**
	 * Modifies filter and remove a property to filter for
	 * @param filter the filter to remove
	 */
	public final void removeFilter(int filter) {
		fFilterProperties &= (-1 ^ filter);
	}
	/**
	 * Tests if a property is filtered
	 * @param filter the filter to test
	 * @return returns the result of the test
	 */
	public final boolean hasFilter(int filter) {
		return (fFilterProperties & filter) != 0;
	}

	/*
	 * @see ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		try {
			if (element instanceof IMemberInfo) {
				IMemberInfo member= (IMemberInfo) element;
				
				if (hasFilter(FILTER_FIELDS) && (member instanceof IFieldInfo)) {
					return false;
				}

				if (hasFilter(FILTER_LOCALTYPES) && isLocalType(member)) {
					return false;
				}

				if (member.getName().startsWith("<")) { // filter out <clinit> //$NON-NLS-1$
					return false;
				}
				int flags= member.getX10FlagsCode();
				if (hasFilter(FILTER_STATIC) && (SearchUtils.hasFlag(X10.STATIC, flags) || isFieldInInterface(member)) && !(member instanceof ITypeInfo)) {
					return false;
				}
				if (hasFilter(FILTER_NONPUBLIC) && !SearchUtils.hasFlag(X10.PUBLIC, flags) && !isMemberInInterface(member) && !isTopLevelType(member) /*&& !isEnumConstant(member)*/) {
					return false;
				}
			}
		} catch (Exception e) {
			// ignore
		}
		return true;
	}

	private boolean isLocalType(IMemberInfo member) {
		return member.getDeclaringType() != null;
	}

	private boolean isMemberInInterface(IMemberInfo member) throws Exception {
		ITypeInfo parent= member.getDeclaringType();
		return parent != null && ModelUtil.isInterface(parent);
	}

	private boolean isFieldInInterface(IMemberInfo member) throws Exception {
		return (member instanceof IFieldInfo) && ModelUtil.isInterface(member.getDeclaringType());
	}

	private boolean isTopLevelType(IMemberInfo member) {
		ITypeInfo parent= member.getDeclaringType();
		return parent == null;
	}

//	private boolean isEnumConstant(IMemberInfo member) throws Exception {
//		return (member instanceof IFieldInfo) && ((IFieldInfo)member).isEnumConstant();
//	}
}
