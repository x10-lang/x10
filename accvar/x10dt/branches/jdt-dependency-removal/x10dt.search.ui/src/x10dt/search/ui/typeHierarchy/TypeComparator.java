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


import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.ui.model.IWorkbenchAdapter;

import x10dt.search.core.elements.IFieldInfo;
import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.core.elements.IMethodInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.ui.UISearchPlugin;
import x10dt.search.ui.typeHierarchy.SearchUtils.Flags;



/**
 * Viewer comparator for Java elements. Ordered by element category, then by element name.
 * Package fragment roots are sorted as ordered on the classpath.
 *
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 *
 * @since 3.3
 *
 * @noextend This class is not intended to be subclassed by clients.
 */
public class TypeComparator extends ViewerComparator {

	private static final int PROJECTS= 1;
	private static final int PACKAGEFRAGMENTROOTS= 2;
	private static final int PACKAGEFRAGMENT= 3;

	private static final int COMPILATIONUNITS= 4;
	private static final int CLASSFILES= 5;

	private static final int RESOURCEFOLDERS= 7;
	private static final int RESOURCES= 8;

	private static final int PACKAGE_DECL=	10;
	private static final int IMPORT_CONTAINER= 11;
	private static final int IMPORT_DECLARATION= 12;

	// Includes all categories ordered using the OutlineSortOrderPage:
	// types, initializers, methods & fields
	private static final int MEMBERSOFFSET= 15;

	private static final int JAVAELEMENTS= 50;
	private static final int OTHERS= 51;

	private MembersOrderPreferenceCache fMemberOrderCache;

	/**
	 * Constructor.
	 */
	public TypeComparator() {
		super(null); // delay initialization of collator
		fMemberOrderCache= UISearchPlugin.getDefault().getMemberOrderPreferenceCache();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#category(java.lang.Object)
	 */
	public int category(Object element) {
		if (element instanceof IMemberInfo) {
				if(element instanceof IMethodInfo)
				{
					IMethodInfo method= (IMethodInfo) element;
					if (method.isConstructor()) {
						return getMemberCategory(MembersOrderPreferenceCache.CONSTRUCTORS_INDEX);
					}
					int flags= method.getX10FlagsCode();
					if (Flags.isStatic(flags))
						return getMemberCategory(MembersOrderPreferenceCache.STATIC_METHODS_INDEX);
					else
						return getMemberCategory(MembersOrderPreferenceCache.METHOD_INDEX);
				}
				
				if(element instanceof IFieldInfo)
				{
					int flags= ((IFieldInfo) element).getX10FlagsCode();
//					if (Flags.isEnum(flags)) {
//						return getMemberCategory(MembersOrderPreferenceCache.ENUM_CONSTANTS_INDEX);
//					}
					if (Flags.isStatic(flags))
						return getMemberCategory(MembersOrderPreferenceCache.STATIC_FIELDS_INDEX);
					else
						return getMemberCategory(MembersOrderPreferenceCache.FIELDS_INDEX);
				}
				
				if(element instanceof ITypeInfo)
				{
					return getMemberCategory(MembersOrderPreferenceCache.TYPE_INDEX);
				}
				
//				switch (je.getElementType()) {
//					case IJavaElement.METHOD:
//						{
//							
//						}
//					case IJavaElement.FIELD :
//						{
//							
//						}
//					case IJavaElement.INITIALIZER :
//						{
//							int flags= ((IInitializer) je).getFlags();
//							if (Flags.isStatic(flags))
//								return getMemberCategory(MembersOrderPreferenceCache.STATIC_INIT_INDEX);
//							else
//								return getMemberCategory(MembersOrderPreferenceCache.INIT_INDEX);
//						}
//					case IJavaElement.TYPE :
//						
//					case IJavaElement.PACKAGE_DECLARATION :
//						return PACKAGE_DECL;
//					case IJavaElement.IMPORT_CONTAINER :
//						return IMPORT_CONTAINER;
//					case IJavaElement.IMPORT_DECLARATION :
//						return IMPORT_DECLARATION;
//					case IJavaElement.PACKAGE_FRAGMENT :
//						return PACKAGEFRAGMENT;
//					case IJavaElement.PACKAGE_FRAGMENT_ROOT :
//						return PACKAGEFRAGMENTROOTS;
//					case IJavaElement.JAVA_PROJECT :
//						return PROJECTS;
//					case IJavaElement.CLASS_FILE :
//						return CLASSFILES;
//					case IJavaElement.COMPILATION_UNIT :
//						return COMPILATIONUNITS;
//				}

			
			return JAVAELEMENTS;
		} else if (element instanceof IFile) {
			return RESOURCES;
		} else if (element instanceof IProject) {
			return PROJECTS;
		} else if (element instanceof IContainer) {
			return RESOURCEFOLDERS;
		} 
//		else if (element instanceof IJarEntryResource) {
//			if (((IJarEntryResource) element).isFile()) {
//				return RESOURCES;
//			}
//			return RESOURCEFOLDERS;
//		} else if (element instanceof PackageFragmentRootContainer) {
//			return PACKAGEFRAGMENTROOTS;
//		}
		return OTHERS;
	}

	private int getMemberCategory(int kind) {
		int offset= fMemberOrderCache.getCategoryIndex(kind);
		return offset + MEMBERSOFFSET;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
		int cat1= category(e1);
		int cat2= category(e2);

//		if (needsClasspathComparision(e1, cat1, e2, cat2)) {
//			IPackageFragmentRoot root1= getPackageFragmentRoot(e1);
//			IPackageFragmentRoot root2= getPackageFragmentRoot(e2);
//			if (root1 == null) {
//				if (root2 == null) {
//					return 0;
//				} else {
//					return 1;
//				}
//			} else if (root2 == null) {
//				return -1;
//			}
			// check if not same to avoid expensive class path access
//			if (!root1.getPath().equals(root2.getPath())) {
//				int p1= getClassPathIndex(root1);
//				int p2= getClassPathIndex(root2);
//				if (p1 != p2) {
//					return p1 - p2;
//				}
//			}
//		}

		if (cat1 != cat2)
			return cat1 - cat2;

		if (cat1 == PROJECTS || cat1 == RESOURCES || cat1 == RESOURCEFOLDERS || cat1 == OTHERS) {
			String name1= getNonJavaElementLabel(viewer, e1);
			String name2= getNonJavaElementLabel(viewer, e2);
			if (name1 != null && name2 != null) {
				return getComparator().compare(name1, name2);
			}
			return 0; // can't compare
		}
		// only java elements from this point

		if (e1 instanceof IMemberInfo) {
			if (fMemberOrderCache.isSortByVisibility()) {
				int flags1= ((IMemberInfo) e1).getX10FlagsCode();
				int flags2= ((IMemberInfo) e2).getX10FlagsCode();
				int vis= fMemberOrderCache.getVisibilityIndex(flags1) - fMemberOrderCache.getVisibilityIndex(flags2);
				if (vis != 0) {
					return vis;
				}
			}
		}

		String name1= getElementName(e1);
		String name2= getElementName(e2);

//		if (e1 instanceof ITypeInfo) { // handle anonymous types
//			if (name1.length() == 0) {
//				if (name2.length() == 0) {
//					try {
//						ITypeHierarchy h1 = X10SearchEngine.createTypeHierarchy(null, ((ITypeInfo) e1).getName(), new NullProgressMonitor());
//						ITypeHierarchy h2 = X10SearchEngine.createTypeHierarchy(null, ((ITypeInfo) e2).getName(), new NullProgressMonitor());
//						return getComparator().compare(h1.getSuperClass(e1), h2.getSuperclass(e2));
//					} catch (Exception e) {
//						return 0;
//					}
//				} else {
//					return 1;
//				}
//			} else if (name2.length() == 0) {
//				return -1;
//			}
//		}

		int cmp= getComparator().compare(name1, name2);
		if (cmp != 0) {
			return cmp;
		}

		if (e1 instanceof IMethodInfo) {
			ITypeInfo[] params1= ((IMethodInfo) e1).getParameters();
			ITypeInfo[] params2= ((IMethodInfo) e2).getParameters();
			int len= Math.min(params1.length, params2.length);
			for (int i = 0; i < len; i++) {
				cmp= getComparator().compare(params1[i].getName(), params2[i].getName());
				if (cmp != 0) {
					return cmp;
				}
			}
			return params1.length - params2.length;
		}
		return 0;
	}

//	private IPackageFragmentRoot getPackageFragmentRoot(Object element) {
//		if (element instanceof PackageFragmentRootContainer) {
//			// return first package fragment root from the container
//			PackageFragmentRootContainer cp= (PackageFragmentRootContainer)element;
//			Object[] roots= cp.getPackageFragmentRoots();
//			if (roots.length > 0)
//				return (IPackageFragmentRoot)roots[0];
//			// non resolvable - return null
//			return null;
//		}
//		return JavaModelUtil.getPackageFragmentRoot((IJavaElement)element);
//	}

	private String getNonJavaElementLabel(Viewer viewer, Object element) {
		// try to use the workbench adapter for non - java resources or if not available, use the viewers label provider
		if (element instanceof IResource) {
			return ((IResource) element).getName();
		}
		if (element instanceof IStorage) {
			return ((IStorage) element).getName();
		}
		if (element instanceof IAdaptable) {
			IWorkbenchAdapter adapter= (IWorkbenchAdapter) ((IAdaptable) element).getAdapter(IWorkbenchAdapter.class);
			if (adapter != null) {
				return adapter.getLabel(element);
			}
		}
		if (viewer instanceof ContentViewer) {
			IBaseLabelProvider prov = ((ContentViewer) viewer).getLabelProvider();
			if (prov instanceof ILabelProvider) {
				return ((ILabelProvider) prov).getText(element);
			}
		}
		return null;
	}

//	private int getClassPathIndex(IPackageFragmentRoot root) {
//		try {
//			IPath rootPath= root.getPath();
//			IPackageFragmentRoot[] roots= root.getJavaProject().getPackageFragmentRoots();
//			for (int i= 0; i < roots.length; i++) {
//				if (roots[i].getPath().equals(rootPath)) {
//					return i;
//				}
//			}
//		} catch (ModelException e) {
//		}
//
//		return Integer.MAX_VALUE;
//	}

	private boolean needsClasspathComparision(Object e1, int cat1, Object e2, int cat2) {
//		if ((cat1 == PACKAGEFRAGMENTROOTS && cat2 == PACKAGEFRAGMENTROOTS) ||
//			(cat1 == PACKAGEFRAGMENT &&
//				((IPackageFragment)e1).getParent().getResource() instanceof IProject &&
//				cat2 == PACKAGEFRAGMENTROOTS) ||
//			(cat1 == PACKAGEFRAGMENTROOTS &&
//				cat2 == PACKAGEFRAGMENT &&
//				((IPackageFragment)e2).getParent().getResource() instanceof IProject)) {
//			IJavaProject p1= getJavaProject(e1);
//			return p1 != null && p1.equals(getJavaProject(e2));
//		}
		return false;
	}
//
//	private IJavaProject getJavaProject(Object element) {
//		if (element instanceof IJavaElement) {
//			return ((IJavaElement)element).getJavaProject();
//		} else if (element instanceof PackageFragmentRootContainer) {
//			return ((PackageFragmentRootContainer)element).getJavaProject();
//		}
//		return null;
//	}

	private String getElementName(Object element) {
		if (element instanceof IMemberInfo) {
			return ((IMemberInfo)element).getName();
		} 
//		else if (element instanceof PackageFragmentRootContainer) {
//			return ((PackageFragmentRootContainer)element).getLabel();
//		} else {
			return element.toString();
//		}
	}
}
