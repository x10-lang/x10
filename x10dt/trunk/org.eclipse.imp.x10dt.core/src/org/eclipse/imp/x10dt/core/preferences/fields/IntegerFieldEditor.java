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

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesTab;
import org.eclipse.imp.x10dt.core.preferences.PreferencesUtilities;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

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
 
    /**
     * Load the default value associated with this field.  That is, load
     * the value set at the default level for this field, regardless of
     * the actual level of this field.
     */
    protected void doLoadDefault() {
        if (getTextControl(parent) != null) {
            int value = preferencesService.getIntPreference(IPreferencesService.DEFAULT_LEVEL,	getPreferenceName());
            levelFromWhichLoaded = IPreferencesService.DEFAULT_LEVEL;
            setInherited(false);	// We're putting the default value here directly, not inheriting it
            setPresentsDefaultValue(true);	// Need this really?
            setStringValue(""+value);	// calls valueChanged();
            
            // SMS 28 Nov 2006 added here
            // Value is default but is not inherited
            Text text = getTextControl(parent);
           	text.setBackground(PreferencesUtilities.colorWhite);
        }
    }

    protected void doStore() {
    	String newStringValue = (String)getTextControl(parent).getText();
    	boolean isEmpty = newStringValue.equals(emptyValue);	
    	// getText() will return an empty string if the field is empty,
    	// and empty strings can be stored in the preferences service,
    	// but an empty string is recognized by the preferences service
    	// as a valid value--when usually it is not.  Once it is recognized
    	// as a valid value, it precludes the searching of subsequent
    	// levels that might contain a non-empty (and actually valid) value.
    	// We would like to be able to store a null value with the preferences
    	// service so as to not short-circuit the search process, but we can't	
    	// do that.  So, if the field value is empty, we have to eliminate the
    	// preference entirely.  (Will that work in general???)
    	if (isEmpty && !isEmptyValueAllowed()) {
    		// We have an empty value where that isn't allowed, so clear the
    		// preference.  Expect that clearing the preferences at a level will
    		// trigger a loading with inheritance at that level
    		preferencesService.clearPreferenceAtLevel(preferencesLevel, getPreferenceName());
    		// If the preference value was previously empty (e.g., if previously inherited)
    		// then clearing the preference node now doesn't cause a change event, so
    		// doesn't trigger reloading with inheritance.  So we should just load the
    		// field again to make sure any inheritance occurs if needed
    		loadWithInheritance();
        	int oldValue = preferencesService.getIntPreference(preferencesLevel, getPreferenceName());
        	int newValue = preferencesService.getIntPreference("default", getPreferenceName());
            getPreferenceStore().firePropertyChangeEvent(getPreferenceName(), oldValue, newValue);
    		return;
    	}

    	int oldValue = preferencesService.getIntPreference(preferencesLevel, getPreferenceName());
    	int newValue = isEmpty ? 0 : Integer.parseInt(newStringValue);
    	// We have a value (possibly empty, if that is allowed) that has changed
    	// from the previous value, so store it
    	preferencesService.setIntPreference(preferencesLevel, getPreferenceName(), newValue);
        getPreferenceStore().firePropertyChangeEvent(getPreferenceName(), oldValue, newValue);

   		fieldModified = false;
   		levelFromWhichLoaded = preferencesLevel;
   		isInherited = false;
   		setPresentsDefaultValue(
   				newValue == preferencesService.getIntPreference(IPreferencesService.DEFAULT_LEVEL, getPreferenceName()));
            
   		
   		// If we've stored the field then it's not inherited, so be sure it's
   		// color indicates that.
   		// For text fields, the background color is the backgroud color within
   		// the field, so don't have to worry about matching anything else
   		getTextControl(parent).setBackground(PreferencesUtilities.colorWhite);
    	
    	IEclipsePreferences node = preferencesService.getNodeForLevel(preferencesLevel);
    	try {
    		if (node != null) node.flush();
    	} catch (BackingStoreException e) {
    		System.err.println("IntegerFieldEditor.doStore():  BackingStoreException flushing node;  node may not have been flushed:" + 
    				"\n\tnode path = " + node.absolutePath() + ", preferences level = "  + preferencesLevel);
    	}
    }
}
