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


import java.text.MessageFormat;

import org.eclipse.imp.utils.SelectionUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;

import x10dt.search.core.elements.ITypeInfo;


/**
 * Refocuses the type hierarchy on the currently selection type.
 */
public class FocusOnSelectionAction extends Action {

	private ITypeHierarchyViewPart fViewPart;

	public FocusOnSelectionAction(ITypeHierarchyViewPart part) {
		super(TypeHierarchyMessages.FocusOnSelectionAction_label);
		setDescription(TypeHierarchyMessages.FocusOnSelectionAction_description);
		setToolTipText(TypeHierarchyMessages.FocusOnSelectionAction_tooltip);
		fViewPart= part;

//		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.FOCUS_ON_SELECTION_ACTION);
	}

	private ISelection getSelection() {
		ISelectionProvider provider= fViewPart.getSite().getSelectionProvider();
		if (provider != null) {
			return provider.getSelection();
		}
		return null;
	}


	/*
	 * @see Action#run
	 */
	public void run() {
		Object element= SelectionUtil.getSingleElement(getSelection());
		if (element instanceof ITypeInfo) {
			fViewPart.setInputElement((ITypeInfo)element);
		}
	}

	public boolean canActionBeAdded() {
		Object element= SelectionUtil.getSingleElement(getSelection());
		if (element instanceof ITypeInfo) {
			ITypeInfo type= (ITypeInfo)element;
			setText(MessageFormat.format(
					TypeHierarchyMessages.FocusOnSelectionAction_label,
					type.getName()));
			return true;
		}
		return false;
	}
}
