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

/**
 * The default level preferences tab.
 */
public class X10PreferencesDefaultTab extends DefaultPreferencesTab {

	public X10PreferencesDefaultTab(IPreferencesService prefService) {
		super(prefService, true);
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
	protected FieldEditor[] createFields(TabbedPreferencesPage page, Composite parent)
	{
		List<FieldEditor> fields = new ArrayList<FieldEditor>();

		IntegerFieldEditor TabSize = fPrefUtils.makeNewIntegerField(
			page, this, fPrefService,
			"default", "TabSize", "TabSize", "",
			parent,
			false, false,
			true, String.valueOf(8),
			false, "0",
			false);
		Link TabSizeDetailsLink = fPrefUtils.createDetailsLink(parent, TabSize, TabSize.getTextControl().getParent(), "Details ...");

		fields.add(TabSize);


		IntegerFieldEditor NumPlaces = fPrefUtils.makeNewIntegerField(
			page, this, fPrefService,
			"default", "NumPlaces", "NumPlaces", "",
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
