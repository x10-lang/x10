package x10dt.core.preferences.generated;

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
 * The configuration level preferences tab.
 */
public class X10RuntimeConfigurationTab extends ConfigurationPreferencesTab {

	public X10RuntimeConfigurationTab(IPreferencesService prefService) {
		super(prefService, true);
	}

	/**
	 * Creates specific preference fields with settings appropriate to
	 * the configuration preferences level.
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
			"configuration", "DefaultRuntime", "Default runtime",
			"Folder containing the default X10 runtime libraries",
			parent,
			true, true,
			false, "Unspecified",
			true, "",
			true);
		fields.add(DefaultRuntime);


		IntegerFieldEditor NumberOfPlaces = fPrefUtils.makeNewIntegerField(
			page, this, fPrefService,
			"configuration", "NumberOfPlaces", "Number of places",
			"The number of logical places upon which the application is executed",
			parent,
			true, true,
			false, String.valueOf(0),
			false, "0",
			true);
		fields.add(NumberOfPlaces);

		return fields.toArray(new FieldEditor[fields.size()]);
	}
}
