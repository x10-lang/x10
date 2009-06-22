/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*    Matthew Kaplan (mkaplan@us.ibm.com) - added support for Font editors and property listeners
*******************************************************************************/

package org.eclipse.imp.x10dt.core.preferences;


import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesTab;
import org.eclipse.imp.preferences.fields.ComboFieldEditor;
import org.eclipse.imp.preferences.fields.RadioGroupFieldEditor;
import org.eclipse.imp.x10dt.core.preferences.fields.BooleanFieldEditor;
import org.eclipse.imp.x10dt.core.preferences.fields.FieldEditor;
import org.eclipse.imp.x10dt.core.preferences.fields.FontFieldEditor;
import org.eclipse.imp.x10dt.core.preferences.fields.IntegerFieldEditor;
import org.eclipse.imp.x10dt.core.preferences.fields.StringFieldEditor;
import org.eclipse.imp.x10dt.core.preferences.fields.CompilerOptionsStringFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;


public class PreferencesUtilities extends org.eclipse.imp.preferences.PreferencesUtilities {

//	public static final Color colorRed = new Color(null, 200, 0, 0);

	public PreferencesUtilities(IPreferencesService service)
	{
		super(service);
	}
	
	
	public String setField(BooleanFieldEditor field, Composite parent)
	{
		// TODO:  Add checks on input validity (see below)
		// Note:  so far assumes that the given level is the one to which the
		// field belongs (which should be true but isn't guaranteed (?))

		String level = field.getPreferencesLevel();
		
		// If the level is "project" and project == null then just set
		// the field here (as a special case).
		// Note:  without some project selected the field should not be
		// editable.  Field will have to be set back to editable when
		// (and probably where) a project is selected.  We might take
		// care of this elsewhere but, until that is verified, keep
		// doing it here.
		// Note also:  loadWithInheritance (which calls setField(..))
		// won't know that project == null and will try to set the field
		// from some higher level
		if (IPreferencesService.PROJECT_LEVEL.equals(level) &&
			service.getProject() == null)
		{
			if (parent == null) {
				System.err.println("PreferencesUtilities.setField():  parent is null");
			}	
			if (parent.isDisposed()) {
				System.err.println("PreferencesUtilities.setField():  parent is disposed");
			}	
			// Don't have a null boolean value to set field to,
			// but would like to show it as "cleared" somehow
			// (presumably "false" shows as empty
			field.setFieldValueFromOutside(false);
			if (!parent.isDisposed()) {
				field.getChangeControl().setEnabled(false);
				field.getChangeControl().setBackground(colorBluish);
			}
			// Pretend that this was set at the project level?
			// (It was certainly cleared at that level)
			return IPreferencesService.PROJECT_LEVEL;
		}
		
		// Otherwise, we have a legitimate level, so set normally
		String levelFromWhichSet = field.loadWithInheritance();
		
		// Note:  You can evidently load a field even when it's control
		// is disposed.  In that case (evidently) you can change the
		// text in the field but not the background color.	
		
		if (parent != null && !parent.isDisposed()) {
			if (level != null && level.equals(levelFromWhichSet)) {
				field.getChangeControl().setBackground(colorWhite);
			} else if (level != null && field.getChangeControl().getEnabled()) {
				field.getChangeControl().setBackground(colorBluish);
			}
		} else {
			// If composite.isDisposed(), then both field.getTextControl(composite)
			// and field.getTextControl() will return null; if needed, a text control
			// must be obtained from somewhere else--but I have no idea where that
			// might be.  Not sure why composite.isDisposed() here in the first place,
			// especially considering that the field can be set
		}

		return levelFromWhichSet;
	}
	
	
	public String setField(BooleanFieldEditor field, Composite composite, boolean value)
	{
		final String whoiam = "PreferenesUtilities.setField(BooleanFieldEditor field, composite, value):  ";
		
		if (field == null)
			throw new IllegalArgumentException(whoiam + "given field is null");
		if (composite == null)
			throw new IllegalArgumentException(whoiam + "given composite is null");
		
		if (composite.isDisposed())
			throw new IllegalStateException(whoiam + "composite is disposed");
		if (!field.getChangeControl().getEnabled())
			throw new IllegalStateException(whoiam + "field is not editable");
		if (IPreferencesService.PROJECT_LEVEL.equals(field.getPreferencesLevel()) &&
				service.getProject() == null)
			throw new IllegalStateException(whoiam + "field represents a project-level preference and project is not set");
		
		String level = field.getPreferencesLevel();
				
		field.setFieldValueFromOutside(value);
		field.getChangeControl().setBackground(colorWhite);

		return level;
	}


	
	public String setField(ComboFieldEditor field, Composite composite)
	{
		// TODO:  Add checks on input validity (see below)
		// Note:  so far assumes that the given level is the one to which the
		// field belongs (which should be true but isn't guaranteed (?))

		String level = field.getPreferencesLevel();
		
		// If the level is "project" and project == null then just set
		// the field here (as a special case).
		if (IPreferencesService.PROJECT_LEVEL.equals(level) &&
			service.getProject() == null)
		{
			if (composite.isDisposed()) {
				System.err.println("PreferencesUtilities.setField():  composite is disposed");
			}
			// Updating a radio field with a null value should
			// cause the first button to be selected
			field.setFieldValueFromOutside(null);
			if (!composite.isDisposed()) {
				// Assume that editability on the project level is set
				// appropriately by a project-selection listener
				//field.getTextControl(composite).setEditable(false);
				field.getComboBoxControl().setBackground(colorWhite);
				Control[] children = field.getComboBoxControl().getChildren();
		    	if (children != null) {
		    		for (int i = 0; i < children.length; i++) {
		    			Button button = (Button) children[i];
		    			button.setBackground(PreferencesUtilities.colorWhite);	
		    		}
		    	}
			}
			// Pretend that this was set at the project level?
			// (It was certainly cleared at that level)
			return IPreferencesService.PROJECT_LEVEL;
		}
		
		// Otherwise, we have a legitimate level, so set normally
		String levelFromWhichSet = field.loadWithInheritance();
		
		// Note:  You can evidently load a field even when it's control
		// is disposed.  In that case you can (evidently) change the
		// text in the field but not the background color.	
		
		if (!composite.isDisposed()) {
			// Needed if color is set in loadWithInheritance()?
			// (Should the color be set in loadWithInheritance()?
//			if (field.isInherited()) {
//				field.getComboBoxControl(composite).setBackground(colorBluish);
//			} else 	{
//				field.getComboBoxControl(composite).setBackground(colorWhite);
//			}
		} else {
			// If composite.isDisposed(), then both field.getTextControl(composite)
			// and field.getTextControl() will return null; if needed, a text control
			// must be obtained from somewhere else--but I have no idea where that
			// might be.  Not sure why composite.isDisposed() here in the first place,
			// especially considering that the field can be set
		}
		
		return levelFromWhichSet;
	}
	
	
	public String setField(ComboFieldEditor field, Composite composite, String value)
	{
		final String whoiam = "PreferenesUtilities.setField(ComboFieldEditor field, composite, value):  ";
		
		if (field == null)
			throw new IllegalArgumentException(whoiam + "given field is null");
		if (composite == null)
			throw new IllegalArgumentException(whoiam + "given composite is null");
		if (value == null)
			throw new IllegalArgumentException(whoiam + "given value is null");
		
		if (composite.isDisposed())
			throw new IllegalStateException(whoiam + "composite is disposed");
		if (IPreferencesService.PROJECT_LEVEL.equals(field.getPreferencesLevel()) &&
				service.getProject() == null)
			throw new IllegalStateException(whoiam + "field represents a project-level preference and project is not set");
		
		String level = field.getPreferencesLevel();
				
		field.setFieldValueFromOutside(value);
		// setString(value) takes care of setting isInherited
		// and presentsDefaultValue, but not ...
		field.getComboBoxControl().setBackground(colorWhite);

		return level;
	}


	
	public String setField(RadioGroupFieldEditor field, Composite composite)
	{
		// TODO:  Add checks on input validity (see below)
		// Note:  so far assumes that the given level is the one to which the
		// field belongs (which should be true but isn't guaranteed (?))

		String level = field.getPreferencesLevel();
		
		// If the level is "project" and project == null then just set
		// the field here (as a special case).
		if (IPreferencesService.PROJECT_LEVEL.equals(level) &&
			service.getProject() == null)
		{
			if (composite.isDisposed()) {
				System.err.println("PreferencesUtilities.setField():  composite is disposed");
			}
			// Updating a radio field with a null value should
			// cause the first button to be selected
			field.setFieldValueFromOutside(null);
			if (!composite.isDisposed()) {
				// Assume that editability on the project level is set
				// appropriately by a project-selection listener
				//field.getTextControl(composite).setEditable(false);
				field.getRadioBoxControl().setBackground(colorBluish);
				Button[] radioButtons = field.getRadioButtons();
		    	if (radioButtons != null) {
		    		for (int i = 0; i < radioButtons.length; i++) {
		    			Button button = (Button) radioButtons[i];
		    			button.setBackground(colorBluish);	
		    		}
		    	}
			}
			// Pretend that this was set at the project level?
			// (It was certainly cleared at that level)
			return IPreferencesService.PROJECT_LEVEL;
		}
		
		// Otherwise, we have a legitimate level, so set normally
		String levelFromWhichSet = field.loadWithInheritance();
		
		// Note:  You can evidently load a field even when it's control
		// is disposed.  In that case (evidently) you can change the
		// text in the field but not the background color.	
		
		if (!composite.isDisposed()) {
			// Needed if color is set in loadWithInheritance()?
			// (Should the color be set in loadWithInheritance()?
//			if (field.isInherited()) {
//				field.getRadioBoxControl(composite).setBackground(colorBluish);
//			} else 	{
//				field.getRadioBoxControl(composite).setBackground(colorWhite);
//			}
		} else {
			// If composite.isDisposed(), then both field.getTextControl(composite)
			// and field.getTextControl() will return null; if needed, a text control
			// must be obtained from somewhere else--but I have no idea where that
			// might be.  Not sure why composite.isDisposed() here in the first place,
			// especially considering that the field can be set
		}

		return levelFromWhichSet;
	}
	
	
	public String setField(RadioGroupFieldEditor field, Composite composite, String value)
	{
		final String whoiam = "PreferenesUtilities.setField(RadioGroupFieldEditor field, composite, value):  ";
		
		if (field == null)
			throw new IllegalArgumentException(whoiam + "given field is null");
		if (composite == null)
			throw new IllegalArgumentException(whoiam + "given composite is null");
		if (value == null)
			throw new IllegalArgumentException(whoiam + "given value is null");
		
		if (composite.isDisposed())
			throw new IllegalStateException(whoiam + "composite is disposed");
		if (IPreferencesService.PROJECT_LEVEL.equals(field.getPreferencesLevel()) &&
				service.getProject() == null)
			throw new IllegalStateException(whoiam + "field represents a project-level preference and project is not set");
		
		String level = field.getPreferencesLevel();
				
		field.setFieldValueFromOutside(value);
		// setString(value) takes care of setting isInherited
		// and presentsDefaultValue, but not coloring
		field.getRadioBoxControl().setBackground(colorWhite);
        Button[] radioButtons = field.getRadioButtons();
    	if (radioButtons != null) {
    		for (int i = 0; i < radioButtons.length; i++) {
    			((Button) radioButtons[i]).setBackground(colorWhite);	
    		}
    	}

		return level;
	}

	
	
	
	public String setField(StringFieldEditor field, Composite composite)
	{
		// TODO:  Add checks on input validity (see below)
		// Note:  so far assumes that the given level is the one to which the
		// field belongs (which should be true but isn't guaranteed (?))

		String level = field.getPreferencesLevel();
		
		// If the level is "project" and project == null then just set
		// the field here (as a special case).
		if (IPreferencesService.PROJECT_LEVEL.equals(level) &&
			service.getProject() == null)
		{
			if (composite.isDisposed()) {
				System.err.println("PreferencesUtilities.setField():  composite is disposed");
			}
			field.setFieldValueFromOutside(null);
			if (!composite.isDisposed()) {
				// Assume that editability on the project level is set
				// appropriately by a project-selection listener
				//field.getTextControl(composite).setEditable(false);
				field.getTextControl(composite).setBackground(colorBluish);
			}
			// Pretend that this was set at the project level?
			// (It was certainly cleared at that level)
			return IPreferencesService.PROJECT_LEVEL;
		}
		
		// Otherwise, we have a legitimate level, so set normally
		String levelFromWhichSet = field.loadWithInheritance();
		
		// Note:  You can evidently load a field even when it's control
		// is disposed.  In that case (evidently) you can change the
		// text in the field but not the background color.	
		
		if (!composite.isDisposed()) {
			/*
			if (level != null && level.equals(levelFromWhichSet)) {
				field.getTextControl(composite).setBackground(colorWhite);
			} else if (level != null && field.getTextControl(composite).getEditable()) {
				field.getTextControl(composite).setBackground(colorBluish);
			}
			*/
			if (field.isInherited()) {
				field.getTextControl(composite).setBackground(colorBluish);
			} else 	{
				field.getTextControl(composite).setBackground(colorWhite);
			}
		} else {
			// If composite.isDisposed(), then both field.getTextControl(composite)
			// and field.getTextControl() will return null; if needed, a text control
			// must be obtained from somewhere else--but I have no idea where that
			// might be.  Not sure why composite.isDisposed() here in the first place,
			// especially considering that the field can be set
		}

		return levelFromWhichSet;
	}
	
	

	public String setField(StringFieldEditor field, Composite composite, String value)
	{
		final String whoiam = "PreferenesUtilities.setField(StringFieldEditor field, composite, value):  ";
		
		if (field == null)
			throw new IllegalArgumentException(whoiam + "given field is null");
		if (composite == null)
			throw new IllegalArgumentException(whoiam + "given composite is null");
		if (value == null)
			throw new IllegalArgumentException(whoiam + "given value is null");
		
		if (composite.isDisposed())
			throw new IllegalStateException(whoiam + "composite is disposed");
		if (!field.getTextControl(composite).getEditable())
			throw new IllegalStateException(whoiam + "field is not editable");
		if (value.equals("") && !field.isEmptyValueAllowed())
			throw new IllegalArgumentException(whoiam + "value is empty and field does not allow empty values");
		if (IPreferencesService.PROJECT_LEVEL.equals(field.getPreferencesLevel()) &&
				service.getProject() == null)
			throw new IllegalStateException(whoiam + "field represents a project-level preference and project is not set");
		
		String level = field.getPreferencesLevel();
				
		field.setFieldValueFromOutside(value);
		// setString(value) takes care of setting isInherited
		// and presentsDefaultValue, but not ...
		field.getTextControl(composite).setBackground(colorWhite);

		return level;
	}
	
	
	public String setField(FontFieldEditor field, Composite composite)
	{
		// mmk - copied and tweeked from setField(ComboFieldEditor, Composite)
		
		// TODO:  Add checks on input validity (see below)
		// Note:  so far assumes that the given level is the one to which the
		// field belongs (which should be true but isn't guaranteed (?))

		String level = field.getPreferencesLevel();
		
		// If the level is "project" and project == null then just set
		// the field here (as a special case).
		if (IPreferencesService.PROJECT_LEVEL.equals(level) &&
			service.getProject() == null)
		{
			if (composite.isDisposed()) {
				System.err.println("PreferencesUtilities.setField():  composite is disposed");
			}
			// Updating a radio field with a null value should
			// cause the first button to be selected
			field.setFieldValueFromOutside(null);
			if (!composite.isDisposed()) {
				// Assume that editability on the project level is set
				// appropriately by a project-selection listener
				//field.getTextControl(composite).setEditable(false);
				// mmk-- colors don't apply to font control (except possibly to select-font button? but that would be grayed)
//				field.getFontControl(composite).setBackground(colorWhite);
//				Control[] children = field.getFontControl(composite).getChildren();
//		    	if (children != null) {
//		    		for (int i = 0; i < children.length; i++) {
//		    			Button button = (Button) children[i];
//		    			button.setBackground(PreferencesUtilities.colorWhite);	
//		    		}
//		    	}
			}
			// Pretend that this was set at the project level?
			// (It was certainly cleared at that level)
			return IPreferencesService.PROJECT_LEVEL;
		}
		
		// Otherwise, we have a legitimate level, so set normally
		String levelFromWhichSet = field.loadWithInheritance();
		
		// Note:  You can evidently load a field even when it's control
		// is disposed.  In that case you can (evidently) change the
		// text in the field but not the background color.	
		
		if (!composite.isDisposed()) {
			// Needed if color is set in loadWithInheritance()?
			// (Should the color be set in loadWithInheritance()?
//			if (field.isInherited()) {
//				field.getComboBoxControl(composite).setBackground(colorBluish);
//			} else 	{
//				field.getComboBoxControl(composite).setBackground(colorWhite);
//			}
		} else {
			// If composite.isDisposed(), then both field.getTextControl(composite)
			// and field.getTextControl() will return null; if needed, a text control
			// must be obtained from somewhere else--but I have no idea where that
			// might be.  Not sure why composite.isDisposed() here in the first place,
			// especially considering that the field can be set
		}
		
		return levelFromWhichSet;
	}
	

 	public BooleanFieldEditor makeNewBooleanField(
	   		PreferencePage page, PreferencesTab tab,
			IPreferencesService service,
			String level, String key, String text,
			Composite parent,
			boolean isEnabled, boolean isEditable,	
			boolean hasSpecialValue, boolean specialValue,
			boolean emptyValueAllowed, boolean emptyValue,
			boolean isRemovable, Object x)
	{
		//System.err.println("SPU.makeNewBooleanField() starting for key = " + key);
		Composite fieldHolder = new Composite(parent, SWT.NONE); 
		fieldHolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		BooleanFieldEditor field =
			new BooleanFieldEditor(page, tab, service, level, key, text, fieldHolder);
		if (level != null && level.equals(IPreferencesService.PROJECT_LEVEL) && service.getProject() != null) {
			setField(field, fieldHolder);
			addBooleanPropertyChangeListeners(service, level, field, key, fieldHolder);
		} else {
			setField(field, fieldHolder);
			addBooleanPropertyChangeListeners(service, level, field, key, fieldHolder);
		}
		field.getChangeControl().setEnabled(isEnabled);
		// boolean controls have no setEditable() method
		// SMS 19 Jun 2007
		if (!hasSpecialValue)
			field.setNoSpecialValue();
		else
			field.setSpecialValue(specialValue);
		// also cannot be empty
		//field.setEmptyValueAllowed(false);
		
		if (level == null) field.setRemovable(false);
		else if (level.equals(IPreferencesService.DEFAULT_LEVEL)) field.setRemovable(false);
		else field.setRemovable(isRemovable);

		initializeField(field, page);
		//System.err.println("SPU.makeNewBooleanField() ending for key = " + key);
		return field;
	}


	// mmk -- based on makeNewComboBoxField
	public FontFieldEditor makeNewFontField(
	   		PreferencePage page, PreferencesTab tab,
	   		IPreferencesService service, String level,
	   		String name, String labelText, String[][] entryNamesAndValues, Composite parent,
	   		boolean isEnabled, boolean hasSpecialValue, String specialValue, boolean isRemovable, Object x)
	{	
		Composite fieldHolder = new Composite(parent, SWT.NONE);
		fieldHolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	    boolean onProjectLevelWithNullProject =
	    	level != null && level.equals(IPreferencesService.PROJECT_LEVEL) && service.getProject() == null;
	    //boolean notOnARealLevel = level == null;
	    //boolean onAFunctioningLevel = !onProjectLevelWithNullProject && !notOnARealLevel;
	    
	    FontFieldEditor field = new FontFieldEditor(
	    		page, tab, service, level,
	    		name, labelText, entryNamesAndValues, fieldHolder,
	    		isEnabled, hasSpecialValue, specialValue, isRemovable);
	    
		if (!onProjectLevelWithNullProject) {
			setField(field, fieldHolder);
			addFontPreferenceChangeListeners(service, level, field, name, fieldHolder);
		} else {
			//setField(field, parent);
			//addStringPropertyChangeListeners(service, level, field, key, fieldHolder);
		}
		
		field.setEnabled(isEnabled, fieldHolder);	

		if (level == null) field.setRemovable(false);	// can never remove from a field that doesn't have a stored value
		else if (level.equals(IPreferencesService.DEFAULT_LEVEL)) field.setRemovable(false);	// can never remove from Default level
		else field.setRemovable(isRemovable);

		field.setPage(page);
		initializeField(field, page);
		field.load();
		return field;
	}
	
	private void initializeField(FieldEditor pe, PreferencePage page) {
        if (page instanceof IPropertyChangeListener) {
        	pe.setPropertyChangeListener((IPropertyChangeListener)page);
        }
        pe.setPreferenceStore(page.getPreferenceStore());

	}
	
	
	public IntegerFieldEditor makeNewIntegerField(
			PreferencePage page,
			PreferencesTab tab,
			IPreferencesService service,
			String level, String key, String text,
			Composite parent,
			boolean isEnabled, boolean isEditable,
			boolean hasSpecialValue, String specialValue,
			boolean emptyValueAllowed, String emptyValue,
			boolean isRemovable, Object x)
	{
		//System.err.println("SPU.makeNewIntegerField() starting for key = " + key);
		Composite fieldHolder = new Composite(parent, SWT.NONE);
		fieldHolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	    boolean onProjectLevelWithNullProject =
	    	level != null && level.equals(IPreferencesService.PROJECT_LEVEL) && service.getProject() == null;
	    boolean notOnARealLevel = level == null;
	    boolean onAFunctioningLevel = !onProjectLevelWithNullProject && !notOnARealLevel;
	    
		IntegerFieldEditor field = new IntegerFieldEditor(page, tab, service, level, key, text, fieldHolder);
		
		if (!onProjectLevelWithNullProject) {
			setField(field, fieldHolder);
			addStringPreferenceChangeListeners(service, level, field, key, fieldHolder);
		} else {
			//setField(field, fieldHolder);
			//addStringPropertyChangeListeners(service, level, field, key, fieldHolder);
		}
		
		if (onProjectLevelWithNullProject || notOnARealLevel) {
			field.getTextControl().setEnabled(false);
			field.getTextControl().setEditable(false);
			field.setEnabled(false, field.getParent());
		} else if (onAFunctioningLevel) {
			field.getTextControl().setEnabled(isEnabled);
			field.getTextControl().setEditable(isEditable);
			field.setEnabled(isEnabled, field.getParent());
		}

		if (hasSpecialValue)
			field.setSpecialValue(specialValue);
		else
			field.setNoSpecialValue();
		field.setEmptyValueAllowed(emptyValueAllowed);
		
		if (level == null) field.setRemovable(false);	// can never remove from a field that doesn't have a stored value
		else if (level.equals(IPreferencesService.DEFAULT_LEVEL)) field.setRemovable(false);	// can never remove from Default level
		else field.setRemovable(isRemovable);
		
		initializeField(field, page);
		//System.err.println("SPU.makeNewIntegerField() ending for key = " + key);
		return field;
	}

	
	public CompilerOptionsStringFieldEditor makeNewCompilerOptionsStringField(
			PreferencePage page,
			PreferencesTab tab,
			IPreferencesService service,
			String level, String key, String text,
			Composite parent,
			boolean isEnabled, boolean isEditable,
			boolean hasSpecialValue, String specialValue,
			boolean emptyValueAllowed, String emptyValue,
			boolean isRemovable, Object x)
	{
		//System.err.println("SPU.makeNewStringField() starting for key = " + key);
		Composite fieldHolder = new Composite(parent, SWT.NONE);
		fieldHolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	    boolean onProjectLevelWithNullProject =
	    	level != null && level.equals(IPreferencesService.PROJECT_LEVEL) && service.getProject() == null;
	    boolean notOnARealLevel = level == null;
	    boolean onAFunctioningLevel = !onProjectLevelWithNullProject && !notOnARealLevel;
	    
		CompilerOptionsStringFieldEditor field = new CompilerOptionsStringFieldEditor(page, tab, service, level, key, text, fieldHolder);
		
		if (!onProjectLevelWithNullProject) {
			setField(field, fieldHolder);
			addStringPreferenceChangeListeners(service, level, field, key, fieldHolder);
		} else {
			//setField(field, fieldHolder);
			//addStringPropertyChangeListeners(service, level, field, key, fieldHolder);
		}
		
		//field.getTextControl().setEnabled(isEnabled);
		if (onProjectLevelWithNullProject || notOnARealLevel) {
			//System.out.println("SPU.makeNewStringField(..):  disabling all");
			field.getTextControl().setEnabled(false);
			field.getTextControl().setEditable(false);
			field.setEnabled(false, field.getParent());
		} else if (onAFunctioningLevel) {
			//System.out.println("SPU.makeNewStringField(..):  setting all to " + isEnabled);
			field.getTextControl().setEnabled(isEnabled);
			field.getTextControl().setEditable(isEditable);
			field.setEnabled(isEnabled, field.getParent());
		}

		if (hasSpecialValue)
			field.setSpecialValue(specialValue);
		else
			field.setNoSpecialValue();
		field.setEmptyValueAllowed(emptyValueAllowed);
		
		if (level == null) field.setRemovable(false);	// can never remove from a field that doesn't have a stored value
		else if (level.equals(IPreferencesService.DEFAULT_LEVEL)) field.setRemovable(false);	// can never remove from Default level
		else field.setRemovable(isRemovable);
		
		initializeField(field, page);
		//System.err.println("SPU.makeNewStringField() ending for key = " + key);
		return field;
	}


	
	public StringFieldEditor makeNewStringField(
			PreferencePage page,
			PreferencesTab tab,
			IPreferencesService service,
			String level, String key, String text,
			Composite parent,
			boolean isEnabled, boolean isEditable,
			boolean hasSpecialValue, String specialValue,
			boolean emptyValueAllowed, String emptyValue,
			boolean isRemovable, Object x)
	{
		//System.err.println("SPU.makeNewStringField() starting for key = " + key);
		Composite fieldHolder = new Composite(parent, SWT.NONE);
		fieldHolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	    boolean onProjectLevelWithNullProject =
	    	level != null && level.equals(IPreferencesService.PROJECT_LEVEL) && service.getProject() == null;
	    boolean notOnARealLevel = level == null;
	    boolean onAFunctioningLevel = !onProjectLevelWithNullProject && !notOnARealLevel;
	    
		StringFieldEditor field = new StringFieldEditor(page, tab, service, level, key, text, fieldHolder);
		
		if (!onProjectLevelWithNullProject) {
			setField(field, fieldHolder);
			addStringPreferenceChangeListeners(service, level, field, key, fieldHolder);
		} else {
			//setField(field, fieldHolder);
			//addStringPropertyChangeListeners(service, level, field, key, fieldHolder);
		}
		
		//field.getTextControl().setEnabled(isEnabled);
		if (onProjectLevelWithNullProject || notOnARealLevel) {
			//System.out.println("SPU.makeNewStringField(..):  disabling all");
			field.getTextControl().setEnabled(false);
			field.getTextControl().setEditable(false);
			field.setEnabled(false, field.getParent());
		} else if (onAFunctioningLevel) {
			//System.out.println("SPU.makeNewStringField(..):  setting all to " + isEnabled);
			field.getTextControl().setEnabled(isEnabled);
			field.getTextControl().setEditable(isEditable);
			field.setEnabled(isEnabled, field.getParent());
		}

		if (hasSpecialValue)
			field.setSpecialValue(specialValue);
		else
			field.setNoSpecialValue();
		field.setEmptyValueAllowed(emptyValueAllowed);
		
		if (level == null) field.setRemovable(false);	// can never remove from a field that doesn't have a stored value
		else if (level.equals(IPreferencesService.DEFAULT_LEVEL)) field.setRemovable(false);	// can never remove from Default level
		else field.setRemovable(isRemovable);
		
		initializeField(field, page);
		//System.err.println("SPU.makeNewStringField() ending for key = " + key);
		return field;
	}


	public class StringPreferenceChangeListener extends PreferenceChangeListener {
		public StringPreferenceChangeListener(org.eclipse.imp.preferences.fields.FieldEditor field, String key, Composite composite) {
			super(field, key, composite);
		}
		
		protected void setFieldByListener(org.eclipse.imp.preferences.fields.FieldEditor field, Composite composite) {
			setField((StringFieldEditor) field, composite);
		}
	}

	public class FontPreferenceChangeListener extends PreferenceChangeListener {
		public FontPreferenceChangeListener(org.eclipse.imp.preferences.fields.FieldEditor field, String key, Composite composite) {
			super(field, key, composite);
		}
		
		protected void setFieldByListener(org.eclipse.imp.preferences.fields.FieldEditor field, Composite composite) {
			setField((FontFieldEditor) field, composite);
		}
	}
	
	public class BooleanPreferenceChangeListener extends PreferenceChangeListener {
		public BooleanPreferenceChangeListener(org.eclipse.imp.preferences.fields.FieldEditor field, String key, Composite composite) {
			super(field, key, composite);
		}
		
		protected void setFieldByListener(org.eclipse.imp.preferences.fields.FieldEditor field, Composite composite) {
			setField((BooleanFieldEditor) field, composite);
		}
	}
	
	
	private void addStringPreferenceChangeListeners(
		IPreferencesService service, String level, StringFieldEditor field, String key, Composite composite)
	{
		int levelIndex = service.getIndexForLevel(level);
		IEclipsePreferences[] nodes = service.getNodesForLevels();
		
		for (int i = levelIndex; i < nodes.length; i++) {
			if (nodes[i] != null) {
				nodes[i].addPreferenceChangeListener(new StringPreferenceChangeListener(field, key, composite));	
			} else {
				//	System.err.println("JsdivInstancePreferencesPage.addPropetyChangeListeners(..):  no listener added at level = " + i + "; node at that level is null");
			}
		}	
	}

	private void addFontPreferenceChangeListeners(
			IPreferencesService service, String level, FontFieldEditor field, String key, Composite composite)
		{
			int levelIndex = service.getIndexForLevel(level);
			IEclipsePreferences[] nodes = service.getNodesForLevels();
			
			for (int i = levelIndex; i < nodes.length; i++) {
				if (nodes[i] != null) {
					nodes[i].addPreferenceChangeListener(new FontPreferenceChangeListener(field, key, composite));	
				} else {
					//	System.err.println("PreferencesUtilities.addComboPropertyChangeListeners(..):  no listener added at level = " + i + "; node at that level is null");
				}
			}	
		}
		
	private void addBooleanPropertyChangeListeners(
			IPreferencesService service, String level, BooleanFieldEditor field, String key, Composite composite)
		{	
			int levelIndex = service.getIndexForLevel(level);
			IEclipsePreferences[] nodes = service.getNodesForLevels();
			
			for (int i = levelIndex + 1; i < nodes.length; i++) {
				if (nodes[i] != null) {
					nodes[i].addPreferenceChangeListener(new BooleanPreferenceChangeListener(field, key, composite));	
				} else {
					//System.err.println("JsdivConfigurationPreferencesPage.addPropetyChangeListeners(..):  no listener added at level = " + i + "; node at that level is null");
				}
			}		
		}


	
	public boolean firstLevelAboveSecond(String first, String second) {
		return service.indexForLevel(first) > service.indexForLevel(second);
	}
	
	
	
	public void createToggleFieldListener(BooleanFieldEditor booleanField, StringFieldEditor stringField, boolean sense)
	{
		createFieldControlToggle(booleanField, stringField, sense);
		createFieldStateToggle(booleanField, stringField, sense);
	}
	
	

	public FieldControlToggleListener createFieldControlToggle(
			BooleanFieldEditor booleanField, StringFieldEditor stringField, boolean sense)
	{
		FieldControlToggleListener listener = new FieldControlToggleListener(booleanField, stringField, sense);
		booleanField.getChangeControl().addSelectionListener(listener);
		return listener;
	}

	
	public FieldStateToggleListener createFieldStateToggle(
			BooleanFieldEditor booleanField, StringFieldEditor stringField, boolean sense)
	{
		FieldStateToggleListener listener = new FieldStateToggleListener(booleanField, stringField, sense);
		booleanField.setPropertyChangeListener(listener);
		return listener;
	}

	
	/*
	 * SMS 16 Nov 2006
	 * 
	 * The following two listeners toggle the enabled/editable state
	 * of a text field according to the value of a boolean field.
	 * In the wigetSelected(SelectionEvent) methods,
	 * 		stringField.getTextControl() returns the text field itself,
	 * 		which can be both enabled and editable (separately)
	 * whereas
	 * 		stringField.getParent() returns the larger control in which
	 * 		the text field occurs (e.g., a "string button" field), which
	 * 		will have an enabled state but not an editable state.
	 * 
	 * The FieldStateToggleListener is the effective one in the examples
	 * I've developed.  The FieldcontrolToggleListener isn't used now and
	 * I'm not sure it will actually have a role.
	 */
	
	
	public class FieldControlToggleListener implements SelectionListener
	{
		public BooleanFieldEditor booleanField = null;
		public StringFieldEditor stringField = null;
		boolean sense = true;

		public FieldControlToggleListener(
			BooleanFieldEditor booleanField, StringFieldEditor stringField, boolean sense)
		{
			this.booleanField = booleanField;
			this.stringField = stringField;
			this.sense = sense;
		}
		
		public void widgetSelected(SelectionEvent e) {
			//System.out.println("SPU.FieldControlToggleListener.wigetSelected(..)");
			boolean value = booleanField.getBooleanValue();
			value = sense? value : !value;
			stringField.getTextControl().setEditable(value);
			stringField.getTextControl().setEnabled(value);
			stringField.setEnabled(value, stringField.getParent());
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			//System.out.println("SPU.FieldControlToggleListener.wigetDefaultSelected(..)");
			boolean value = booleanField.getBooleanValue();
			value = sense? value : !value;
			stringField.getTextControl().setEditable(value);
			stringField.getTextControl().setEnabled(value);
			stringField.setEnabled(value, stringField.getParent());
		}
	}

	
	
	public class FieldStateToggleListener implements IPropertyChangeListener
	{
		public BooleanFieldEditor booleanField = null;
		public StringFieldEditor stringField = null;
		boolean sense = true;

		public FieldStateToggleListener(
			BooleanFieldEditor booleanField, StringFieldEditor stringField, boolean sense)
		{
			this.booleanField = booleanField;
			this.stringField = stringField;
			this.sense = sense;
		}
		
	    public void propertyChange(PropertyChangeEvent event) {
			//System.out.println("SPU.FieldStateToggleListener.propertyChange(..)");
			boolean value = ((Boolean)event.getNewValue()).booleanValue();
			value = sense? value : !value;
			stringField.getTextControl().setEditable(value);
			stringField.getTextControl().setEnabled(value);
			stringField.setEnabled(value, stringField.getParent());
	    }
	}
	
	
	/*
	 * For laying out grid data in 	widgets for preferences pages (or anything else)
	 */
	public static void fillGridPlace(Composite composite, int num) {
		for (int i = 1; i <= num; i++) {
			Label label = new Label(composite, SWT.NONE);
			label.setText("This space intentionally left blank");
			label.setVisible(false);
		}
	}
	
}
