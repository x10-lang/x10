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


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.core.elements.IMethodInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.elements.IX10Element;
import x10dt.search.core.engine.ITypeHierarchy;


/**
 * Base class for content providers for type hierarchy viewers.
 * Implementors must override 'getTypesInHierarchy'.
 * Java delta processing is also performed by the content provider
 */
public abstract class TypeHierarchyContentProvider implements ITreeContentProvider { //, IWorkingCopyProvider {
	protected static final Object[] NO_ELEMENTS= new Object[0];

	protected TypeHierarchyLifeCycle fTypeHierarchy;
	protected IMemberInfo[] fMemberFilter;

	protected TreeViewer fViewer;

	private ViewerFilter fWorkingSetFilter;
	private MethodOverrideTester fMethodOverrideTester;
	private ITypeHierarchyLifeCycleListener fTypeHierarchyLifeCycleListener;
	
	TreeNode root;

	public TypeHierarchyContentProvider(TypeHierarchyLifeCycle lifecycle) {
		fTypeHierarchy= lifecycle;
		fMemberFilter= null;
		fWorkingSetFilter= null;
		fMethodOverrideTester= null;
		fTypeHierarchyLifeCycleListener= new ITypeHierarchyLifeCycleListener() {
			public void typeHierarchyChanged(TypeHierarchyLifeCycle typeHierarchyProvider, IMemberInfo[] changedTypes) {
				if (changedTypes == null) {
					fMethodOverrideTester= null;
				}
			}
		};
		lifecycle.addChangedListener(fTypeHierarchyLifeCycleListener);
	}

	/**
	 * Sets members to filter the hierarchy for. Set to <code>null</code> to disable member filtering.
	 * When member filtering is enabled, the hierarchy contains only types that contain
	 * an implementation of one of the filter members and the members themself.
	 * The hierarchy can be empty as well.
	 * @param memberFilter the new member filter
	 */
	public final void setMemberFilter(IMemberInfo[] memberFilter) {
		fMemberFilter= memberFilter;
	}

	private boolean initializeMethodOverrideTester(IMethodInfo filterMethod, ITypeInfo typeToFindIn) {
		ITypeInfo filterType= filterMethod.getDeclaringType();
		ITypeHierarchy hierarchy= fTypeHierarchy.getHierarchy();

		boolean filterOverrides= ModelUtil.isSuperType(hierarchy, typeToFindIn, filterType);
		ITypeInfo focusType= filterOverrides ? filterType : typeToFindIn;

		if (fMethodOverrideTester == null || !fMethodOverrideTester.getFocusType().equals(focusType)) {
			fMethodOverrideTester= new MethodOverrideTester(focusType, hierarchy);
		}
		return filterOverrides;
	}

	private void addCompatibleMethods(IMethodInfo filterMethod, ITypeInfo typeToFindIn, List children) throws Exception {
		boolean filterMethodOverrides= initializeMethodOverrideTester(filterMethod, typeToFindIn);
		IMethodInfo[] methods= SearchUtils.getMethods(typeToFindIn);
		for (int i= 0; i < methods.length; i++) {
			IMethodInfo curr= methods[i];
			if (isCompatibleMethod(filterMethod, curr, filterMethodOverrides) && !children.contains(curr)) {
				children.add(curr);
			}
		}
	}

	private boolean hasCompatibleMethod(IMethodInfo filterMethod, ITypeInfo typeToFindIn) throws Exception {
		boolean filterMethodOverrides= initializeMethodOverrideTester(filterMethod, typeToFindIn);
		IMethodInfo[] methods= SearchUtils.getMethods(typeToFindIn);
		for (int i= 0; i < methods.length; i++) {
			if (isCompatibleMethod(filterMethod, methods[i], filterMethodOverrides)) {
				return true;
			}
		}
		return false;
	}

	private boolean isCompatibleMethod(IMethodInfo filterMethod, IMethodInfo method, boolean filterOverrides) throws Exception {
		if (filterOverrides) {
			return fMethodOverrideTester.isSubsignature(filterMethod, method);
		} else {
			return fMethodOverrideTester.isSubsignature(method, filterMethod);
		}
	}

	/**
	 * The members to filter or <code>null</code> if member filtering is disabled.
	 * @return the member filter
	 */
	public IMemberInfo[] getMemberFilter() {
		return fMemberFilter;
	}

	/**
	 * Sets a filter representing a working set or <code>null</code> if working sets are disabled.
	 * @param filter the filter
	 */
	public void setWorkingSetFilter(ViewerFilter filter) {
		fWorkingSetFilter= filter;
	}

	

	protected final ITypeHierarchy getHierarchy() {
		return fTypeHierarchy.getHierarchy();
	}


	/* (non-Javadoc)
	 * @see IReconciled#providesWorkingCopies()
	 */
	public boolean providesWorkingCopies() {
		return true;
	}


	/*
	 * Called for the root element
	 * @see IStructuredContentProvider#getElements
	 */
	public Object[] getElements(Object parent) {
		ArrayList types= new ArrayList();
		getRootTypes(types);
		for (int i= types.size() - 1; i >= 0; i--) {
			ITypeInfo curr= (ITypeInfo) types.get(i);
			try {
				if (!isInTree(curr)) {
					types.remove(i);
				}
			} catch (Exception e) {
				// ignore
			}
		}
		return types.toArray();
	}

	protected void getRootTypes(List res) {
		ITypeHierarchy hierarchy= getHierarchy();
		if (hierarchy != null) {
			ITypeInfo input= hierarchy.getType();
			if (input != null) {
				res.add(input);
			}
			// opened on a region: dont show
		}
	}

	/**
	 * Hook to overwrite. Filter will be applied on the returned types
	 * @param type the type
	 * @param res all types in the hierarchy of the given type
	 */
	protected abstract void getTypesInHierarchy(ITypeInfo type, List res);

	/**
	 * Hook to overwrite. Return null if parent is ambiguous.
	 * @param type the type
	 * @return the parent type
	 */
	protected abstract IMemberInfo getParentType(IMemberInfo type);


	private boolean isInScope(ITypeInfo type) {
		if (fWorkingSetFilter != null && !fWorkingSetFilter.select(null, null, type)) {
			return false;
		}
		
		IX10Element input= fTypeHierarchy.getInputElement();
		if (input instanceof ITypeInfo) {
			return true;
		}

//		IX10Element parent= type.getAncestor(input.getElementType());
//		if (inputType == IX10Element.PACKAGE_FRAGMENT) {
//			if (parent == null || parent.getName().equals(input.getName())) {
//				return true;
//			}
//		} else 
//		if (input.equals(parent)) {
//			return true;
//		}
		return false;
	}

	/*
	 * Called for the tree children.
	 * @see ITreeContentProvider#getChildren
	 */
	public Object[] getChildren(Object element) {
		if (element instanceof ITypeInfo) {
			try {
				ITypeInfo type= (ITypeInfo)element;

				List children= new ArrayList();
				if (fMemberFilter != null) {
					addFilteredMemberChildren(type, children);
				}

				addTypeChildren(type, children);

				return children.toArray();
			} catch (Exception e) {
				// ignore
			}
		}
		return NO_ELEMENTS;
	}

	/*
	 * @see ITreeContentProvider#hasChildren
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof ITypeInfo) {
			try {
				ITypeInfo type= (ITypeInfo) element;
				return hasTypeChildren(type);// || (fMemberFilter != null && hasMemberFilterChildren(type));
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	private void addFilteredMemberChildren(ITypeInfo parent, List children) throws Exception {
		for (int i= 0; i < fMemberFilter.length; i++) {
			IMemberInfo member= fMemberFilter[i];
			if (parent.equals(member.getDeclaringType())) {
				if (!children.contains(member)) {
					children.add(member);
				}
			} else if (member instanceof IMethodInfo) {
				addCompatibleMethods((IMethodInfo) member, parent, children);
			}
		}
	}

	private void addTypeChildren(ITypeInfo type, List children) throws Exception {
		ArrayList types= new ArrayList();
		getTypesInHierarchy(type, types);
		int len= types.size();
		for (int i= 0; i < len; i++) {
			ITypeInfo curr= (ITypeInfo) types.get(i);
			if (isInTree(curr)) {
				children.add(curr);
			}
		}
	}

	protected final boolean isInTree(ITypeInfo type) throws Exception {
		if (isInScope(type)) {
			if (fMemberFilter != null) {
				return hasMemberFilterChildren(type) || hasTypeChildren(type);
			} else {
				return true;
			}
		}
		return hasTypeChildren(type);
	}

	private boolean hasMemberFilterChildren(ITypeInfo type) throws Exception {
		for (int i= 0; i < fMemberFilter.length; i++) {
			IMemberInfo member= fMemberFilter[i];
			if (type.equals(member.getDeclaringType())) {
				return true;
			} else if (member instanceof IMethodInfo) {
				if (hasCompatibleMethod((IMethodInfo) member, type)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasTypeChildren(ITypeInfo type) throws Exception {
		ArrayList types= new ArrayList();
		getTypesInHierarchy(type, types);
		int len= types.size();
		for (int i= 0; i < len; i++) {
			ITypeInfo curr= (ITypeInfo) types.get(i);
			if (isInTree(curr)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * @see IContentProvider#inputChanged
	 */
	public void inputChanged(Viewer part, Object oldInput, Object newInput) {
		Assert.isTrue(part instanceof TreeViewer);
		fViewer= (TreeViewer)part;
	}

	/*
	 * @see IContentProvider#dispose
	 */
	public void dispose() {
		fTypeHierarchy.removeChangedListener(fTypeHierarchyLifeCycleListener);

	}

	/*
	 * @see ITreeContentProvider#getParent
	 */
	public Object getParent(Object element) {
		if (element instanceof IMemberInfo) {
			IMemberInfo member= (IMemberInfo) element;
			if (member instanceof ITypeInfo) {
				return getParentType((ITypeInfo)member);
			}
			return member.getDeclaringType();
		}
		return null;
	}

	protected final boolean isAnonymous(ITypeInfo type) {
		return type.getName().length() == 0;
	}

	protected final boolean isAnonymousFromInterface(ITypeInfo type) {
		return isAnonymous(type) && fTypeHierarchy.getHierarchy().getInterfaces(type.getName()).length != 0;
	}

	protected final boolean isObject(ITypeInfo type) {
		return "x10.lang.Object".equals(type.getName());  //$NON-NLS-1$//$NON-NLS-2$
	}
}
