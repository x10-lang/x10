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



import org.eclipse.ui.IViewPart;

import x10dt.search.core.elements.IMemberInfo;


/**
 * The standard type hierarchy view presents a type hierarchy for a given input class
 * or interface. Visually, this view consists of a pair of viewers, one showing the type
 * hierarchy, the other showing the members of the type selected in the first.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @see ID_TYPE_HIERARCHY
 *
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ITypeHierarchyViewPart extends IViewPart {

	/**
	 * Constant used for the vertical view layout.
	 * @since 3.3
	 */
	public static final int VIEW_LAYOUT_VERTICAL= 0;

	/**
	 * Constant used for the horizontal view layout.
	 * @since 3.3
	 */
	public static final int VIEW_LAYOUT_HORIZONTAL= 1;

	/**
	 * Constant used for the single view layout (no members view)
	 * @since 3.3
	 */
	public static final int VIEW_LAYOUT_SINGLE= 2;

	/**
	 * Constant used for the automatic view layout.
	 * @since 3.3
	 */
	public static final int VIEW_LAYOUT_AUTOMATIC= 3;

	/**
	 * Constant used for the 'classic' type hierarchy mode.
	 * @since 3.3
	 */
	public static final int HIERARCHY_MODE_CLASSIC= 2;

	/**
	 * Constant used for the super types hierarchy mode.
	 * @since 3.3
	 */
	public static final int HIERARCHY_MODE_SUPERTYPES= 0;

	/**
	 * Constant used for the sub types hierarchy mode.
	 * @since 3.3
	 */
	public static final int HIERARCHY_MODE_SUBTYPES= 1;

	/**
	 * Sets the input element of this type hierarchy view. The following input types are possible
	 * <code>IMember</code> (types, methods, fields..), <code>IPackageFragment</code>, <code>IPackageFragmentRoot</code>
	 * and <code>IJavaProject</code>.
	 *
	 * @param element the input element of this type hierarchy view, or <code>null</code>
	 *  to clear any input
	 *
	 * @since 2.0
	 */
	public void setInputElement(IMemberInfo element);

	/**
	 * Returns the input element of this type hierarchy view.
	 *
	 * @return the input element, or <code>null</code> if no input element is set
	 * @see #setInputElement(IMemberInfo)
	 *
	 * @since 2.0
	 */
	public IMemberInfo getInputElement();

	/**
	 * Locks the the members view and shows the selected members in the hierarchy.
	 *
	 * @param enabled If set, the members view will be locked and the selected members are shown in the hierarchy.
	 *
	 * @since 3.3
	 */
	public void showMembersInHierarchy(boolean enabled);

	/**
	 * If set, the lock mode is enabled.
	 *
	 * @return returns if the lock mode is enabled.
	 *
	 * @since 3.3
	 */
	public boolean isShowMembersInHierarchy();

	/**
	 * Specifies if type names are shown with the parent container's name.
	 *
	 * @param enabled if enabled, the hierarchy will also show the type container names
	 *
	 * @since 3.3
	 */
	public void showQualifiedTypeNames(boolean enabled);

	/**
	 * If set, type names are shown with the parent container's name.
	 *
	 * @return returns if type names are shown with the parent container's name.
	 *
	 * @since 3.3
	 */
	public boolean isQualifiedTypeNamesEnabled();

    /**
     * Returns whether this type hierarchy view's selection automatically tracks the active editor.
     *
     * @return <code>true</code> if linking is enabled, <code>false</code> if not
     *
     * @since 3.3
     */
	public boolean isLinkingEnabled();

    /**
     * Sets whether this type hierarchy view's selection automatically tracks the active editor.
     *
     * @param enabled <code>true</code> to enable, <code>false</code> to disable
     *
     * @since 3.3
     */
	public void setLinkingEnabled(boolean enabled);

	/**
	 * Sets the view layout. Valid inputs are {@link #VIEW_LAYOUT_VERTICAL}, {@link #VIEW_LAYOUT_HORIZONTAL}
	 * {@link #VIEW_LAYOUT_SINGLE} and {@link #VIEW_LAYOUT_AUTOMATIC}.
	 *
	 * @param layout The layout to set
	 *
	 * @since 3.3
	 */
	public void setViewLayout(int layout);

	/**
	 * Returns the currently configured view layout. Possible layouts are {@link #VIEW_LAYOUT_VERTICAL}, {@link #VIEW_LAYOUT_HORIZONTAL}
	 * {@link #VIEW_LAYOUT_SINGLE} and {@link #VIEW_LAYOUT_AUTOMATIC} but clients should also be able to handle yet unknown
	 * layout.
	 *
	 * @return The layout currently set
	 *
	 * @since 3.3
	 */
	public int getViewLayout();

	/**
	 * Sets the hierarchy mode. Valid modes are {@link #HIERARCHY_MODE_SUBTYPES}, {@link #HIERARCHY_MODE_SUPERTYPES}
	 * and {@link #HIERARCHY_MODE_CLASSIC}.
	 *
	 * @param mode The hierarchy mode to set
	 *
	 * @since 3.3
	 */
	public void setHierarchyMode(int mode);

	/**
	 * Returns the currently configured hierarchy mode. Possible modes are {@link #HIERARCHY_MODE_SUBTYPES}, {@link #HIERARCHY_MODE_SUPERTYPES}
	 * and {@link #HIERARCHY_MODE_CLASSIC} but clients should also be able to handle yet unknown modes.
	 *
	 * @return The hierarchy mode currently set
	 *
	 * @since 3.3
	 */
	public int getHierarchyMode();

}
