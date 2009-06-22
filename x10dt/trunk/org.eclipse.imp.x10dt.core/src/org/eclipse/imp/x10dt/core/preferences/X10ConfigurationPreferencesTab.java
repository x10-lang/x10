package org.eclipse.imp.x10dt.core.preferences;

import org.eclipse.imp.preferences.ConfigurationPreferencesTab;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesUtilities;
import org.eclipse.imp.preferences.fields.BooleanFieldEditor;
import org.eclipse.imp.preferences.fields.ComboFieldEditor;
import org.eclipse.imp.preferences.fields.FieldEditor;
import org.eclipse.imp.preferences.fields.IntegerFieldEditor;
import org.eclipse.imp.preferences.fields.RadioGroupFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Link;

public class X10ConfigurationPreferencesTab extends ConfigurationPreferencesTab {
	
	
	public X10ConfigurationPreferencesTab(IPreferencesService prefService) {
		super(prefService);
	}
	
	
	/**
	 * Creates specific preference fields with settings appropriate to
	 * the Workspace Configuration preferences level.
	 * 
	 * Overrides an unimplemented method in SafariPreferencesTab.
	 * 
	 * @return	An array that contains the created preference fields
	 */
	protected FieldEditor[] createFields(Composite composite) {
		
		// TODO:  Declare preference fields here ...
		ComboFieldEditor compilerConfiguration = null;
		IntegerFieldEditor samplingFrequency = null;
		BooleanFieldEditor emitMessages = null;
		RadioGroupFieldEditor statsDisable = null;
		
		// TODO:  Construct the specific fields, including a "details" link
		// for each field; also create "toggle" listeners between fields whose
		// editability is linked.  Add spaces, boxes, etc. as apprpriate.
		//
		// SafariPreferencesUtilities has factory-like methods for creating
		// fields and links of specific types.
		//
		// Among the various parameters that can be set for a Safari preferences
		// field, fields below the default level should generally be removable.
		
		/*
		 * FYI:  The parameters to makeNew*Field following the "composite" parameter"
		 *	boolean isEnabled, boolean isEditable,
		 *	boolean hasSpecialValue, String specialValue,
		 *	boolean emptyValueAllowed, String emptyValue,
		 *	boolean isRemovable
		 * Except that ComboField and RadioGroupField are different (some of those
		 * concepts not applying to those field types)
		 */

		compilerConfiguration = prefUtils.makeNewComboField(
				prefPage, this, prefService, IPreferencesService.CONFIGURATION_LEVEL,
				PreferenceConstants.P_X10CONFIG_NAME, "Compiler configuration:",
				new String[][] { { "Standard", "standard" } }, composite,
				true, true, PreferencesUtilities.comboDefaultName, true);
		Link compilerConfigurationDetails = prefUtils.createDetailsLink(
				composite, compilerConfiguration, compilerConfiguration.getComboBoxControl(composite).getParent(), "Details ...");
		
		PreferencesUtilities.fillGridPlace(composite, 2);	
	
		samplingFrequency = prefUtils.makeNewIntegerField(
				prefPage, this, prefService, IPreferencesService.CONFIGURATION_LEVEL,
				PreferenceConstants.P_SAMPLING_FREQ, "Sampling frequency:",
				composite, true, true, true, "50", false, "", true);
		// Special implementation fields for this page field:
		samplingFrequency.setValidRange(0, 99);
		Link samplingFrequencyDetails = prefUtils.createDetailsLink(
				composite, samplingFrequency, samplingFrequency.getTextControl().getParent(), "Details ...");

		PreferencesUtilities.fillGridPlace(composite, 2);			
		
		// SMS 12 Dec 2006
		// get grid layout for composite in order to restore it after
		// adding RadioGroupField (which seems to cause the number of
		// columns to be set to one)
		Layout layout = composite.getLayout();
		statsDisable = prefUtils.makeNewRadioGroupField(
				prefPage, this, prefService, IPreferencesService.CONFIGURATION_LEVEL,
				PreferenceConstants.P_STATS_DISABLE, "Statistics Disable:", 2,
				new String[][] { { "&None", "none" }, { "&All", "all" } }, composite, true,
				true, true);
		Link statsDisableDetails = prefUtils.createDetailsLink(
				composite, statsDisable, statsDisable.getRadioBoxControl(composite), "Details ...");
		// Setting a RadioGroupField into a grid (or any container) seems to cause
		// the number of columns to be reset to one, so the layout of the containing
		// is reset here to its original value.
		composite.setLayout(layout);

		PreferencesUtilities.fillGridPlace(composite, 2);	
		
		// Boolean preference emitMessages
		emitMessages = prefUtils.makeNewBooleanField(
				prefPage, this, prefService, IPreferencesService.CONFIGURATION_LEVEL,
				PreferenceConstants.P_EMIT_MESSAGES, "Emit diagnostic messages from the builder",
				composite, true, true, true, false, false, false, true);
		Link emitMessagesDetails = prefUtils.createDetailsLink(
				composite, emitMessages, emitMessages.getChangeControl().getParent(), "Details ...");

		
		
		// Example of more spacing
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
	
	
	
	
}
