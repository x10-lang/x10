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
public class X10FormattingProjectTab extends ProjectPreferencesTab {

	public X10FormattingProjectTab(IPreferencesService prefService) {
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

		IntegerFieldEditor indentWidth = fPrefUtils.makeNewIntegerField(
			page, this, fPrefService,
			"project", "indentWidth", "indent width",
			"The number of spaces by which to indent various entities relative to their containing constructs",
			parent,
			false, false,
			false, String.valueOf(0),
			false, "0",
			true);
		fields.add(indentWidth);


		BooleanFieldEditor indentBlockStatements = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "indentBlockStatements", "indent block statements",
			"",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(indentBlockStatements);


		BooleanFieldEditor indentMethodBody = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "indentMethodBody", "indent method body",
			"",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(indentMethodBody);


		BooleanFieldEditor indentTypeBody = fPrefUtils.makeNewBooleanField(
			page, this, fPrefService,
			"project", "indentTypeBody", "indent type body",
			"",
			parent,
			false, false,
			false, false,
			false, false,
			true);
		fields.add(indentTypeBody);

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
		IntegerFieldEditor indentWidth = (IntegerFieldEditor) fFields[0];
		Link indentWidthDetailsLink = (Link) fDetailsLinks.get(0);
		BooleanFieldEditor indentBlockStatements = (BooleanFieldEditor) fFields[1];
		Link indentBlockStatementsDetailsLink = (Link) fDetailsLinks.get(1);
		BooleanFieldEditor indentMethodBody = (BooleanFieldEditor) fFields[2];
		Link indentMethodBodyDetailsLink = (Link) fDetailsLinks.get(2);
		BooleanFieldEditor indentTypeBody = (BooleanFieldEditor) fFields[3];
		Link indentTypeBodyDetailsLink = (Link) fDetailsLinks.get(3);

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

				fPrefUtils.setField(indentWidth, indentWidth.getHolder());
				indentWidth.getTextControl().setEditable(true);
				indentWidth.getTextControl().setEnabled(true);
				indentWidth.setEnabled(true, indentWidth.getParent());
				indentWidthDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(indentBlockStatements, indentBlockStatements.getHolder());
				indentBlockStatements.getChangeControl().setEnabled(true);
				indentBlockStatementsDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(indentMethodBody, indentMethodBody.getHolder());
				indentMethodBody.getChangeControl().setEnabled(true);
				indentMethodBodyDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				fPrefUtils.setField(indentTypeBody, indentTypeBody.getHolder());
				indentTypeBody.getChangeControl().setEnabled(true);
				indentTypeBodyDetailsLink.setEnabled(selectedProjectCombo.getText().length() > 0);

				clearModifiedMarksOnLabels();
			}

			// Add property change listeners
			if (indentWidth.getHolder() != null) addProjectPreferenceChangeListeners(indentWidth, "indentWidth", indentWidth.getHolder());
			if (indentBlockStatements.getHolder() != null) addProjectPreferenceChangeListeners(indentBlockStatements, "indentBlockStatements", indentBlockStatements.getHolder());
			if (indentMethodBody.getHolder() != null) addProjectPreferenceChangeListeners(indentMethodBody, "indentMethodBody", indentMethodBody.getHolder());
			if (indentTypeBody.getHolder() != null) addProjectPreferenceChangeListeners(indentTypeBody, "indentTypeBody", indentTypeBody.getHolder());

			haveCurrentListeners = true;
		}

		// Or if we don't have a new project preferences node ...
		if (newNode == null || !(newNode instanceof IEclipsePreferences)) {
			// May happen when the preferences page is first brought up, or
			// if we allow the project to be deselected\nn			// Clear the preferences from the store
			fPrefService.clearPreferencesAtLevel(IPreferencesService.PROJECT_LEVEL);

			// Disable fields and make them non-editable
			if (!composite.isDisposed()) {
				indentWidth.getTextControl().setEditable(false);
				indentWidth.getTextControl().setEnabled(false);
				indentWidth.setEnabled(false, indentWidth.getParent());

				indentBlockStatements.getChangeControl().setEnabled(false);

				indentMethodBody.getChangeControl().setEnabled(false);

				indentTypeBody.getChangeControl().setEnabled(false);

			}

			// Remove listeners
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
			// To help assure that field properties are established properly
			performApply();
		}
	}


}
