/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

package org.eclipse.imp.x10dt.core.preferences.fields;

import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesTab;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class IntegerFieldEditor extends StringFieldEditor {
	
	// From IntegerFieldEditor
    protected int minValidValue = 0;
    protected int maxValidValue = Integer.MAX_VALUE;
    protected static final int DEFAULT_TEXT_LIMIT = 10;

    protected String errorMessage =
    	"Value must be an integer between " + minValidValue + " and " + maxValidValue;
	
    /**
     * Creates an integer field editor
     * Use the method <code>setTextLimit</code> to limit the text.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param width the width of the text input field in characters,
     *  or <code>UNLIMITED</code> for no limit
     * @param strategy either <code>VALIDATE_ON_KEY_STROKE</code> to perform
     *  on the fly checking (the default), or <code>VALIDATE_ON_FOCUS_LOST</code> to
     *  perform validation only after the text has been typed in
     * @param parent the parent of the field editor's control
     * @since 2.0
     */
    public IntegerFieldEditor(
			PreferencePage page, PreferencesTab tab,
    		IPreferencesService service, String level, String name, String labelText,
    		int width, int strategy, Composite parent)
    {
    	super(page, tab, service, level, name, labelText, width, strategy, parent);
    }
	
	
    /**
     * Creates a string field editor.
     * Use the method <code>setTextLimit</code> to limit the text.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param width the width of the text input field in characters,
     *  or <code>UNLIMITED</code> for no limit
     * @param parent the parent of the field editor's control
     */
    public IntegerFieldEditor(
			PreferencePage page, PreferencesTab tab,
    		IPreferencesService service, String level, String name, String labelText,
    		int width, Composite parent)
    {
        super(page, tab, service, level, name, labelText, width, org.eclipse.jface.preference.StringFieldEditor.VALIDATE_ON_KEY_STROKE, parent);
    }
 
    
    /**
     * Creates a string field editor of unlimited width.
     * Use the method <code>setTextLimit</code> to limit the text.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public IntegerFieldEditor(
			PreferencePage page, PreferencesTab tab,
			IPreferencesService service, String level, String name, String labelText,
			Composite parent)
    {
    	// Replaced UNLIMITED text width in the following with default
        super(page, tab, service, level, name, labelText, DEFAULT_TEXT_LIMIT, parent);
    }

    
    /**
     * Copied from IntegerFieldEditor.
     * 
     * Sets the range of valid values for this field.
     * 
     * @param min the minimum allowed value (inclusive)
     * @param max the maximum allowed value (inclusive)
     */
    public void setValidRange(int min, int max) {
        minValidValue = min;
        maxValidValue = max;
    }

    
    
    /**
     * Copied from IntegerFieldEditor.
     * 
     * Returns this field editor's current value as an integer.
     *
     * @return the value
     * @exception NumberFormatException if the <code>String</code> does not
     *   contain a parsable integer
     */
    public int getIntValue() throws NumberFormatException {
        return new Integer(getStringValue()).intValue();
    }
    
    
    /* (non-Javadoc)
     * Copied from IntegerFieldEditor (with minor adaptations).
     * Method declared on StringFieldEditor.
     * Checks whether the entered String is a valid integer or not.
     */
    protected boolean checkState() {
    	
        Text text = getTextControl(parent);

        if (text == null)
            return false;


        String numberString = text.getText();
        if (numberString==null || numberString.equals("")) numberString="0";
        try {
            int number = Integer.valueOf(numberString).intValue();
            if (number >= minValidValue && number <= maxValidValue) {
                clearErrorMessage();
                return true;
            } else {
                setErrorMessage(getLevelName() + ":  " + getLabelText() + "  " + errorMessage);
                return false;
            }
        } catch (NumberFormatException e1) {
            setErrorMessage(getLevelName() + ":  " + getLabelText() + "  " + "Number format exception");
        }

        return false;
    }

    
    public String getLevelName() {
    	if (preferencesLevel.equals(IPreferencesService.DEFAULT_LEVEL)) return "Default";
    	if (preferencesLevel.equals(IPreferencesService.CONFIGURATION_LEVEL)) return "Configuration";
    	if (preferencesLevel.equals(IPreferencesService.INSTANCE_LEVEL)) return "Workspace";
    	if (preferencesLevel.equals(IPreferencesService.PROJECT_LEVEL)) return "Project";
    	return "";
    }
    
    protected void doLoad()
    {
        if (getTextControl(parent) != null) {
        	String value = null;
        	if (preferencesLevel != null) {
        		// The "normal" case, in which field corresponds to a preferences level
        		value = preferencesService.getRawStringPreference(preferencesLevel, getPreferenceName());
        		levelFromWhichLoaded = preferencesLevel;
        		setInherited(false);
        	}
        	else {
        		// Not normal, exactly, but possible if loading is being done into a
        		// field that is not associated with a specific level
        		value = preferencesService.getRawStringPreference(getPreferenceName());
        		levelFromWhichLoaded = preferencesService.getApplicableLevel(getPreferenceName(), preferencesLevel);
    			setInherited(!levelFromWhichLoaded.equals(preferencesLevel));
        	}
        	
        	if (value==null || value.equals("")) value = "0";
           	setPresentsDefaultValue(IPreferencesService.DEFAULT_LEVEL.equals(levelFromWhichLoaded));
            setStringValue(value);
        }
    
    }
}
