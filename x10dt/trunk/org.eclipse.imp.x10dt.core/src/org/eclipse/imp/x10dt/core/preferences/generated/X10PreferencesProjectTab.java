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
 * The project level preferences tab.
 */


public class X10PreferencesProjectTab extends ProjectPreferencesTab {

	public X10PreferencesProjectTab(IPreferencesService prefService) {
		super(prefService);
	}

	/**
	 * Creates specific preference fields with settings appropriate to
	 * the project preferences level.
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
			"project", "TabSize", "TabSize",
			parent,
			false, false,
			true, String.valueOf(8),
			false, "0",
			true);
			Link TabSizeDetailsLink = fPrefUtils.createDetailsLink(parent, TabSize, TabSize.getTextControl().getParent(), "Details ...");

		fields.add(TabSize);


		IntegerFieldEditor NumPlaces = fPrefUtils.makeNewIntegerField(
			page, tab, fPrefService,
			"project", "NumPlaces", "NumPlaces",
			parent,
			false, false,
			true, String.valueOf(8),
			false, "0",
			true);
			Link NumPlacesDetailsLink = fPrefUtils.createDetailsLink(parent, NumPlaces, NumPlaces.getTextControl().getParent(), "Details ...");

		fields.add(NumPlaces);

		FieldEditor[] fieldsArray = new FieldEditor[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			fieldsArray[i] = (FieldEditor) fields.get(i);
		}
		return fieldsArray;
	}


	protected void addressProjectSelection(IPreferencesService.ProjectSelectionEvent event, Composite composite)
	{
		boolean haveCurrentListeners = false;

		Preferences oldeNode = event.getPrevious();
		Preferences newNode = event.getNew();

		if (oldeNode == null && newNode == null) {
			// Happens sometimes when you clear the project selection.
			// Nothing, really, to do in this case ...
			return;
		}

		// If oldeNode is not null, we want to remove any preference-change listeners from it
		if (oldeNode != null && oldeNode instanceof IEclipsePreferences && haveCurrentListeners) {
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
		} else {
			// Print an advisory message if you want to
		}

		// Declare local references to the fields
		IntegerFieldEditor TabSize = (IntegerFieldEditor) fFields[0];
		IntegerFieldEditor NumPlaces = (IntegerFieldEditor) fFields[1];

		// Declare a 'holder' for each preference field; not strictly necessary
		// but helpful in various manipulations of fields and controls to follow
		Composite TabSizeHolder = null;
		Composite NumPlacesHolder = null;
		// If we have a new project preferences node, then do various things
		// to set up the project's preferences
		if (newNode != null && newNode instanceof IEclipsePreferences) {
			// Set project name in the selected-project field
			selectedProjectName.setStringValue(newNode.name());

			// If the containing composite is not disposed, then set field values
			// and make them enabled and editable (as appropriate to the type of field)

			if (!composite.isDisposed()) {
				// Note:  Where there are toggles between fields, it is a good idea to set the
				// properties of the dependent field here according to the values they should have
				// based on the independent field.  There should be listeners to take care of 
				// that sort of adjustment once the tab is established, but when properties are
				// first initialized here, the properties may not always be set correctly through
				// the toggle.  I'm not entirely sure why that happens, except that there may be
				// a race condition between the setting of the dependent values by the listener
				// and the setting of those values here.  If the values are set by the listener
				// first (which might be surprising, but may be possible) then they will be
				// overwritten by values set here--so the values set here should be consistent
				// with what the listener would set.

				TabSizeHolder = TabSize.getTextControl().getParent();
				fPrefUtils.setField(TabSize, TabSizeHolder);
				TabSize.getTextControl().setEditable(true);
				TabSize.getTextControl().setEnabled(true);
				TabSize.setEnabled(true, TabSize.getParent());

				NumPlacesHolder = NumPlaces.getTextControl().getParent();
				fPrefUtils.setField(NumPlaces, NumPlacesHolder);
				NumPlaces.getTextControl().setEditable(true);
				NumPlaces.getTextControl().setEnabled(true);
				NumPlaces.setEnabled(true, NumPlaces.getParent());

				clearModifiedMarksOnLabels();
			}

			// Add property change listeners
			if (TabSizeHolder != null) addProjectPreferenceChangeListeners(TabSize, "TabSize", TabSizeHolder);
			if (NumPlacesHolder != null) addProjectPreferenceChangeListeners(NumPlaces, "NumPlaces", NumPlacesHolder);

			haveCurrentListeners = true;
		}

		// Or if we don't have a new project preferences node ...
		if (newNode == null || !(newNode instanceof IEclipsePreferences)) {
			// May happen when the preferences page is first brought up, or
			// if we allow the project to be deselected\nn			// Unset project name in the tab
			selectedProjectName.setStringValue("none selected");

			// Clear the preferences from the store
			fPrefService.clearPreferencesAtLevel(IPreferencesService.PROJECT_LEVEL);

			// Disable fields and make them non-editable
			if (!composite.isDisposed()) {
				TabSize.getTextControl().setEditable(false);
				TabSize.getTextControl().setEnabled(false);
				TabSize.setEnabled(false, TabSize.getParent());

				NumPlaces.getTextControl().setEditable(false);
				NumPlaces.getTextControl().setEnabled(false);
				NumPlaces.setEnabled(false, NumPlaces.getParent());

			}

			// Remove listeners
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
			// To help assure that field properties are established properly
			performApply();
		}
	}


}
