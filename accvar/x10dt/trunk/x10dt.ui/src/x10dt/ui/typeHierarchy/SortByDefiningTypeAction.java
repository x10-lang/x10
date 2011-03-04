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


import org.eclipse.swt.custom.BusyIndicator;

import org.eclipse.jface.action.Action;

import org.eclipse.ui.PlatformUI;


/**
 * Action to let the label provider show the defining type of the method
 */
public class SortByDefiningTypeAction extends Action {

	private MethodsViewer fMethodsViewer;

	/**
	 * Creates the action.
	 * @param viewer the viewer
	 * @param initValue the initial state
	 */
	public SortByDefiningTypeAction(MethodsViewer viewer, boolean initValue) {
		super(TypeHierarchyMessages.SortByDefiningTypeAction_label);
		setDescription(TypeHierarchyMessages.SortByDefiningTypeAction_description);
		setToolTipText(TypeHierarchyMessages.SortByDefiningTypeAction_tooltip);

		X10PluginImages.setLocalImageDescriptors(this, "definingtype_sort_co.gif"); //$NON-NLS-1$

		fMethodsViewer= viewer;

//		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.SORT_BY_DEFINING_TYPE_ACTION);

		setChecked(initValue);
	}

	/*
	 * @see Action#actionPerformed
	 */
	public void run() {
		BusyIndicator.showWhile(fMethodsViewer.getControl().getDisplay(), new Runnable() {
			public void run() {
				fMethodsViewer.sortByDefiningType(isChecked());
			}
		});
	}
}
