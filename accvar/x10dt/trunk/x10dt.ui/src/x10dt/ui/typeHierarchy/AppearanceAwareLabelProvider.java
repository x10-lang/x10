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


import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PlatformUI;

import x10dt.ui.X10DTUIPlugin;


/**
 * JavaUILabelProvider that respects settings from the Appearance preference page.
 * Triggers a viewer update when a preference changes.
 */
public class AppearanceAwareLabelProvider extends X10LabelProvider implements IPropertyChangeListener, IPropertyListener {

	public final static long DEFAULT_TEXTFLAGS= X10ElementLabels.ROOT_VARIABLE | X10ElementLabels.T_TYPE_PARAMETERS | X10ElementLabels.M_PARAMETER_TYPES |
		X10ElementLabels.M_APP_TYPE_PARAMETERS | X10ElementLabels.M_APP_RETURNTYPE  | X10ElementLabels.REFERENCED_ROOT_POST_QUALIFIED;
	public final static int DEFAULT_IMAGEFLAGS= X10ElementImageProvider.OVERLAY_ICONS;
	
	private long fTextFlagMask;
	private int fImageFlagMask;

	/**
	 * Constructor for AppearanceAwareLabelProvider.
	 * @param textFlags Flags defined in <code>X10ElementLabels</code>.
	 * @param imageFlags Flags defined in <code>JavaElementImageProvider</code>.
	 */
	public AppearanceAwareLabelProvider(long textFlags, int imageFlags) {
		super(textFlags, imageFlags);
		initMasks();
		X10DTUIPlugin.getInstance().getPreferenceStore().addPropertyChangeListener(this);
		PlatformUI.getWorkbench().getEditorRegistry().addPropertyListener(this);
	}

	/**
	 * Creates a labelProvider with DEFAULT_TEXTFLAGS and DEFAULT_IMAGEFLAGS
	 */
	public AppearanceAwareLabelProvider() {
		this(DEFAULT_TEXTFLAGS, DEFAULT_IMAGEFLAGS);
	}

	private void initMasks() {
		IPreferenceStore store= X10DTUIPlugin.getInstance().getPreferenceStore();
		fTextFlagMask= -1;
		if (!store.getBoolean(X10Constants.APPEARANCE_METHOD_RETURNTYPE)) {
			fTextFlagMask ^= X10ElementLabels.M_APP_RETURNTYPE;
		}
		if (!store.getBoolean(X10Constants.APPEARANCE_METHOD_TYPEPARAMETERS)) {
			fTextFlagMask ^= X10ElementLabels.M_APP_TYPE_PARAMETERS;
		}
		if (!store.getBoolean(X10Constants.APPEARANCE_COMPRESS_PACKAGE_NAMES)) {
			fTextFlagMask ^= X10ElementLabels.P_COMPRESSED;
		}
		if (!store.getBoolean(X10Constants.APPEARANCE_CATEGORY)) {
			fTextFlagMask ^= X10ElementLabels.ALL_CATEGORY;
		}

		fImageFlagMask= -1;
	}

	/*
	 * @see IPropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String property= event.getProperty();
		if (property.equals(X10Constants.APPEARANCE_METHOD_RETURNTYPE)
				|| property.equals(X10Constants.APPEARANCE_METHOD_TYPEPARAMETERS)
				|| property.equals(X10Constants.APPEARANCE_CATEGORY)
				|| property.equals(X10Constants.APPEARANCE_PKG_NAME_PATTERN_FOR_PKG_VIEW)
				|| property.equals(X10Constants.APPEARANCE_COMPRESS_PACKAGE_NAMES)) {
			initMasks();
			LabelProviderChangedEvent lpEvent= new LabelProviderChangedEvent(this, null); // refresh all
			fireLabelProviderChanged(lpEvent);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPropertyListener#propertyChanged(java.lang.Object, int)
	 */
	public void propertyChanged(Object source, int propId) {
		if (propId == IEditorRegistry.PROP_CONTENTS) {
			fireLabelProviderChanged(new LabelProviderChangedEvent(this, null)); // refresh all
		}
	}

	/*
	 * @see IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		X10DTUIPlugin.getInstance().getPreferenceStore().removePropertyChangeListener(this);
		PlatformUI.getWorkbench().getEditorRegistry().removePropertyListener(this);
		super.dispose();
	}

	/*
	 * @see JavaUILabelProvider#evaluateImageFlags()
	 */
	protected int evaluateImageFlags(Object element) {
		return getImageFlags() & fImageFlagMask;
	}

	/*
	 * @see JavaUILabelProvider#evaluateTextFlags()
	 */
	protected long evaluateTextFlags(Object element) {
		return getTextFlags() & fTextFlagMask;
	}

}
