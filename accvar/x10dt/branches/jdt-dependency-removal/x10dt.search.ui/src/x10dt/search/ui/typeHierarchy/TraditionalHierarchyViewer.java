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


import java.util.List;

import org.eclipse.swt.widgets.Composite;

import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.ITypeHierarchy;
import x10dt.search.core.pdb.X10FlagsEncoder.X10;


/**
 * A TypeHierarchyViewer that looks like the type hierarchy view of VA/Java:
 * Starting form Object down to the element in focus, then all subclasses from
 * this element.
 * Used by the TypeHierarchyViewPart which has to provide a TypeHierarchyLifeCycle
 * on construction (shared type hierarchy)
 */
public class TraditionalHierarchyViewer extends TypeHierarchyViewer {

	public TraditionalHierarchyViewer(Composite parent, TypeHierarchyLifeCycle lifeCycle) {
		super(parent, new TraditionalHierarchyContentProvider(lifeCycle), lifeCycle);
	}

	/*
	 * @see TypeHierarchyViewer#getTitle
	 */
	public String getTitle() {
		if (isMethodFiltering()) {
			return TypeHierarchyMessages.TraditionalHierarchyViewer_filtered_title;
		} else {
			return TypeHierarchyMessages.TraditionalHierarchyViewer_title;
		}
	}

	/*
	 * @see TypeHierarchyViewer#updateContent
	 */
	public void updateContent(boolean expand) {
		getTree().setRedraw(false);
		refresh();

		if (expand) {
			TraditionalHierarchyContentProvider contentProvider= (TraditionalHierarchyContentProvider) getContentProvider();
			int expandLevel= contentProvider.getExpandLevel();
			if (isMethodFiltering()) {
				expandLevel++;
			}
			expandToLevel(expandLevel);
		}
		getTree().setRedraw(true);
	}

	/**
	 * Content provider for the 'traditional' type hierarchy.
	 */
	public static class TraditionalHierarchyContentProvider extends TypeHierarchyContentProvider {
		
		public TraditionalHierarchyContentProvider(TypeHierarchyLifeCycle provider) {
			super(provider);
		}

		public int getExpandLevel() {
			ITypeHierarchy hierarchy= getHierarchy();
			if (hierarchy != null) {
				ITypeInfo input= hierarchy.getType();
				if (input != null) {
					return getDepth(hierarchy, input) + 2;
				} else {
					return 5;
				}
			}
			return 2;
		}

		private int getDepth(ITypeHierarchy hierarchy, ITypeInfo input) {
			int count= 0;
			ITypeInfo superType= hierarchy.getSuperClass(input.getName());
			while (superType != null) {
				count++;
				superType= hierarchy.getSuperClass(superType.getName());
			}
			return count;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.ui.typehierarchy.TypeHierarchyContentProvider#getRootTypes(java.util.List)
		 */
		protected final void getRootTypes(List res) {
			ITypeHierarchy hierarchy= getHierarchy();
			if (hierarchy != null) {
				ITypeInfo input= hierarchy.getType();
//				if (input == null) {
//					String[] classes= hierarchy.getAllSuperTypes(input);
//					for (int i= 0; i < classes.length; i++) {
//						res.add(getType(classes[i]));
//					}
//				} else {
					if (SearchUtils.hasFlag(X10.INTERFACE, input.getX10FlagsCode())) {
						res.add(input);
					} else if (isAnonymousFromInterface(input)) {
						res.add(hierarchy.getInterfaces(input.getName())[0]);
					} else if (isObject(input)) {
					    res.add(input);
					} else {
						ITypeInfo[] roots= hierarchy.getAllSuperClasses(input.getName());
						for (int i= 0; i < roots.length; i++) {
							ITypeInfo type = roots[i];
							if (isObject(type)) {
								res.add(type);
								return;
							}
						}
						for (int i= 0; i < roots.length; i++) { // something wrong with the hierarchy
							res.add(roots[i]);
						}
					}
//				}
			}
		}
		
		/*
		 * @see TypeHierarchyContentProvider.getTypesInHierarchy
		 */
		protected final void getTypesInHierarchy(ITypeInfo type, List res) {
			ITypeHierarchy hierarchy= getHierarchy();
			if (hierarchy != null) {
				ITypeInfo[] types= hierarchy.getSubTypes(type.getName());
				if (isObject(type)) {
					for (int i= 0; i < types.length; i++) {
						ITypeInfo curr= types[i];
						if(!isAnonymousFromInterface(curr)) { // no anonymous classes on 'Object' -> will be children of interface
							res.add(curr);
						}
					}
				} else {
					boolean isHierarchyOnType= (type.equals(hierarchy.getType()));
					boolean isClass= !SearchUtils.hasFlag(X10.INTERFACE, type.getX10FlagsCode());
					if (isClass || isHierarchyOnType) {
						for (int i= 0; i < types.length; i++) {
							res.add(types[i]);
						}
					} else {
						for (int i= 0; i < types.length; i++) {
							ITypeInfo curr = types[i];
							// no classes implementing interfaces, only if anonymous
							if(SearchUtils.hasFlag(X10.INTERFACE, curr.getX10FlagsCode() /*|| isAnonymous(curr)*/))
							{
								res.add(curr);
							}
						}
					}
				}
			}
		}

		protected IMemberInfo getParentType(IMemberInfo type) {
			ITypeHierarchy hierarchy= getHierarchy();
			if (hierarchy != null) {
				return hierarchy.getSuperClass(type.getName());
				// don't handle interfaces
			}
			return null;
		}
	}
}
