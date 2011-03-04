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
public class X10FormattingDefaultTab extends DefaultPreferencesTab {

	public X10FormattingDefaultTab(IPreferencesService prefService) {
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

		IntegerFieldEditor indentWidth = fPrefUtils.makeNewIntegerField(
			page, this, fPrefService,
			"default", "indentWidth", "indent width",
			"The number of spaces by which to indent various entities relative to their containing constructs",
			parent,
			true, true,
			false, String.valueOf(0),
			false, "0",
			false);
		fields.add(indentWidth);


		BooleanFieldEditor indentBlockStatements = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"default", "indentBlockStatements", "indent block statements",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			false);
		fields.add(indentBlockStatements);


		BooleanFieldEditor indentMethodBody = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"default", "indentMethodBody", "indent method body",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			false);
		fields.add(indentMethodBody);


		BooleanFieldEditor indentTypeBody = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"default", "indentTypeBody", "indent type body",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			false);
		fields.add(indentTypeBody);

		return fields.toArray(new FieldEditor[fields.size()]);
	}
}
