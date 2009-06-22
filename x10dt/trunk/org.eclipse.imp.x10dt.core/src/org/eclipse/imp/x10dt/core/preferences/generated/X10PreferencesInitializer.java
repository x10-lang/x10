/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

package org.eclipse.imp.x10dt.core.preferences.generated;

import org.eclipse.imp.preferences.PreferencesInitializer;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.x10dt.core.X10Plugin;

/**
 * Initializations of default values for preferences.
 */


public class X10PreferencesInitializer extends PreferencesInitializer {
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferencesService service = X10Plugin.getPreferencesService();

		service.setIntPreference(IPreferencesService.DEFAULT_LEVEL, X10PreferencesConstants.P_TABSIZE, 4);
		service.setIntPreference(IPreferencesService.DEFAULT_LEVEL, X10PreferencesConstants.P_NUMPLACES, 4);
	}

	/*
	 * Clear (remove) any preferences set on the given level.
	 */
	public void clearPreferencesOnLevel(String level) {
		IPreferencesService service = X10Plugin.getPreferencesService();
		service.clearPreferencesAtLevel(level);

	}
}
