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
package x10dt.ui.typeHierarchy.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.actions.SelectionDispatchAction;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;

import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.ui.X10DTUIPlugin;
import x10dt.ui.typeHierarchy.ActionMessages;
import x10dt.ui.typeHierarchy.OpenTypeHierarchyUtil;
import x10dt.ui.typeHierarchy.SelectionConverter;


/**
 * This action opens a type hierarchy on the selected type.
 * <p>
 * The action is applicable to selections containing elements of type
 * <code>IType</code>.
 *
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 *
 * @since 2.0
 *
 * @noextend This class is not intended to be subclassed by clients.
 */
public class OpenTypeHierarchyAction extends SelectionDispatchAction {

	private UniversalEditor fEditor;

	/**
	 * Creates a new <code>OpenTypeHierarchyAction</code>. The action requires
	 * that the selection provided by the site's selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 *
	 * @param site the site providing context information for this action
	 */
	public OpenTypeHierarchyAction(IWorkbenchSite site) {
		super(site);
		setText(ActionMessages.OpenTypeHierarchyAction_label);
		setToolTipText(ActionMessages.OpenTypeHierarchyAction_tooltip);
		setDescription(ActionMessages.OpenTypeHierarchyAction_description);
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.OPEN_TYPE_HIERARCHY_ACTION);
	}

	/**
	 * Creates a new <code>OpenTypeHierarchyAction</code>. The action requires
	 * that the selection provided by the given selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 *
	 * @param site the site providing context information for this action
	 * @param provider a special selection provider which is used instead
	 *  of the site's selection provider or <code>null</code> to use the site's
	 *  selection provider
	 *
	 * @since 3.2
	 * @deprecated Use {@link #setSpecialSelectionProvider(ISelectionProvider)} instead. This API will be
	 * removed after 3.2 M5.
     */
    public OpenTypeHierarchyAction(IWorkbenchSite site, ISelectionProvider provider) {
        this(site);
        setSpecialSelectionProvider(provider);
    }


	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * @param editor the Java editor
	 *
	 * @noreference This constructor is not intended to be referenced by clients.
	 */
	public OpenTypeHierarchyAction(UniversalEditor editor) {
		this(editor.getEditorSite());
		fEditor= editor;
		
		setEnabled(true);
//		setEnabled(SelectionConverter.canOperateOn(fEditor));
	}

	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(ITextSelection selection) {
	}

	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(isEnabled(selection));
	}

	private boolean isEnabled(IStructuredSelection selection) {
		if (selection.size() != 1)
			return false;
		Object input= selection.getFirstElement();

		return input instanceof IMemberInfo;

//		if (input instanceof LogicalPackage)
//			return true;
//
//		if (!(input instanceof ITypeInfo))
//			return false;
//		switch (((ITypeInfo)input).getElementType()) {
//			case ITypeInfo.INITIALIZER:
//			case ITypeInfo.METHOD:
//			case ITypeInfo.FIELD:
//			case ITypeInfo.TYPE:
//				return true;
//			case ITypeInfo.PACKAGE_FRAGMENT_ROOT:
//			case ITypeInfo.JAVA_PROJECT:
//			case ITypeInfo.PACKAGE_FRAGMENT:
//			case ITypeInfo.PACKAGE_DECLARATION:
//			case ITypeInfo.IMPORT_DECLARATION:
//			case ITypeInfo.CLASS_FILE:
//			case ITypeInfo.COMPILATION_UNIT:
//				return true;
//			case ITypeInfo.LOCAL_VARIABLE:
//			default:
//				return false;
//		}
	}

	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(ITextSelection selection) {
		IMemberInfo input= SelectionConverter.getInput(fEditor);
//		if (!ActionUtil.isProcessable(getShell(), input))
//			return;

		try {
			IMemberInfo[] elements= SelectionConverter.codeResolveOrInputForked(fEditor);
			if (elements == null)
				return;
			List candidates= new ArrayList(elements.length);
			for (int i= 0; i < elements.length; i++) {
				IMemberInfo[] resolvedElements= OpenTypeHierarchyUtil.getCandidates(elements[i]);
				if (resolvedElements != null)
					candidates.addAll(Arrays.asList(resolvedElements));
			}
			run((ITypeInfo[])candidates.toArray(new ITypeInfo[candidates.size()]));
		} catch (InvocationTargetException e) {
//			ExceptionHandler.handle(e, getShell(), getDialogTitle(), ActionMessages.SelectionConverter_codeResolve_failed);
			X10DTUIPlugin.log(e);
		} catch (InterruptedException e) {
			// cancelled
		}
	}

	/* (non-Javadoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(IStructuredSelection selection) {
		if (selection.size() != 1)
			return;
		Object input= selection.getFirstElement();

//		if (input instanceof LogicalPackage) {
//			IPackageFragment[] fragments= ((LogicalPackage)input).getFragments();
//			if (fragments.length == 0)
//				return;
//			input= fragments[0];
//		}

		if (!(input instanceof ITypeInfo)) {
			IStatus status= createStatus(ActionMessages.OpenTypeHierarchyAction_messages_no_java_element);
			ErrorDialog.openError(getShell(), getDialogTitle(), ActionMessages.OpenTypeHierarchyAction_messages_title, status);
			return;
		}
		ITypeInfo element= (ITypeInfo) input;
//		if (!ActionUtil.isProcessable(getShell(), element))
//			return;

		List result= new ArrayList(1);
		IStatus status= compileCandidates(result, element);
		if (status.isOK()) {
			run((ITypeInfo[]) result.toArray(new ITypeInfo[result.size()]));
		} else {
			ErrorDialog.openError(getShell(), getDialogTitle(), ActionMessages.OpenTypeHierarchyAction_messages_title, status);
		}
	}

	/*
	 * No Javadoc since the method isn't meant to be public but is
	 * since the beginning
	 */
	public void run(ITypeInfo[] elements) {
		if (elements.length == 0) {
			getShell().getDisplay().beep();
			return;
		}
		OpenTypeHierarchyUtil.open(elements, getSite().getWorkbenchWindow());
	}

	private static String getDialogTitle() {
		return ActionMessages.OpenTypeHierarchyAction_dialog_title;
	}

	private static IStatus compileCandidates(List result, ITypeInfo elem) {
		IStatus ok= new Status(IStatus.OK, X10DTUIPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
//		try {
//			switch (elem.getElementType()) {
//				case ITypeInfo.INITIALIZER:
//				case ITypeInfo.METHOD:
//				case ITypeInfo.FIELD:
//				case ITypeInfo.TYPE:
//				case ITypeInfo.PACKAGE_FRAGMENT_ROOT:
//				case ITypeInfo.JAVA_PROJECT:
//					result.add(elem);
//					return ok;
//				case ITypeInfo.PACKAGE_FRAGMENT:
//					if (((IPackageFragment)elem).containsJavaResources()) {
//						result.add(elem);
//						return ok;
//					}
//					return createStatus(ActionMessages.OpenTypeHierarchyAction_messages_no_java_resources);
//				case ITypeInfo.PACKAGE_DECLARATION:
//					result.add(elem.getAncestor(ITypeInfo.PACKAGE_FRAGMENT));
//					return ok;
//				case ITypeInfo.IMPORT_DECLARATION:
//					IImportDeclaration decl= (IImportDeclaration) elem;
//					if (decl.isOnDemand()) {
//						elem= JavaModelUtil.findTypeContainer(elem.getJavaProject(), Signature.getQualifier(elem.getElementName()));
//					} else {
//						elem= elem.getJavaProject().findType(elem.getElementName());
//					}
//					if (elem != null) {
//						result.add(elem);
//						return ok;
//					}
//					return createStatus(ActionMessages.OpenTypeHierarchyAction_messages_unknown_import_decl);
//				case ITypeInfo.CLASS_FILE:
//					result.add(((IClassFile)elem).getType());
//					return ok;
//				case ITypeInfo.COMPILATION_UNIT:
//					ICompilationUnit cu= (ICompilationUnit)elem;
//					IType[] types= cu.getTypes();
//					if (types.length > 0) {
//						result.addAll(Arrays.asList(types));
//						return ok;
//					}
//					return createStatus(ActionMessages.OpenTypeHierarchyAction_messages_no_types);
//			}
//		} catch (JavaModelException e) {
//			return e.getStatus();
//		}
		return createStatus(ActionMessages.OpenTypeHierarchyAction_messages_no_valid_java_element);
	}

	private static IStatus createStatus(String message) {
		return new Status(IStatus.INFO, X10DTUIPlugin.PLUGIN_ID, IStatus.ERROR, message, null);
	}
}
