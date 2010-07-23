/******************************************/
/* WARNING: GENERATED FILE - DO NOT EDIT! */
/******************************************/
package org.eclipse.imp.x10dt.core.preferences.generated;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Group;
import org.eclipse.imp.preferences.*;
import org.eclipse.imp.preferences.fields.*;
import org.osgi.service.prefs.Preferences;


/**
 * The instance level preferences tab.
 */
public class X10InstanceTab extends InstancePreferencesTab {

	public X10InstanceTab(IPreferencesService prefService) {
		super(prefService, true);
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
	protected FieldEditor[] createFields(TabbedPreferencesPage page, Composite parent)
	{
		List<FieldEditor> fields = new ArrayList<FieldEditor>();

		IntegerFieldEditor tabWidth = fPrefUtils.makeNewIntegerField(
			page, this, fPrefService,
			"instance", "tabWidth", "Tab width",
			"The number of spaces equivalent to one tab in the source editor",
			parent,
			true, true,
			true, "0",
			false);
		fields.add(tabWidth);


		BooleanFieldEditor spacesForTabs = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "spacesForTabs", "Use spaces for tabs",
			"",
			parent,
			true, true,
			true, false,
			false);
		fields.add(spacesForTabs);

		return fields.toArray(new FieldEditor[fields.size()]);
	}
}
