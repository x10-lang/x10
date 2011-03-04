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


import java.text.MessageFormat;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import x10dt.search.core.elements.IMemberInfo;


/**
 * Action used for the type hierarchy forward / backward buttons
 */
public class HistoryAction extends Action {

	private TypeHierarchyViewPart fViewPart;
	private IMemberInfo fElement;

	public HistoryAction(TypeHierarchyViewPart viewPart, IMemberInfo element) {
        super("", AS_RADIO_BUTTON); //$NON-NLS-1$
		fViewPart= viewPart;
		fElement= element;

		String elementName= element.getName();
		setText(elementName);
		setImageDescriptor(getImageDescriptor(element));

		setDescription(MessageFormat.format(TypeHierarchyMessages.HistoryAction_description, elementName));
		setToolTipText(MessageFormat.format(TypeHierarchyMessages.HistoryAction_tooltip, elementName));
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.HISTORY_ACTION);
	}

	private ImageDescriptor getImageDescriptor(IMemberInfo elem) {
//		JavaElementImageProvider imageProvider= new JavaElementImageProvider();
//		X10LabelProvider imageProvider= new X10LabelProvider();
//		ImageDescriptor desc= imageProvider.getImage(elem);
//		imageProvider.dispose();
//		return desc;
		return null;
	}

	/*
	 * @see Action#run()
	 */
	public void run() {
		fViewPart.gotoHistoryEntry(fElement);
	}

}
