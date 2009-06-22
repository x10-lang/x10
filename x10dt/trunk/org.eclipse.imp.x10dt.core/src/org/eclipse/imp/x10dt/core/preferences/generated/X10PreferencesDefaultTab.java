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

import java.util.List;
import java.util.ArrayList;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.imp.preferences.*;
import org.eclipse.imp.preferences.fields.*;
import org.osgi.service.prefs.Preferences;

//		 TODO:  Import additional classes for specific field types from
//		 org.eclipse.uide.preferences.fields

/**
 * The default level preferences tab.
 */


public class X10PreferencesDefaultTab extends DefaultPreferencesTab {

	public X10PreferencesDefaultTab(IPreferencesService prefService) {
		super(prefService);
	}

	/**
	 * Creates a language-specific preferences initializer.
	 *
	 * @return    The preference initializer to be used to initialize
	 *            preferences in this tab
	 */
	public AbstractPreferenceInitializer getPreferenceInitializer() {
		X10PreferencesInitializer preferencesInitializer = new X10PreferencesInitializer();
		return preferencesInitializer;
	}

	/**
	 * Creates specific preference fields with settings appropriate to
	 * the default preferences level.
	 *
	 * Overrides an unimplemented method in PreferencesTab.
	 *
	 * @return    An array that contains the created preference fields
	 *
	 */
	protected FieldEditor[] createFields(
		TabbedPreferencesPage page, PreferencesTab tab, String tabLevel,
		Composite parent)
	{
		List fields = new ArrayList();

		IntegerFieldEditor TabSize = fPrefUtils.makeNewIntegerField(
			page, tab, fPrefService,
			"default", "TabSize", "TabSize",
			parent,
			false, false,
			true, String.valueOf(8),
			false, "0",
			false);
			Link TabSizeDetailsLink = fPrefUtils.createDetailsLink(parent, TabSize, TabSize.getTextControl().getParent(), "Details ...");

		fields.add(TabSize);


		IntegerFieldEditor NumPlaces = fPrefUtils.makeNewIntegerField(
			page, tab, fPrefService,
			"default", "NumPlaces", "NumPlaces",
			parent,
			false, false,
			true, String.valueOf(8),
			false, "0",
			false);
			Link NumPlacesDetailsLink = fPrefUtils.createDetailsLink(parent, NumPlaces, NumPlaces.getTextControl().getParent(), "Details ...");

		fields.add(NumPlaces);

		FieldEditor[] fieldsArray = new FieldEditor[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			fieldsArray[i] = (FieldEditor) fields.get(i);
		}
		return fieldsArray;
	}
}
