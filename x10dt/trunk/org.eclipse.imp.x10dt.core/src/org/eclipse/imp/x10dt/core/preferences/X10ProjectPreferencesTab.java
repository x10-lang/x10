package org.eclipse.imp.x10dt.core.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.ProjectPreferencesTab;
import org.eclipse.imp.preferences.PreferencesUtilities;
import org.eclipse.imp.preferences.fields.BooleanFieldEditor;
import org.eclipse.imp.preferences.fields.ComboFieldEditor;
import org.eclipse.imp.preferences.fields.FieldEditor;
import org.eclipse.imp.preferences.fields.IntegerFieldEditor;
import org.eclipse.imp.preferences.fields.RadioGroupFieldEditor;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.osgi.service.prefs.Preferences;


public class X10ProjectPreferencesTab extends ProjectPreferencesTab {

	/*
	 * TODO:  Declare fields for field editors here.
	 * In contrast to the other tabs, with the Project tab the
	 * field editors have to be shared between two methods, so
	 * they are be declared outside of both.
	 */
	ComboFieldEditor compilerConfiguration = null;
	IntegerFieldEditor samplingFrequency = null;
	RadioGroupFieldEditor statsDisable = null;
	BooleanFieldEditor emitMessages = null;

	
	
public X10ProjectPreferencesTab(IPreferencesService prefService) {
		super(prefService);
	}

	
	
	/**
	 * Creates specific preference fields with settings appropriate to
	 * the Project preferences level.
	 * 
	 * Overrides an unimplemented method in SafariPreferencesTab.
	 * 
	 * @return	An array that contains the created preference fields
	 */
	protected FieldEditor[] createFields(Composite composite) {
		
		// TODO:  Construct the specific fields, including a "details" link
		// for each field; also create "toggle" listeners between fields whose
		// editability is linked.  Add spaces, boxes, etc. as apprpriate.
		//
		// PreferencesUtilities has factory-like methods for creating
		// fields and links of specific types.
		//
		// Among the various parameters that can be set for a Safari preferences
		// field, fields below the default level should generally be removable.
		// 
		// A special consideration on the project level is that preference fields
		// in general should not be enabled (or enableable) when no project is
		// selected.  If the this tab is implemented such that no project will be
		// selected when the tab is first created, then the individual fields in
		// the tab should be created with their enabled state (and editable state,
		// if applicable) set to false.


		/*
		 * Parameters for call to makeNewComboField(..):
		 *  PreferencePage page, SafariPreferencesTab tab,
		 *  IPreferencesService service, String level,
		 *  String name, String labelText, String[][] entryNamesAndValues, Composite parent,
		 * 	boolean isEnabled, boolean hasSpecialValue, String specialValue, boolean isRemovable
		*/
		compilerConfiguration = prefUtils.makeNewComboField(
				prefPage, this, prefService, IPreferencesService.PROJECT_LEVEL,
				PreferenceConstants.P_X10CONFIG_NAME, "Compiler configuration:",
				new String[][] { { "Standard", "standard" } }, composite,
				false, true, PreferencesUtilities.comboDefaultName, true);
		Link compilerConfigurationDetails = prefUtils.createDetailsLink(
				composite, compilerConfiguration, compilerConfiguration.getComboBoxControl(composite).getParent(), "Details ...");
		detailsLinks.add(compilerConfigurationDetails);
		
		PreferencesUtilities.fillGridPlace(composite, 2);	
	
		
		/*
		 * Parameters to call to makeNewIntegerField (similar to those for other subtypes of string field): 
		 *  PreferencePage page, SafariPreferencesTab tab,
		 *  IPreferencesService service, String level, String key,
		 *  String text, Composite parent,
		 *  boolean isEnabled, boolean isEditable, boolean hasSpecialValue, String specialValue,
		 *  boolean emptyValueAllowed, String emptyValue,boolean isRemovable
		 */
		samplingFrequency = prefUtils.makeNewIntegerField(
				prefPage, this, prefService, IPreferencesService.PROJECT_LEVEL,
				PreferenceConstants.P_SAMPLING_FREQ, "Sampling frequency:",
				composite, false, false, true, "50", false, "", true);
		// Special implementation fields for this page field:
		samplingFrequency.setValidRange(0, 99);
		Link samplingFrequencyDetails = prefUtils.createDetailsLink(
				composite, samplingFrequency, samplingFrequency.getTextControl().getParent(), "Details ...");
		detailsLinks.add(samplingFrequencyDetails);
		
		PreferencesUtilities.fillGridPlace(composite, 2);			
		
		
		// The next field to be added is a radio group field.  Adding a radio
		// group field causes the number of columns in the layout to be set to
		// one.  That's probably inappropriate, so record the prevailing number
		// of columns before setting the field so as to be able to restore the
		// number of columns after setting the field
		int originalNumColumns = ((GridLayout) composite.getLayout()).numColumns;
		
		/*
		 * Parameters for call to 	makeNewRadioGroupField(..):
		 *  PreferencePage page, SafariPreferencesTab tab,
		 *  IPreferencesService service, String level,
		 *  String name, String labelText, int numColumns,
		 *  String[][] labelAndValues, Composite parent,
		 *  boolean useGroup, boolean isEnabled, boolean isRemovable
		 */
		statsDisable = prefUtils.makeNewRadioGroupField(
				prefPage, this, prefService, IPreferencesService.PROJECT_LEVEL,
				PreferenceConstants.P_STATS_DISABLE, "Statistics Disable:	", 2,
				new String[][] { { "&None", "none" }, { "&All", "all" } }, composite,
				true, false, true);
		Link statsDisableDetails = prefUtils.createDetailsLink(
				composite, statsDisable, statsDisable.getRadioBoxControl(composite), "Details ...");
		detailsLinks.add(statsDisableDetails);
		
		// Restore the number of columns
		GridLayout layoutForRestoration = new GridLayout();
		layoutForRestoration.numColumns = originalNumColumns;
		composite.setLayout(layoutForRestoration);
			

		PreferencesUtilities.fillGridPlace(composite, 2);	
		
   		/*
   		 * Parameters for makeNewBooleanField(..): 
   		 *  PreferencePage page, SafariPreferencesTab tab,
   		 *  IPreferencesService service, String level,
   		 *  String key, String text, Composite parent, 
   		 *  boolean isEnabled, boolean isEditable, boolean hasSpecialValue, boolean specialValue,
   		 *  boolean emptyValueAllowed, boolean emptyValue, boolean isRemovable
   		 */
		emitMessages = prefUtils.makeNewBooleanField(
				prefPage, this, prefService, IPreferencesService.PROJECT_LEVEL,
				PreferenceConstants.P_EMIT_MESSAGES, "Emit diagnostic messages from the builder",
				composite, false, false, true, false, false, false, true);
		Link emitMessagesDetails = prefUtils.createDetailsLink(
				composite, emitMessages, emitMessages.getChangeControl().getParent(), "Details ...");
		detailsLinks.add(emitMessagesDetails);	

		
		// The makeNew***Field methods should check for the special case
		// represented by this tab (project level with no project selected)
		// and disable the new fields accordingly
		
		PreferencesUtilities.fillGridPlace(composite, 2);
		
		// TODO:  Put the created fields into an array and return it
		FieldEditor fields[] = new FieldEditor[4];		// change length as appropriate
		// 	Add fields here ...
		fields[0] = compilerConfiguration;
		fields[1] = samplingFrequency;
		fields[2] = statsDisable;
		fields[3] = emitMessages;
		
		return fields;
	}
	
	
	
	/**
	 * Respond to the selection of a project in the project-preferences tab.
	 * 
	 * Overrides an unimplemented method in ProjectPreferencesTab.
	 */
	public void addressProjectSelection(IPreferencesService.ProjectSelectionEvent event, Composite composite)
	{
		// Check that at least one affected preference is non-null
		Preferences oldeNode = event.getPrevious();
		Preferences newNode = event.getNew();
		if (oldeNode == null && newNode == null) {
			// This is what happens for some reason when you clear the project selection,
			// but there shouldn't be anything to do (I don't think), because newNode == null
			// implies that the preferences should be cleared, disabled, etc., and oldeNode
			// ==  null implies that they should already be cleared, disabled, etc.
			// So, it should be okay to return from here ...
			return;
		}

		
		// If oldeNode is not null, we want to remove any preference-change listeners from it
		// (they're no longer needed, and the new project preferences node will get new listeners)
		boolean haveCurrentListeners = false;
		if (oldeNode != null && oldeNode instanceof IEclipsePreferences && haveCurrentListeners) {
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
		} else {	
			//System.out.println("JsdivProjectPreferencesPage.SafariProjectSelectionListener.selection():  " +
			//	"\n\tnode is null, not of IEclipsePreferences type, or currentListener is null; not possible to add preference-change listener");
		}

		// TODO:  For each preference field, declare a composite to hold that field
		// (that is, the parent of the field).  These are not strictly necessary, because
		// the parent can always be obtained from the field, but for repeated accesses of
		// the parents these local variables are convenient and efficient.
		// ...
		Composite emitMessagesHolder	= null;
		Composite samplingFrequencyHolder = null;
		Composite compilerConfigurationHolder = null;
		Composite statsDisableHolder = null;

		
		// If we have a new project preferences node, then set up the project's preferences
		if (newNode != null && newNode instanceof IEclipsePreferences) {
			// Set project name in the selected-project field
			selectedProjectName.setStringValue(newNode.name());
			
			// If the containing composite is not disposed, then set the field
			// values and make them enabled and editable	

			// Not entirely sure why the composite could or should be disposed if we're here,
			// but it happens sometimes when selecting a project for the second time, i.e.,
			// after having selected a project once, clicked "OK", and then brought up the
			// dialog and selected a project again.  PERHAPS there is a race condition, such
			// that sometimes the project-selection dialog is still overlaying the preferences
			// tab at the time that the listeners try to update the tab.  If the project-selection
			// dialog were still up then the preferences tab would be disposed.
			
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
				
				// Used in setting enabled and editable status
				boolean value = false;
				
				// TODO:  For each field
				// 1) assign the parent to a local variable for handy reference (if you're using
				//    these)
				// 2) Call PreferencesUtilities.setField(..) with the field (and it's parent)
				//    to set a value in the field.  (setField(..) will obtain a value from the
				//    preferences store, if there is one set there, or inherit one from a higher
				//    preferences level, if not).
				// 3) Enable the fields as appropriate.  Most fields will be enabled.  If the enabled
				//    state of one field depends on the value of another, then set the first accordingly.
				//    Note also that field may contain multiple controls that may need to be set
				//    individually.  Also, String fields have an editable state that can be set along
				//    with their enabled state.
				
				emitMessagesHolder = emitMessages.getChangeControl().getParent();
				prefUtils.setField(emitMessages, emitMessagesHolder);
				emitMessages.getChangeControl().setEnabled(true);

				samplingFrequencyHolder = samplingFrequency.getTextControl().getParent();
				prefUtils.setField(samplingFrequency, samplingFrequencyHolder);
				samplingFrequency.getTextControl(samplingFrequencyHolder).setEditable(true);
				samplingFrequency.getTextControl(samplingFrequencyHolder).setEnabled(true);
				samplingFrequency.setEnabled(true, samplingFrequency.getParent());

				compilerConfigurationHolder = compilerConfiguration.getComboBoxControl(compilerConfigurationHolder).getParent();
				prefUtils.setField(compilerConfiguration, compilerConfigurationHolder);
				compilerConfiguration.setEnabled(true, compilerConfiguration.getParent());
				compilerConfiguration.getComboBoxControl(compilerConfigurationHolder).setEnabled(true);

				// This is just the way it is ...
				// (I had to figure this out in the debugger; it may make sense if you
				// analyze the layout of the preference page properly)
				statsDisableHolder = composite.getParent().getParent();
				prefUtils.setField(statsDisable, statsDisableHolder);
				statsDisable.getRadioBoxControl(statsDisableHolder).setEnabled(true);
				Button[] buttons = statsDisable.getRadioButtons();
				for (int i = 0; i < buttons.length; i++) {
						buttons[i].setEnabled(true);
				}
				
				// Since the fields are been freshly initialized,
				// remove indications of prior modifications
				clearModifiedMarksOnLabels();
				
			}
			

			// TODO:  Add a property change listener for each field, so that if the
			// preference is changed in the model the field can be updated (not all 
			// updates to preference values originate through the preference field
			// editor).  Listeners can be added using addProjectPreferenceChangeListners(..)
			// in ProjectPreferencesTab.
			if (emitMessagesHolder != null) addProjectPreferenceChangeListeners(
				emitMessages, "String constant for name of boolean preference", emitMessagesHolder);
			if (samplingFrequencyHolder != null) addProjectPreferenceChangeListeners(
				samplingFrequency, "String constant for name of filename preference", samplingFrequencyHolder);
			if (compilerConfigurationHolder != null) addProjectPreferenceChangeListeners(
				compilerConfiguration, "String constant for name of directory-list preference", compilerConfigurationHolder);
			if (statsDisableHolder != null) addProjectPreferenceChangeListeners(
				statsDisable, "String constant for name of string preference", statsDisableHolder);

			
			
			// Indicate that there are current listeners
			haveCurrentListeners = true;
		}
		
		// Or if we don't have a new project preferences node ...
		if (newNode == null || !(newNode instanceof IEclipsePreferences)) {
			// For example, when the tab is first brought up, or if the project
			// has been deselected	
			
			// Clear the project name in the selected-project field
			selectedProjectName.setStringValue("none selected");
			
			// Clear the preferences from the store
			prefService.clearPreferencesAtLevel(IPreferencesService.PROJECT_LEVEL);
			
			// Disable fields and make them non-editable
			if (!composite.isDisposed()) {
				// TODO:  set enabled (and editable, if applicable) to false
				// for all fields
				emitMessages.getChangeControl().setEnabled(false);

				samplingFrequency.getTextControl(samplingFrequencyHolder).setEnabled(false);
				samplingFrequency.getTextControl(samplingFrequencyHolder).setEditable(false);

				compilerConfiguration.getComboBoxControl(compilerConfigurationHolder).setEnabled(false);
				//	compilerConfiguration.getTextControl(compilerConfigurationHolder).setEditablefalse);

				statsDisable.getRadioBoxControl(statsDisableHolder).setEnabled(false);
			}
			
			// Remove listeners
			removeProjectPreferenceChangeListeners();
			haveCurrentListeners = false;
		}
	}
	
}
