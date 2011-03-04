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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.search.internal.ui.text.BasicElementLabels;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * Add the <code>StatusBarUpdater</code> to your ViewPart to have the statusbar
 * describing the selected elements.
 */
public class StatusBarUpdater implements ISelectionChangedListener {

//	private final long LABEL_FLAGS= JavaElementLabels.DEFAULT_QUALIFIED | JavaElementLabels.ROOT_POST_QUALIFIED | JavaElementLabels.APPEND_ROOT_PATH |
//			JavaElementLabels.M_PARAMETER_TYPES | JavaElementLabels.M_PARAMETER_NAMES | JavaElementLabels.M_APP_RETURNTYPE | JavaElementLabels.M_EXCEPTIONS |
//		 	JavaElementLabels.F_APP_TYPE_SIGNATURE | JavaElementLabels.T_TYPE_PARAMETERS;

	private IStatusLineManager fStatusLineManager;

	public StatusBarUpdater(IStatusLineManager statusLineManager) {
		fStatusLineManager= statusLineManager;
	}

	/*
	 * @see ISelectionChangedListener#selectionChanged
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		String statusBarMessage= formatMessage(event.getSelection());
		fStatusLineManager.setMessage(statusBarMessage);
	}


	protected String formatMessage(ISelection sel) {
		if (sel instanceof IStructuredSelection && !sel.isEmpty()) {
			IStructuredSelection selection= (IStructuredSelection) sel;

			int nElements= selection.size();
			if (nElements > 1) {
				return MessageFormat.format(Messages.StatusBarUpdater_num_elements_selected, String.valueOf(nElements));
			} else {
				Object elem= selection.getFirstElement();
//				if (elem instanceof ISourceEntity) {
//					return formatJavaElementMessage((ISourceEntity) elem);
//				} 
				//else 
				if (elem instanceof IResource) {
					return formatResourceMessage((IResource) elem);
				} 
//				else if (elem instanceof PackageFragmentRootContainer) {
//					PackageFragmentRootContainer container= (PackageFragmentRootContainer) elem;
//					return container.getLabel() + JavaElementLabels.CONCAT_STRING + container.getJavaProject().getElementName();
//				} else if (elem instanceof IJarEntryResource) {
//					IJarEntryResource jarEntryResource= (IJarEntryResource) elem;
//					StringBuffer buf= new StringBuffer(BasicElementLabels.getResourceName(jarEntryResource.getName()));
//					buf.append(JavaElementLabels.CONCAT_STRING);
//					IPath fullPath= jarEntryResource.getFullPath();
//					if (fullPath.segmentCount() > 1) {
//						buf.append(BasicElementLabels.getPathLabel(fullPath.removeLastSegments(1), false));
//						buf.append(JavaElementLabels.CONCAT_STRING);
//					}
//					buf.append(JavaElementLabels.getElementLabel(jarEntryResource.getPackageFragmentRoot(), JavaElementLabels.ROOT_POST_QUALIFIED));
//					return buf.toString();
//				} 
				else if (elem instanceof IAdaptable) {
					IWorkbenchAdapter wbadapter= (IWorkbenchAdapter) ((IAdaptable)elem).getAdapter(IWorkbenchAdapter.class);
					if (wbadapter != null) {
						return wbadapter.getLabel(elem);
					}
				}
			}
		}
		return "";  //$NON-NLS-1$
	}

//	private String formatJavaElementMessage(ISourceEntity element) {
//		return JavaElementLabels.getElementLabel(element, LABEL_FLAGS);
//	}
//
	private String formatResourceMessage(IResource element) {
		IContainer parent= element.getParent();
		if (parent != null && parent.getType() != IResource.ROOT)
			return BasicElementLabels.getResourceName(element.getName()) + " - " + BasicElementLabels.getPathLabel(parent.getFullPath(), false);
		else
			return BasicElementLabels.getResourceName(element.getName());
	}

}
