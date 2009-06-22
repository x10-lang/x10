package org.eclipse.imp.x10dt.core.preferences.generated;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.TabbedPreferencesPage;
import org.eclipse.imp.x10dt.core.preferences.InstancePreferencesTab;
import org.eclipse.imp.x10dt.core.preferences.PreferencesTab;
import org.eclipse.imp.x10dt.core.preferences.fields.FieldEditor;
import org.eclipse.imp.x10dt.core.preferences.fields.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.osgi.service.prefs.Preferences;

//		 TODO:  Import additional classes for specific field types from
//		 org.eclipse.uide.preferences.fields

/**
 * The instance level preferences tab.
 */


public class X10PreferencesInstanceTab extends InstancePreferencesTab {

	public X10PreferencesInstanceTab(IPreferencesService prefService) {
		super(prefService);
	}

	/**
	 * Creates specific preference fields with settings appropriate to
	 * the instance preferences level.
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

		IntegerFieldEditor TabSize = fPrefUtils_x10.makeNewIntegerField(
			page, tab, fPrefService,
			"instance", "TabSize", "TabSize",
			parent,
			true, true,
			true, String.valueOf(8),
			false, "0",
			true);
//			Link TabSizeDetailsLink = fPrefUtils_x10.createDetailsLink(parent, TabSize, TabSize.getTextControl().getParent(), "Details ...");

		fields.add(TabSize);


		IntegerFieldEditor NumPlaces = fPrefUtils_x10.makeNewIntegerField(
			page, tab, fPrefService,
			"instance", "NumPlaces", "NumPlaces",
			parent,
			true, true,
			true, String.valueOf(8),
			false, "0",
			true);
//			Link NumPlacesDetailsLink = fPrefUtils_x10.createDetailsLink(parent, NumPlaces, NumPlaces.getTextControl().getParent(), "Details ...");

		fields.add(NumPlaces);

		FieldEditor[] fieldsArray = new FieldEditor[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			fieldsArray[i] = (FieldEditor) fields.get(i);
		}
		return fieldsArray;
	}
}
