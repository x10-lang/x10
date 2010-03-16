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

		IntegerFieldEditor TabSize = fPrefUtils.makeNewIntegerField(
			page, this, fPrefService,
			"instance", "TabSize", "Tab size",
			"The number of spaces equivalent to one tab in the source editor",
			parent,
			true, true,
			false, String.valueOf(0),
			false, "0",
			true);
		fields.add(TabSize);


		FontFieldEditor SourceFont = fPrefUtils.makeNewFontField(
			page, this, fPrefService,
			"instance", "SourceFont", "Source font",
			"The font to use in the source code editor",
			parent,
			true, true,
			true);
		fields.add(SourceFont);


		DirectoryFieldEditor DefaultRuntime = fPrefUtils.makeNewDirectoryField(
			page, this, fPrefService,
			"instance", "DefaultRuntime", "Default runtime",
			"Folder containing the default X10 runtime libraries",
			parent,
			true, true,
			false, "Unspecified",
			true, "",
			true);
		fields.add(DefaultRuntime);


		BooleanFieldEditor BadPlaceRuntimeCheck = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "BadPlaceRuntimeCheck", "Bad place runtime check",
			"Enable the generation of code to check for direct accesses to data at another place",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(BadPlaceRuntimeCheck);


		BooleanFieldEditor LoopOptimizations = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "LoopOptimizations", "Loop optimizations",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(LoopOptimizations);


		BooleanFieldEditor ArrayOptimizations = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "ArrayOptimizations", "Array optimizations",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(ArrayOptimizations);


		BooleanFieldEditor PermitAssert = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "PermitAssert", "Permit asserts",
			"",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(PermitAssert);


		BooleanFieldEditor EchoCompileArgumentsToConsole = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"instance", "EchoCompileArgumentsToConsole", "Echo compile arguments to console",
			"The arguments to x10 compile command will be echoed in the console view.",
			parent,
			true, true,
			false, false,
			false, false,
			true);
		fields.add(EchoCompileArgumentsToConsole);


		IntegerFieldEditor NumPlaces = fPrefUtils.makeNewIntegerField(
			page, this, fPrefService,
			"instance", "NumPlaces", "Num places",
			"The number of logical places upon which the application is executed",
			parent,
			true, true,
			false, String.valueOf(0),
			false, "0",
			true);
		fields.add(NumPlaces);


		StringFieldEditor AdditionalCompilerOptions = fPrefUtils.makeNewStringField(
			page, this, fPrefService,
			"instance", "AdditionalCompilerOptions", "Additional compiler options",
			"",
			parent,
			true, true,
			false, "Unspecified",
			true, "",
			true);
		AdditionalCompilerOptions.setValidator(new org.eclipse.imp.x10dt.core.preferences.fields.CompilerOptionsValidator());
		fields.add(AdditionalCompilerOptions);

		return fields.toArray(new FieldEditor[fields.size()]);
	}
}
