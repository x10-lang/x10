/******************************************/
/* WARNING: GENERATED FILE - DO NOT EDIT! */
/******************************************/
package x10dt.core.preferences.generated;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
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
public class X10RuntimeInstanceTab extends InstancePreferencesTab {

	public X10RuntimeInstanceTab(IPreferencesService prefService) {
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

		DirectoryFieldEditor DefaultRuntime = fPrefUtils.makeNewDirectoryField(
			page, this, fPrefService,
			"instance", "DefaultRuntime", "Default runtime",
			"Folder containing the default X10 runtime libraries",
			parent,
			true, true,
			true, "",
			false);
		fields.add(DefaultRuntime);


		IntegerFieldEditor NumberOfPlaces = fPrefUtils.makeNewIntegerField(
			page, this, fPrefService,
			"instance", "NumberOfPlaces", "Number of places",
			"The number of logical places upon which the application is executed",
			parent,
			true, true,
			true, "0",
			false);
		fields.add(NumberOfPlaces);

		return fields.toArray(new FieldEditor[fields.size()]);
	}
}
