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


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

/**
 * The class provides API to extend type selection dialogs like the
 * open type dialog.
 * <p>
 * The class should be subclassed by clients wishing to extend the type
 * selection dialog.
 * </p>
 *
 * @see org.eclipse.jdt.ui.JavaUI#createTypeDialog(org.eclipse.swt.widgets.Shell, org.eclipse.jface.operation.IRunnableContext, org.eclipse.jdt.core.search.IJavaSearchScope, int, boolean, String, TypeSelectionExtension)
 * @since 3.2
 */
public abstract class TypeSelectionExtension {

	private ITypeSelectionComponent fComponent;

	/**
	 * Initializes the type dialog extension with the given type dialog
	 *
	 * @param component the type dialog hosting this extension
	 */
	public final void initialize(ITypeSelectionComponent component) {
		fComponent= component;
	}

	/**
	 * Returns the type selection dialog or <code>null</code> if
	 * the extension has not been initialized yet.
	 *
	 * @return the type selection dialog or <code>null</code>
	 */
	public final ITypeSelectionComponent getTypeSelectionComponent() {
		return fComponent;
	}

	/**
	 * Creates the content area which the extensions contributes to the
	 * type selection dialog. The area will be presented between the
	 * table showing the list of types and the optional status line.
	 *
	 * @param parent the parent of the additional content area
	 * @return the additional content area or <code>null</code> if no
	 *  additional content area is required
	 */
	public Control createContentArea(Composite parent) {
		return null;
	}

	/**
	 * Returns the filter extension or <code>null</code> if
	 * no additional filtering is required.
	 *
	 * @return the additional filter extension
	 */
	public ITypeInfoFilterExtension getFilterExtension() {
		return null;
	}

	/**
	 * Returns the selection validator or <code>null</code> if
	 * selection validation is not required. The elements passed
	 * to the selection validator are of type {@link IType}.
	 *
	 * @return the selection validator or <code>null</code>
	 */
	public ISelectionStatusValidator getSelectionValidator() {
		return null;
	}

	/**
	 * Returns an image provider or <code>null</code> if the standard
	 * images should be used.
	 *
	 * @return the image provider
	 */
	public ITypeInfoImageProvider getImageProvider() {
		return null;
	}
}
