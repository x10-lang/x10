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
 * The project level preferences tab.
 */
public class X10CompilerProjectTab extends ProjectPreferencesTab {

	public X10CompilerProjectTab(IPreferencesService prefService) {
		super(prefService, true);
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
	protected FieldEditor[] createFields(TabbedPreferencesPage page, Composite parent)
	{
		List<FieldEditor> fields = new ArrayList<FieldEditor>();

		BooleanFieldEditor BadPlaceRuntimeCheck = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "BadPlaceRuntimeCheck", "Bad place runtime check",
			"Enable the generation of code to check for direct accesses to data at another place",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(BadPlaceRuntimeCheck);


		BooleanFieldEditor PermitAssert = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "PermitAssert", "Permit asserts",
			"",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(PermitAssert);


		BooleanFieldEditor StaticCalls = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "StaticCalls", "Strict static call checking",
			"",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(StaticCalls);


		BooleanFieldEditor VerboseCalls = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "VerboseCalls", "Emit warnings for casts inserted at dynamically-checked calls",
			"",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(VerboseCalls);


		BooleanFieldEditor EchoCompileArgumentsToConsole = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "EchoCompileArgumentsToConsole", "Echo compile arguments to console",
			"The arguments to x10 compile command will be echoed in the console view.",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(EchoCompileArgumentsToConsole);


		StringFieldEditor AdditionalCompilerOptions = fPrefUtils.makeNewStringField(
			page, this, fPrefService,
			"project", "AdditionalCompilerOptions", "Additional compiler options",
			"",
			parent,
			false, false,
			false, "Unspecified",
			true, "",
			true);
		AdditionalCompilerOptions.setValidator(new x10dt.core.preferences.fields.CompilerOptionsValidator());
		fields.add(AdditionalCompilerOptions);

		return fields.toArray(new FieldEditor[fields.size()]);
	}


	protected void addressProjectSelection(IPreferencesService.ProjectSelectionEvent event, Composite composite)
	{
		boolean haveCurrentListeners = false;

		Preferences oldNode = event.getPrevious();
		Preferences newNode = event.getNew();

		if (oldNode == null && newNode == null) {
			// Happens sometimes when you clear the project selection.
			// Nothing, really, to do in this case ...
			return;
		}

		// If oldeNode is not null, we want to remove any preference-change listeners from it
		if (oldNode != null && oldNode instanceof IEclipsePreferences && haveCurrentListeners) {
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
		} else {
			// Print an advisory message if you want to
		}

		// Declare local references to the fields
		BooleanFieldEditor BadPlaceRuntimeCheck = (BooleanFieldEditor) fFields[0];
		Link BadPlaceRuntimeCheckDetailsLink = (Link) fDetailsLinks.get(0);
		BooleanFieldEditor PermitAssert = (BooleanFieldEditor) fFields[1];
		Link PermitAssertDetailsLink = (Link) fDetailsLinks.get(1);
		BooleanFieldEditor StaticCalls = (BooleanFieldEditor) fFields[2];
		Link StaticCallsDetailsLink = (Link) fDetailsLinks.get(2);
		BooleanFieldEditor VerboseCalls = (BooleanFieldEditor) fFields[3];
		Link VerboseCallsDetailsLink = (Link) fDetailsLinks.get(3);
		BooleanFieldEditor EchoCompileArgumentsToConsole = (BooleanFieldEditor) fFields[4];
		Link EchoCompileArgumentsToConsoleDetailsLink = (Link) fDetailsLinks.get(4);
		StringFieldEditor AdditionalCompilerOptions = (StringFieldEditor) fFields[5];
		Link AdditionalCompilerOptionsDetailsLink = (Link) fDetailsLinks.get(5);

		// If we have a new project preferences node, then do various things
		// to set up the project's preferences
		if (newNode != null && newNode instanceof IEclipsePreferences) {
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

				fPrefUtils.setField(BadPlaceRuntimeCheck, BadPlaceRuntimeCheck.getHolder());
				BadPlaceRuntimeCheck.getChangeControl().setEnabled(true);
				BadPlaceRuntimeCheckDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(PermitAssert, PermitAssert.getHolder());
				PermitAssert.getChangeControl().setEnabled(true);
				PermitAssertDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(StaticCalls, StaticCalls.getHolder());
				StaticCalls.getChangeControl().setEnabled(true);
				StaticCallsDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(VerboseCalls, VerboseCalls.getHolder());
				VerboseCalls.getChangeControl().setEnabled(true);
				VerboseCallsDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(EchoCompileArgumentsToConsole, EchoCompileArgumentsToConsole.getHolder());
				EchoCompileArgumentsToConsole.getChangeControl().setEnabled(true);
				EchoCompileArgumentsToConsoleDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(AdditionalCompilerOptions, AdditionalCompilerOptions.getHolder());
				AdditionalCompilerOptions.getTextControl().setEditable(true);
				AdditionalCompilerOptions.getTextControl().setEnabled(true);
				AdditionalCompilerOptions.setEnabled(true, AdditionalCompilerOptions.getParent());
				AdditionalCompilerOptionsDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				clearModifiedMarksOnLabels();
			}

			// Add property change listeners
			if (BadPlaceRuntimeCheck.getHolder() != null) addProjectPreferenceChangeListeners(BadPlaceRuntimeCheck, "BadPlaceRuntimeCheck", BadPlaceRuntimeCheck.getHolder());
			if (PermitAssert.getHolder() != null) addProjectPreferenceChangeListeners(PermitAssert, "PermitAssert", PermitAssert.getHolder());
			if (StaticCalls.getHolder() != null) addProjectPreferenceChangeListeners(StaticCalls, "StaticCalls", StaticCalls.getHolder());
			if (VerboseCalls.getHolder() != null) addProjectPreferenceChangeListeners(VerboseCalls, "VerboseCalls", VerboseCalls.getHolder());
			if (EchoCompileArgumentsToConsole.getHolder() != null) addProjectPreferenceChangeListeners(EchoCompileArgumentsToConsole, "EchoCompileArgumentsToConsole", EchoCompileArgumentsToConsole.getHolder());
			if (AdditionalCompilerOptions.getHolder() != null) addProjectPreferenceChangeListeners(AdditionalCompilerOptions, "AdditionalCompilerOptions", AdditionalCompilerOptions.getHolder());

			haveCurrentListeners = true;
		}

		// Or if we don't have a new project preferences node ...
		if (newNode == null || !(newNode instanceof IEclipsePreferences)) {
			// May happen when the preferences page is first brought up, or
			// if we allow the project to be deselected\nn			// Clear the preferences from the store
			fPrefService.clearPreferencesAtLevel(IPreferencesService.PROJECT_LEVEL);

			// Disable fields and make them non-editable
			if (!composite.isDisposed()) {
				BadPlaceRuntimeCheck.getChangeControl().setEnabled(false);

				PermitAssert.getChangeControl().setEnabled(false);

				StaticCalls.getChangeControl().setEnabled(false);

				VerboseCalls.getChangeControl().setEnabled(false);

				EchoCompileArgumentsToConsole.getChangeControl().setEnabled(false);

				AdditionalCompilerOptions.getTextControl().setEditable(false);
				AdditionalCompilerOptions.getTextControl().setEnabled(false);
				AdditionalCompilerOptions.setEnabled(false, AdditionalCompilerOptions.getParent());

			}

			// Remove listeners
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
			// To help assure that field properties are established properly
			performApply();
		}
	}


}
