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
 * The default level preferences tab.
 */
public class X10RuntimeDefaultTab extends DefaultPreferencesTab {

	public X10RuntimeDefaultTab(IPreferencesService prefService) {
		super(prefService, true);
	}

	/**
	 * Creates a language-specific preferences initializer.
	 *
	 * @return    The preference initializer to be used to initialize
	 *            preferences in this tab
	 */
	public AbstractPreferenceInitializer getPreferenceInitializer() {
		X10Initializer preferencesInitializer = new X10Initializer();
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

		DirectoryFieldEditor DefaultRuntime = fPrefUtils.makeNewDirectoryField(
			page, this, fPrefService,
			"default", "DefaultRuntime", "Default runtime",
			"Folder containing the default X10 runtime libraries",
			parent,
			true, true,
			false, "Unspecified",
			true, "",
			false);
		fields.add(DefaultRuntime);


		IntegerFieldEditor NumberOfPlaces = fPrefUtils.makeNewIntegerField(
			page, this, fPrefService,
			"default", "NumberOfPlaces", "Number of places",
			"The number of logical places upon which the application is executed",
			parent,
			true, true,
			false, String.valueOf(0),
			false, "0",
			false);
		fields.add(NumberOfPlaces);

		return fields.toArray(new FieldEditor[fields.size()]);
	}
}
