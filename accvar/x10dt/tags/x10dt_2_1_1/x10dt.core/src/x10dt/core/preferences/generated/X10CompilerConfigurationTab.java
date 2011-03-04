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
public class X10CompilerConfigurationTab extends ConfigurationPreferencesTab {

	public X10CompilerConfigurationTab(IPreferencesService prefService) {
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

		BooleanFieldEditor BadPlaceRuntimeCheck = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"configuration", "BadPlaceRuntimeCheck", "Bad place runtime check",
			"Enable the generation of code to check for direct accesses to data at another place",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(BadPlaceRuntimeCheck);


		BooleanFieldEditor PermitAssert = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"configuration", "PermitAssert", "Permit asserts",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(PermitAssert);


		BooleanFieldEditor StaticCalls = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"configuration", "StaticCalls", "Strict static call checking",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(StaticCalls);


		BooleanFieldEditor VerboseCalls = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"configuration", "VerboseCalls", "Emit warnings for casts inserted at dynamically-checked calls",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(VerboseCalls);


		BooleanFieldEditor EchoCompileArgumentsToConsole = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"configuration", "EchoCompileArgumentsToConsole", "Echo compile arguments to console",
			"The arguments to x10 compile command will be echoed in the console view.",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(EchoCompileArgumentsToConsole);


		StringFieldEditor AdditionalCompilerOptions = fPrefUtils.makeNewStringField(
			page, this, fPrefService,
			"configuration", "AdditionalCompilerOptions", "Additional compiler options",
			"",
			parent,
			true, true,
			false, "Unspecified",
			true, "",
			true);
		AdditionalCompilerOptions.setValidator(new x10dt.core.preferences.fields.CompilerOptionsValidator());
		fields.add(AdditionalCompilerOptions);

		return fields.toArray(new FieldEditor[fields.size()]);
	}
}
