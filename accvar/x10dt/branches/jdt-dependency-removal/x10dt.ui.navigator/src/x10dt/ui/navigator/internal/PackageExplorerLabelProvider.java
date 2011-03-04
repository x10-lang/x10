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
package x10dt.ui.navigator.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.model.ISourceFolder;
import org.eclipse.imp.ui.ElementLabels;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkingSet;

import x10dt.search.ui.typeHierarchy.AppearanceAwareLabelProvider;
import x10dt.search.ui.typeHierarchy.X10ElementImageProvider;

/**
 * Provides the labels for the Package Explorer.
 * <p>
 * It provides labels for the packages in hierarchical layout and in all
 * other cases delegates it to its super class.
 * </p>
 * @since 2.1
 */
public class PackageExplorerLabelProvider extends AppearanceAwareLabelProvider {

	private PackageExplorerContentProvider fContentProvider;
	private Map fWorkingSetImages;

	private boolean fIsFlatLayout;
	private PackageExplorerProblemsDecorator fProblemDecorator;

	public PackageExplorerLabelProvider(PackageExplorerContentProvider cp) {
		super(DEFAULT_TEXTFLAGS | ElementLabels.P_COMPRESSED | ElementLabels.ALL_CATEGORY, DEFAULT_IMAGEFLAGS | X10ElementImageProvider.SMALL_ICONS);

		fProblemDecorator= new PackageExplorerProblemsDecorator();
		addLabelDecorator(fProblemDecorator);
		Assert.isNotNull(cp);
		fContentProvider= cp;
		fWorkingSetImages= null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.viewsupport.JavaUILabelProvider#getStyledText(java.lang.Object)
	 */
	public StyledString getStyledText(Object element) {
		String text= getSpecificText(element);
		if (text != null) {
			return new StyledString(decorateText(text, element));
		}
		return super.getStyledText(element);
	}

	private String getSpecificText(Object element) {
		if (!fIsFlatLayout && element instanceof ISourceFolder) {
			ISourceFolder fragment = (ISourceFolder) element;
			Object parent= fContentProvider.getHierarchicalPackageParent(fragment);
			if (parent instanceof ISourceFolder) {
				return getNameDelta((ISourceFolder) parent, fragment);
			} else if (parent instanceof IFolder) { // bug 152735
				return getNameDelta((IFolder) parent, fragment);
			}
		} 
		else 
			if (element instanceof IWorkingSet) {
			return ((IWorkingSet) element).getLabel();
		}
		return null;
	}

	public String getText(Object element) {
		String text= getSpecificText(element);
		if (text != null) {
			return decorateText(text, element);
		}
		return super.getText(element);
	}

	private String getNameDelta(ISourceFolder parent, ISourceFolder fragment) {
		String prefix= parent.getName() + '.';
		String fullName= fragment.getName();
		if (fullName.startsWith(prefix)) {
			return fullName.substring(prefix.length());
		}
		return fullName;
	}

	private String getNameDelta(IFolder parent, ISourceFolder fragment) {
		IPath prefix= parent.getFullPath();
		IPath fullPath= fragment.getPath();
		if (prefix.isPrefixOf(fullPath)) {
			StringBuffer buf= new StringBuffer();
			for (int i= prefix.segmentCount(); i < fullPath.segmentCount(); i++) {
				if (buf.length() > 0)
					buf.append('.');
				buf.append(fullPath.segment(i));
			}
			return buf.toString();
		}
		return fragment.getName();
	}

	public Image getImage(Object element) {
		if (element instanceof IWorkingSet) {
			ImageDescriptor image= ((IWorkingSet)element).getImageDescriptor();
			if (image == null) {
				return null;
			}
			if (fWorkingSetImages == null) {
				fWorkingSetImages= new HashMap();
			}

			Image result= (Image) fWorkingSetImages.get(image);
			if (result == null) {
				result= image.createImage();
				fWorkingSetImages.put(image, result);
			}
			return decorateImage(result, element);
		}
		return super.getImage(element);
	}

	public void setIsFlatLayout(boolean state) {
		fIsFlatLayout= state;
		fProblemDecorator.setIsFlatLayout(state);
	}

	public void dispose() {
		if (fWorkingSetImages != null) {
			for (Iterator iter= fWorkingSetImages.values().iterator(); iter.hasNext();) {
				((Image)iter.next()).dispose();
			}
		}
		super.dispose();
	}
}
