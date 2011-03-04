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
import org.eclipse.jface.viewers.ViewerComparator;

import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.core.elements.ITypeInfo;

/**
 *  Viewer sorter which sorts the Java elements like
 *  they appear in the source.
 *
 * @since 3.2
 */
public class SourcePositionComparator extends ViewerComparator {

	/*
	 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
		return 0;
//		if (!(e1 instanceof ISourceReference))
//			return 0;
//		if (!(e2 instanceof ISourceReference))
//			return 0;

//		IJavaElement parent1= ((IJavaElement)e1).getParent();
//		if (parent1 == null || !parent1.equals(((IJavaElement)e2).getParent())) {
//				ITypeInfo t1= getOutermostDeclaringType(e1);
//				if (t1 == null)
//					return 0;
//
//				ITypeInfo t2= getOutermostDeclaringType(e2);
//				try {
//					if (!t1.equals(t2)) {
//						if (t2 == null)
//							return 0;
//
//						if (SearchUtils.hasFlag(X10.PUBLIC, t1.getX10FlagsCode()) && SearchUtils.hasFlag(X10.PUBLIC, t2.getX10FlagsCode()))
//							return 0;
//
//						if (!t1.getPackageFragment().equals(t2.getPackageFragment()))
//							return 0;
//
//						ICompilationUnit cu1= (ICompilationUnit)((IJavaElement)e1).getAncestor(IJavaElement.COMPILATION_UNIT);
//						if (cu1 != null) {
//							if (!cu1.equals(((IJavaElement)e2).getAncestor(IJavaElement.COMPILATION_UNIT)))
//								return 0;
//						} else {
//							IClassFile cf1= (IClassFile)((IJavaElement)e1).getAncestor(IJavaElement.CLASS_FILE);
//							if (cf1 == null)
//								return 0;
//							IClassFile cf2= (IClassFile)((IJavaElement)e2).getAncestor(IJavaElement.CLASS_FILE);
//							String source1= cf1.getSource();
//							if (source1 != null && !source1.equals(cf2.getSource()))
//								return 0;
//						}
//					}
//				} catch (ModelException e3) {
//					return 0;
//				}
//		}
//
//		try {
//			ISourceRange sr1= ((ISourceReference)e1).getSourceRange();
//			ISourceRange sr2= ((ISourceReference)e2).getSourceRange();
//			if (sr1 == null || sr2 == null)
//				return 0;
//
//			return sr1.getOffset() - sr2.getOffset();
//
//		} catch (ModelException e) {
//			return 0;
//		}
	}

	private ITypeInfo getOutermostDeclaringType(Object element) {
		if (!(element instanceof IMemberInfo))
			return null;

		ITypeInfo declaringType;
		if (element instanceof ITypeInfo)
			declaringType= (ITypeInfo)element;
		else {
			declaringType= ((IMemberInfo)element).getDeclaringType();
			if (declaringType == null)
				return null;
		}

		ITypeInfo declaringTypeDeclaringType= declaringType.getDeclaringType();
		while (declaringTypeDeclaringType != null) {
			declaringType= declaringTypeDeclaringType;
			declaringTypeDeclaringType= declaringType.getDeclaringType();
		}
		return declaringType;
	}
}
