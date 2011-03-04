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
package x10dt.search.ui.typeHierarchy;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;

import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.ui.Messages;
import x10dt.search.ui.UISearchPlugin;
import x10dt.search.ui.actions.EditorActionDefinitionIds;


public class OpenTypeHierarchyUtil {

	private OpenTypeHierarchyUtil() {
	}

	public static TypeHierarchyViewPart open(ITypeInfo element, IWorkbenchWindow window) {
		IMemberInfo[] candidates= getCandidates(element);
		if (candidates != null) {
			return open(candidates, window);
		}
		return null;
	}

	public static TypeHierarchyViewPart open(IMemberInfo[] candidates, IWorkbenchWindow window) {
		Assert.isTrue(candidates != null && candidates.length != 0);

		IMemberInfo input= null;
		if (candidates.length > 1) {
			String title= Messages.OpenTypeHierarchyUtil_selectionDialog_title;
			String message= Messages.OpenTypeHierarchyUtil_selectionDialog_message;
			input= SelectionConverter.selectJavaElement(candidates, window.getShell(), title, message);
		} else {
			input= candidates[0];
		}
		if (input == null)
			return null;

		try {
			if (X10Constants.OPEN_TYPE_HIERARCHY_IN_PERSPECTIVE.equals(UISearchPlugin.getDefault().getPreferenceStore().getString(EditorActionDefinitionIds.OPEN_TYPE_HIERARCHY))) {
				return openInPerspective(window, input);
			} else {
				return openInViewPart(window, input);
			}

		} catch (Exception e) {
//			ExceptionHandler.handle(e, window.getShell(),
//				Messages.OpenTypeHierarchyUtil_error_open_perspective,
//				e.getMessage());
			UISearchPlugin.log(e);
		} 
//			catch (JavaModelException e) {
//			ExceptionHandler.handle(e, window.getShell(),
//				Messages.OpenTypeHierarchyUtil_error_open_editor,
//				e.getMessage());
//		}
		return null;
	}

	private static TypeHierarchyViewPart openInViewPart(IWorkbenchWindow window, IMemberInfo input) {
		IWorkbenchPage page= window.getActivePage();
		try {
			TypeHierarchyViewPart result= (TypeHierarchyViewPart) page.findView(X10Constants.ID_TYPE_HIERARCHY);
			if (result != null) {
				result.clearNeededRefresh(); // avoid refresh of old hierarchy on 'becomes visible'
			}
			result= (TypeHierarchyViewPart) page.showView(X10Constants.ID_TYPE_HIERARCHY);
			result.setInputElement(input);
			return result;
		} catch (CoreException e) {
//			ExceptionHandler.handle(e, window.getShell(),
//				Messages.OpenTypeHierarchyUtil_error_open_view, e.getMessage());
			UISearchPlugin.log(e);
		}
		return null;
	}

	private static TypeHierarchyViewPart openInPerspective(IWorkbenchWindow window, IMemberInfo input) throws WorkbenchException, Exception {
		IWorkbench workbench= UISearchPlugin.getDefault().getWorkbench();
		// The problem is that the input element can be a working copy. So we first convert it to the original element if
		// it exists.
		IMemberInfo perspectiveInput= input;

		if (input instanceof IMemberInfo) {
			if (!(input instanceof ITypeInfo)) {
				perspectiveInput= ((IMemberInfo)input).getDeclaringType();
			} else {
				perspectiveInput= input;
			}
		}
		;
		IWorkbenchPage page= workbench.showPerspective(X10Constants.ID_HIERARCHYPERSPECTIVE, window, SearchUtils.getResource(perspectiveInput));

		TypeHierarchyViewPart part= (TypeHierarchyViewPart) page.findView(X10Constants.ID_TYPE_HIERARCHY);
		if (part != null) {
			part.clearNeededRefresh(); // avoid refresh of old hierarchy on 'becomes visible'
		}
		part= (TypeHierarchyViewPart) page.showView(X10Constants.ID_TYPE_HIERARCHY);
		part.setInputElement(input);
		if (input instanceof IMemberInfo) {
			if (page.getEditorReferences().length == 0) {
				SearchUtils.openEditor(input);
			}
		}
		return part;
	}


	/**
	 * Converts the input to a possible input candidates
	 * @param input input
	 * @return the possible candidates
	 */
	public static IMemberInfo[] getCandidates(Object input) {
		if (!(input instanceof IMemberInfo)) {
			return null;
		}
		
		return new IMemberInfo[] { (IMemberInfo)input };
		
		
//		try {
//			ITypeInfo elem= (ITypeInfo) input;
//			switch (elem.getElementType()) {
//				case ITypeInfo.INITIALIZER:
//				case ITypeInfo.METHOD:
//				case ITypeInfo.FIELD:
//				case ITypeInfo.TYPE:
//				case ITypeInfo.PACKAGE_FRAGMENT_ROOT:
//				case ITypeInfo.JAVA_PROJECT:
//					return new ITypeInfo[] { elem };
//				case ITypeInfo.PACKAGE_FRAGMENT:
//					if (((IPackageFragment)elem).containsJavaResources())
//						return new ITypeInfo[] {elem};
//					break;
//				case ITypeInfo.PACKAGE_DECLARATION:
//					return new ITypeInfo[] { elem.getAncestor(ITypeInfo.PACKAGE_FRAGMENT) };
//				case ITypeInfo.IMPORT_DECLARATION:
//					IImportDeclaration decl= (IImportDeclaration) elem;
//					if (decl.isOnDemand()) {
//						elem= JavaModelUtil.findTypeContainer(elem.getJavaProject(), Signature.getQualifier(elem.getElementName()));
//					} else {
//						elem= elem.getJavaProject().findType(elem.getElementName());
//					}
//					if (elem == null)
//						return null;
//					return new ITypeInfo[] {elem};
//
//				case ITypeInfo.CLASS_FILE:
//					return new ITypeInfo[] { ((IClassFile)input).getType() };
//				case ITypeInfo.COMPILATION_UNIT: {
//					ICompilationUnit cu= (ICompilationUnit) elem.getAncestor(ITypeInfo.COMPILATION_UNIT);
//					if (cu != null) {
//						IType[] types= cu.getTypes();
//						if (types.length > 0) {
//							return types;
//						}
//					}
//					break;
//				}
//				default:
//			}
//		} catch (JavaModelException e) {
//			UISearchPlugin.log(e);
//		}
//		return null;
	}
}
