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
package x10dt.ui.navigator.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = PreferenceConstants.getPreferenceStore();
		PreferenceConstants.initializeDefaultValues(store);
	}

	public static void setThemeBasedPreferences(IPreferenceStore store, boolean fireEvent) {
//		ColorRegistry registry= null;
//		if (PlatformUI.isWorkbenchRunning())
//			registry= PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
	}

//	/**
//	 * Sets the default value and fires a property
//	 * change event if necessary.
//	 *
//	 * @param store	the preference store
//	 * @param key the preference key
//	 * @param newValue the new value
//	 * @param fireEvent <code>false</code> if no event should be fired
//	 * @since 3.4
//	 */
//	private static void setDefault(IPreferenceStore store, String key, RGB newValue, boolean fireEvent) {
//		if (!fireEvent) {
//			PreferenceConverter.setDefault(store, key, newValue);
//			return;
//		}
//
//		RGB oldValue= null;
//		if (store.isDefault(key))
//			oldValue= PreferenceConverter.getDefaultColor(store, key);
//
//		PreferenceConverter.setDefault(store, key, newValue);
//
//		if (oldValue != null && !oldValue.equals(newValue))
//			store.firePropertyChangeEvent(key, oldValue, newValue);
//	}
//
//	/**
//	 * Returns the RGB for the given key in the given color registry.
//	 *
//	 * @param registry the color registry
//	 * @param key the key for the constant in the registry
//	 * @param defaultRGB the default RGB if no entry is found
//	 * @return RGB the RGB
//	 * @since 3.4
//	 */
//	private static RGB findRGB(ColorRegistry registry, String key, RGB defaultRGB) {
//		if (registry == null)
//			return defaultRGB;
//
//		RGB rgb= registry.getRGB(key);
//		if (rgb != null)
//			return rgb;
//
//		return defaultRGB;
//	}

}