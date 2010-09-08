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
public class X10RuntimeProjectTab extends ProjectPreferencesTab {

	public X10RuntimeProjectTab(IPreferencesService prefService) {
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

		DirectoryFieldEditor DefaultRuntime = fPrefUtils.makeNewDirectoryField(
			page, this, fPrefService,
			"project", "DefaultRuntime", "Default runtime",
			"Folder containing the default X10 runtime libraries",
			parent,
			false, false,
			false, "Unspecified",
			true, "",
			true);
		fields.add(DefaultRuntime);


		IntegerFieldEditor NumberOfPlaces = fPrefUtils.makeNewIntegerField(
			page, this, fPrefService,
			"project", "NumberOfPlaces", "Number of places",
			"The number of logical places upon which the application is executed",
			parent,
			false, false,
			false, String.valueOf(0),
			false, "0",
			true);
		fields.add(NumberOfPlaces);

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
		StringFieldEditor DefaultRuntime = (StringFieldEditor) fFields[0];
		Link DefaultRuntimeDetailsLink = (Link) fDetailsLinks.get(0);
		IntegerFieldEditor NumberOfPlaces = (IntegerFieldEditor) fFields[1];
		Link NumberOfPlacesDetailsLink = (Link) fDetailsLinks.get(1);

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

				fPrefUtils.setField(DefaultRuntime, DefaultRuntime.getHolder());
				DefaultRuntime.getTextControl().setEditable(true);
				DefaultRuntime.getTextControl().setEnabled(true);
				DefaultRuntime.setEnabled(true, DefaultRuntime.getParent());
				DefaultRuntimeDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(NumberOfPlaces, NumberOfPlaces.getHolder());
				NumberOfPlaces.getTextControl().setEditable(true);
				NumberOfPlaces.getTextControl().setEnabled(true);
				NumberOfPlaces.setEnabled(true, NumberOfPlaces.getParent());
				NumberOfPlacesDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				clearModifiedMarksOnLabels();
			}

			// Add property change listeners
			if (DefaultRuntime.getHolder() != null) addProjectPreferenceChangeListeners(DefaultRuntime, "DefaultRuntime", DefaultRuntime.getHolder());
			if (NumberOfPlaces.getHolder() != null) addProjectPreferenceChangeListeners(NumberOfPlaces, "NumberOfPlaces", NumberOfPlaces.getHolder());

			haveCurrentListeners = true;
		}

		// Or if we don't have a new project preferences node ...
		if (newNode == null || !(newNode instanceof IEclipsePreferences)) {
			// May happen when the preferences page is first brought up, or
			// if we allow the project to be deselected\nn			// Clear the preferences from the store
			fPrefService.clearPreferencesAtLevel(IPreferencesService.PROJECT_LEVEL);

			// Disable fields and make them non-editable
			if (!composite.isDisposed()) {
				DefaultRuntime.getTextControl().setEditable(false);
				DefaultRuntime.getTextControl().setEnabled(false);
				DefaultRuntime.setEnabled(false, DefaultRuntime.getParent());

				NumberOfPlaces.getTextControl().setEditable(false);
				NumberOfPlaces.getTextControl().setEnabled(false);
				NumberOfPlaces.setEnabled(false, NumberOfPlaces.getParent());

			}

			// Remove listeners
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
			// To help assure that field properties are established properly
			performApply();
		}
	}


}
