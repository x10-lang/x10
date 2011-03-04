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


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;

import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.ITypeHierarchy;
import x10dt.search.core.engine.X10SearchEngine;
import x10dt.search.core.engine.scope.IX10SearchScope;
import x10dt.search.core.engine.scope.SearchScopeFactory;
import x10dt.search.core.engine.scope.X10SearchScope;


/**
 * Manages a type hierarchy, to keep it refreshed, and to allow it to be shared.
 */
public class TypeHierarchyLifeCycle implements ITypeHierarchyChangedListener { //, IElementChangedListener {

	private boolean fHierarchyRefreshNeeded;
	private ITypeHierarchy fHierarchy;
	private IMemberInfo fInputElement;
	private boolean fIsSuperTypesOnly;
	
	private List fChangeListeners;

	public TypeHierarchyLifeCycle() {
		this(false);
	}

	public TypeHierarchyLifeCycle(boolean isSuperTypesOnly) {
		fHierarchy= null;
		fInputElement= null;
		fIsSuperTypesOnly= isSuperTypesOnly;
		fChangeListeners= new ArrayList(2);
	}

	public ITypeHierarchy getHierarchy() {
		return fHierarchy;
	}

	public IMemberInfo getInputElement() {
		return fInputElement;
	}


	public void freeHierarchy() {
		if (fHierarchy != null) {
//			fHierarchy.removeTypeHierarchyChangedListener(this);
			//JavaCore.removeElementChangedListener(this);
			fHierarchy= null;
			fInputElement= null;
		}
	}

	public void removeChangedListener(ITypeHierarchyLifeCycleListener listener) {
		fChangeListeners.remove(listener);
	}

	public void addChangedListener(ITypeHierarchyLifeCycleListener listener) {
		if (!fChangeListeners.contains(listener)) {
			fChangeListeners.add(listener);
		}
	}

	private void fireChange(ITypeInfo[] changedTypes) {
		for (int i= fChangeListeners.size()-1; i>=0; i--) {
			ITypeHierarchyLifeCycleListener curr= (ITypeHierarchyLifeCycleListener) fChangeListeners.get(i);
			curr.typeHierarchyChanged(this, changedTypes);
		}
	}

	public void ensureRefreshedTypeHierarchy(final IMemberInfo element, IRunnableContext context) throws InvocationTargetException, InterruptedException {
		if (element == null|| !element.exists(new NullProgressMonitor())) {
			freeHierarchy();
			return;
		}
		boolean hierachyCreationNeeded= (fHierarchy == null || !element.equals(fInputElement));

		if (hierachyCreationNeeded || fHierarchyRefreshNeeded) {

			IRunnableWithProgress op= new IRunnableWithProgress() {
				public void run(IProgressMonitor pm) throws InvocationTargetException, InterruptedException {
					try {
						doHierarchyRefresh(element, pm);
					} catch (OperationCanceledException e) {
						throw new InterruptedException();
					} catch (Exception e) {
						throw new InvocationTargetException(e);
					}
				}
			};
			fHierarchyRefreshNeeded= true;
			context.run(true, true, op);
			fHierarchyRefreshNeeded= false;
		}
	}

	private ITypeHierarchy createTypeHierarchy(IMemberInfo element, IProgressMonitor pm) throws Exception {
		if (element instanceof ITypeInfo) {
		  final IX10SearchScope scope;
      if (element.getCompilationUnit() == null) {
        scope = SearchScopeFactory.createWorkspaceScope(X10SearchScope.ALL);
      } else {
        scope = SearchScopeFactory.createSelectiveScope(X10SearchScope.ALL, element.getCompilationUnit().getResource());
      }
      final ITypeInfo typeInfo = (ITypeInfo) element;
			if (this.fIsSuperTypesOnly) {
				final ITypeHierarchy h = X10SearchEngine.createTypeHierarchy(SearchScopeFactory.createWorkspaceScope(X10SearchScope.ALL), typeInfo, pm);
				return X10SearchEngine.createTypeHierarchy(scope, h.getSuperClass(typeInfo), pm);
			} else {
				return X10SearchEngine.createTypeHierarchy(scope, typeInfo, pm);
			}
		}
		return null;
	}


	public synchronized void doHierarchyRefresh(IMemberInfo element, IProgressMonitor pm) throws Exception {
		boolean hierachyCreationNeeded= (fHierarchy == null || !element.equals(fInputElement));
		// to ensure the order of the two listeners always remove / add listeners on operations
		// on type hierarchies
		if (fHierarchy != null) {
//			fHierarchy.removeTypeHierarchyChangedListener(this);
			//JavaCore.removeElementChangedListener(this);
		}
		if (hierachyCreationNeeded) {
			fHierarchy= createTypeHierarchy(element, pm);
			if (pm != null && pm.isCanceled()) {
				throw new OperationCanceledException();
			}
			fInputElement= element;
		} else {
			fHierarchy = createTypeHierarchy(element, pm);
		}
//		fHierarchy.addTypeHierarchyChangedListener(this);
		//JavaCore.addElementChangedListener(this);
		fHierarchyRefreshNeeded= false;
	}

	/*
	 * @see ITypeHierarchyChangedListener#typeHierarchyChanged
	 */
	public void typeHierarchyChanged(ITypeHierarchy typeHierarchy) {
	 	fHierarchyRefreshNeeded= true;
 		fireChange(null);
	}

	/*
	 * @see IElementChangedListener#elementChanged(ElementChangedEvent)
	 */
//	public void elementChanged(ElementChangedEvent event) {
//		if (fChangeListeners.isEmpty()) {
//			return;
//		}
//
//		if (fHierarchyRefreshNeeded) {
//			return;
//		} else {
//			ArrayList changedTypes= new ArrayList();
//			processDelta(event.getDelta(), changedTypes);
//			if (changedTypes.size() > 0) {
//				fireChange((IType[]) changedTypes.toArray(new IType[changedTypes.size()]));
//			}
//		}
//	}

	/*
	 * Assume that the hierarchy is intact (no refresh needed)
	 */
//	private void processDelta(IJavaElementDelta delta, ArrayList changedTypes) {
//		ITypeInfo element= delta.getElement();
//		switch (element.getElementType()) {
//			case ITypeInfo.TYPE:
//				processTypeDelta((IType) element, changedTypes);
//				processChildrenDelta(delta, changedTypes); // (inner types)
//				break;
//			case ITypeInfo.JAVA_MODEL:
//			case ITypeInfo.JAVA_PROJECT:
//			case ITypeInfo.PACKAGE_FRAGMENT_ROOT:
//			case ITypeInfo.PACKAGE_FRAGMENT:
//				processChildrenDelta(delta, changedTypes);
//				break;
//			case ITypeInfo.COMPILATION_UNIT:
//				ICompilationUnit cu= (ICompilationUnit)element;
//				if (!JavaModelUtil.isPrimary(cu)) {
//					return;
//				}
//
//				if (delta.getKind() == IJavaElementDelta.CHANGED && isPossibleStructuralChange(delta.getFlags())) {
//					try {
//						if (cu.exists()) {
//							IType[] types= cu.getAllTypes();
//							for (int i= 0; i < types.length; i++) {
//								processTypeDelta(types[i], changedTypes);
//							}
//						}
//					} catch (Exception e) {
//						X10DTUIPlugin.log(e);
//					}
//				} else {
//					processChildrenDelta(delta, changedTypes);
//				}
//				break;
//			case ITypeInfo.CLASS_FILE:
//				if (delta.getKind() == IJavaElementDelta.CHANGED) {
//					IType type= ((IClassFile) element).getType();
//					processTypeDelta(type, changedTypes);
//				} else {
//					processChildrenDelta(delta, changedTypes);
//				}
//				break;
//		}
//	}
//
//	private boolean isPossibleStructuralChange(int flags) {
//		return (flags & (IJavaElementDelta.F_CONTENT | IJavaElementDelta.F_FINE_GRAINED)) == IJavaElementDelta.F_CONTENT;
//	}

	private void processTypeDelta(ITypeInfo type, ArrayList changedTypes) {
		if (getHierarchy().contains(type)) {
			changedTypes.add(type);
		}
	}

//	private void processChildrenDelta(IJavaElementDelta delta, ArrayList changedTypes) {
//		IJavaElementDelta[] children= delta.getAffectedChildren();
//		for (int i= 0; i < children.length; i++) {
//			processDelta(children[i], changedTypes); // recursive
//		}
//	}


}
