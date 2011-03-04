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
package x10dt.ui.typeHierarchy;

import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import x10dt.search.core.elements.IMemberInfo;
import x10dt.search.core.elements.IMethodInfo;
import x10dt.search.core.elements.ITypeInfo;
import x10dt.search.core.engine.ITypeHierarchy;
import x10dt.ui.X10DTUIPlugin;
import x10dt.ui.typeHierarchy.SearchUtils.Flags;


/**
 * Label provider for the hierarchy viewers. Types in the hierarchy that are not belonging to the
 * input scope are rendered differently.
  */
public class HierarchyLabelProvider extends AppearanceAwareLabelProvider {

	private static class FocusDescriptor extends CompositeImageDescriptor {
		private ImageDescriptor fBase;
		public FocusDescriptor(ImageDescriptor base) {
			fBase= base;
		}
		protected void drawCompositeImage(int width, int height) {
			drawImage(getImageData(fBase), 0, 0);
			drawImage(getImageData(X10PluginImages.DESC_OVR_FOCUS), 0, 0);
		}

		private ImageData getImageData(ImageDescriptor descriptor) {
			ImageData data= descriptor.getImageData(); // see bug 51965: getImageData can return null
			if (data == null) {
				data= DEFAULT_IMAGE_DATA;
				X10DTUIPlugin.getInstance().writeErrorMsg("Image data not available: " + descriptor.toString()); //$NON-NLS-1$
			}
			return data;
		}

		protected Point getSize() {
			return X10ElementImageProvider.BIG_SIZE;
		}
		public int hashCode() {
			return fBase.hashCode();
		}
		public boolean equals(Object object) {
			return object != null && FocusDescriptor.class.equals(object.getClass()) && ((FocusDescriptor)object).fBase.equals(fBase);
		}
	}

	private Color fSpecialColor;

	private ViewerFilter fFilter;

	private TypeHierarchyLifeCycle fHierarchy;

	public HierarchyLabelProvider(TypeHierarchyLifeCycle lifeCycle) {
		super(DEFAULT_TEXTFLAGS | X10ElementLabels.USE_RESOLVED, DEFAULT_IMAGEFLAGS);

		fHierarchy= lifeCycle;
		fFilter= null;
	}

	/**
	 * @return Returns the filter.
	 */
	public ViewerFilter getFilter() {
		return fFilter;
	}

	/**
	 * @param filter The filter to set.
	 */
	public void setFilter(ViewerFilter filter) {
		fFilter= filter;
	}

	protected boolean isDifferentScope(ITypeInfo type) {
		if (fFilter != null && !fFilter.select(null, null, type)) {
			return true;
		}

		IMemberInfo input= fHierarchy.getInputElement();
		if (input == null || input instanceof ITypeInfo) {
			return false;
		}

//		IJavaElement parent= type.getAncestor(input.getElementType());
//		if (input.getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
//			if (parent == null || parent.getElementName().equals(input.getElementName())) {
//				return false;
//			}
//		} else if (input.equals(parent)) {
//			return false;
//		}
		return true;
	}

	/* (non-Javadoc)
	 * @see ILabelProvider#getImage
	 */
	public Image getImage(Object element) {
		Image result= null;
		if (element instanceof ITypeInfo) {
			ImageDescriptor desc= getTypeImageDescriptor((ITypeInfo) element);
			if (desc != null) {
				if (element.equals(fHierarchy.getInputElement())) {
					desc= new FocusDescriptor(desc);
				}
				result= X10DTUIPlugin.getImageDescriptorRegistry().get(desc);
			}
		} else {
			result= fImageLabelProvider.getImageLabel(element, evaluateImageFlags(element));
		}
		return decorateImage(result, element);
	}

	private ImageDescriptor getTypeImageDescriptor(ITypeInfo type) {
		ITypeHierarchy hierarchy= fHierarchy.getHierarchy();
		if (hierarchy == null) {
			return new X10ElementImageDescriptor(X10PluginImages.DESC_OBJS_CLASS, 0, X10ElementImageProvider.BIG_SIZE);
		}

		int flags= type.getX10FlagsCode();
		if (flags == -1) {
			return new X10ElementImageDescriptor(X10PluginImages.DESC_OBJS_CLASS, 0, X10ElementImageProvider.BIG_SIZE);
		}

		boolean isInterface= Flags.isInterface(flags);
		ITypeInfo declaringType= type.getDeclaringType();
		boolean isInner= declaringType != null;
		boolean isInInterfaceOrAnnotation= false;
		if (isInner) {
			isInInterfaceOrAnnotation= Flags.isInterface(declaringType.getX10FlagsCode());
		}

		ImageDescriptor desc= X10ElementImageProvider.getTypeImageDescriptor(isInner, isInInterfaceOrAnnotation, flags, isDifferentScope(type));

		int adornmentFlags= 0;
		if (Flags.isFinal(flags)) {
			adornmentFlags |= X10ElementImageDescriptor.FINAL;
		}
		if (Flags.isAbstract(flags) && !isInterface) {
			adornmentFlags |= X10ElementImageDescriptor.ABSTRACT;
		}
		if (Flags.isStatic(flags)) {
			adornmentFlags |= X10ElementImageDescriptor.STATIC;
		}
//		if (Flags.isDeprecated(flags)) {
//			adornmentFlags |= X10ElementImageDescriptor.DEPRECATED;
//		}

		return new X10ElementImageDescriptor(desc, adornmentFlags, X10ElementImageProvider.BIG_SIZE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
	 */
	public Color getForeground(Object element) {
		if (element instanceof IMethodInfo) {
			if (fSpecialColor == null) {
				fSpecialColor= Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
			}
			return fSpecialColor;
		} else if (element instanceof ITypeInfo && isDifferentScope((ITypeInfo) element)) {
			return JFaceResources.getColorRegistry().get(JFacePreferences.QUALIFIER_COLOR);
		}
		return null;
	}



}
