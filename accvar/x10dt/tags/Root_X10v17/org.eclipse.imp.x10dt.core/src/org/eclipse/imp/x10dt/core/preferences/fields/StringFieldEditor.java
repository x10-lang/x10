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
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Things to note:
 * 
 * @author sutton, mmk@us.ibm.com
 *
 */

public class StringFieldEditor extends FieldEditor
{
    
	/**
     * Cached valid state.
     */
    private boolean isValid;

    /**
     * Old text value.
     */
    //private String oldValue;
    protected String previousValue;

    /**
     * The text field, or <code>null</code> if none.
     */
    protected Text textField;

    /**
     * Width of text field in characters; initially unlimited.
     */
    protected int widthInChars = org.eclipse.jface.preference.StringFieldEditor.UNLIMITED;

    /**
     * Text limit of text field in characters; initially unlimited.
     */
    protected int textLimit = org.eclipse.jface.preference.StringFieldEditor.UNLIMITED;

    /**
     * The error message, or <code>null</code> if none.
     */
    protected String errorMessage;

    /**
     * Indicates whether the empty string is legal;
     * <code>true</code> by default.
     */
    protected boolean emptyStringAllowed = true;

    /**
     * The empty string (in case you needed it)
     */
	protected final String emptyValue = "";
    
    /**
     * The validation strategy; 
     * <code>VALIDATE_ON_KEY_STROKE</code> by default.
     */
    protected int validateStrategy = org.eclipse.jface.preference.StringFieldEditor.VALIDATE_ON_KEY_STROKE;

 
	
	
    /**
     * Creates a string field editor.
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
    public StringFieldEditor(
			PreferencePage page, PreferencesTab tab,
    		IPreferencesService service, String level, String name, String labelText,
    		int width, int strategy, Composite parent)
    {
    	super(page, tab, service, level, name, labelText, parent);

    	// Relating to IMP things
    	preferencesService = service;
    	preferencesLevel = level;
    	this.parent = parent;
    	prefPage = page;
    	setPage(prefPage);
    	prefTab = tab;	
    	
    	// Relating to StrinfFieldEditor things
        init(name, labelText);
        widthInChars = width;
        setValidateStrategy(strategy);
        isValid = false;
        // Why set this in a local field rather than in the page?
        errorMessage = JFaceResources
                .getString("StringFieldEditor.errorMessage");//$NON-NLS-1$
        createControl(parent);	
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
    public StringFieldEditor(
			PreferencePage page, PreferencesTab tab,
    		IPreferencesService service, String level,
    		String name, String labelText, int width, Composite parent)
    {
        this(page, tab, service, level,
        	 name, labelText, 
     		(width == org.eclipse.jface.preference.StringFieldEditor.UNLIMITED ? 40 : width),
        	 org.eclipse.jface.preference.StringFieldEditor.VALIDATE_ON_KEY_STROKE, parent);
    }
    
    /**
     * Creates a string field editor of unlimited width.
     * Use the method <code>setTextLimit</code> to limit the text.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public StringFieldEditor(
			PreferencePage page, PreferencesTab tab,
			IPreferencesService service, String level,
			String name, String labelText, Composite parent)
    {
        this(page, tab, service, level, name, labelText, 40, parent);
    }

   
    /**
     * @return	The parent control of this field editor
     */
    public Composite getParent() {
    	return parent;
    }
 
    
    
    /**
     * Sets the strategy for validating the text.
     * <p>
     * Calling this method has no effect after <code>createPartControl</code>
     * is called. Thus this method is really only useful for subclasses to call
     * in their constructor. However, it has public visibility for backward 
     * compatibility.
     * </p>
     *
     * @param value either <code>VALIDATE_ON_KEY_STROKE</code> to perform
     *  on the fly checking (the default), or <code>VALIDATE_ON_FOCUS_LOST</code> to
     *  perform validation only after the text has been typed in
     *  
     *  Copied from StringFieldEditor and adapted to this location.
     */
    public void setValidateStrategy(int value) {
        Assert.isTrue(value == org.eclipse.jface.preference.StringFieldEditor.VALIDATE_ON_FOCUS_LOST
                || value == org.eclipse.jface.preference.StringFieldEditor.VALIDATE_ON_KEY_STROKE);
        validateStrategy = value;
    }

    
    /*
     * Methods related to a special value for this field, treated
     * as a string (as you would expect)
     */

    /**
     * @return	The special value associated with this field, if there
     * 			is such a value and it is a string
     * @throws	IllegalStateException
     * 				If the field does not have a special value or
     * 				if the special value is not a string
     */
	public String getSpecialStringValue() { 
		if (!hasSpecialValue) {
			throw new IllegalStateException("StringField.getSpecialValue():  field does not have a special value");
		} else if (!(specialValue instanceof String)) {
			throw new IllegalStateException("StringField.getSpecialValue():  special value is not a String");
		} else {
			return (String) specialValue;
		}
	}
	
  
	/**
	 * Set the special value associated with this field to be the given string.
	 * Overrides the method in the supertype to check that the given value is
	 * a String.
	 * 
	 * @param specialValue	The special value to associate with this field
	 * @throws IllegalStateException
	 * 				If the given value is not a String
	 */
	public void setSpecialValue(String specialValue) {
		if (!(specialValue instanceof String)) {
			throw new IllegalStateException("StringField.setSpecialValue():  given value is not a String");
		}
		hasSpecialValue = true;
		this.specialValue = specialValue;
	}

	
	/*
	 * Methods related to whether the empty string is allowed
	 * for this field.
	 * 
	 * These methods mimic coresponding instance methods defined
	 * on the superclass.
	 */
	
	
	/**
	 * @return	Whether the empty string is allowed as a value
	 * 			for this field.
	 */
	public boolean isEmptyValueAllowed() {
		return emptyStringAllowed;
	}
	
	/**
	 * Sets whether the empty string is allowed as a value
	 * for this field.
	 * 
	 * @param allowed	Whether ...
	 */
	public void setEmptyValueAllowed(boolean allowed) {
		emptyStringAllowed = allowed;
	}
	

	
	/**
	 * @return	The empty value for fields of this type,
	 * 			i.e., the empty string, regardless of whether
	 * 			that value is allowed for a particular field
	 */
	public String getEmptyValue() {
			return emptyValue;
	}


	

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    public boolean isValid() {
        return isValid;
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void refreshValidState() {
        isValid = checkState();
    }
	
	
    /*
     * Methods related to loading values from the preferences service
     * into the preferences store.
     * 
     * All of the "doLoad..." methods should
     * - Set isInherited, presentsDefaultValue, and levelFromWhichLoaded
     *   since these are know directly here and vary from load method to
     *   load method
     * - Call setStringValue(..), which will set previousValue and
     *   fieldModified (which can be set generally given the old and
     *   new values), and which will also call valueChanged(), which
     */
	
	
	
	/**
	 * Load the string value for this field.  Load it according to
	 * the preference level associated with this field, if there is one,
	 * or load it according to an applicable level, if not.
	 */
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
           	setPresentsDefaultValue(IPreferencesService.DEFAULT_LEVEL.equals(levelFromWhichLoaded));
            setStringValue(value);
        }
    }

 
    /**
     * Load the default value associated with this field.  That is, load
     * the value set at the default level for this field, regardless of
     * the actual level of this field.
     */
    protected void doLoadDefault() {
        if (getTextControl(parent) != null) {
            String value = preferencesService.getRawStringPreference(IPreferencesService.DEFAULT_LEVEL,	getPreferenceName());
            levelFromWhichLoaded = IPreferencesService.DEFAULT_LEVEL;
            setInherited(false);	// We're putting the default value here directly, not inheriting it
            setPresentsDefaultValue(true);	// Need this really?
            setStringValue(value);	// calls valueChanged();
            
            // SMS 28 Nov 2006 added here
            // Value is default but is not inherited
            Text text = getTextControl(parent);
           	text.setBackground(PreferencesUtilities.colorWhite);
        }
    }

    

    /**
     * Do the work of loading the value for the given level into the field.
     */
    protected void doLoadLevel(String level) {
        if (getTextControl(parent) != null) {
        	String value = null;
        	if (preferencesLevel != null) {
        		value = preferencesService.getRawStringPreference(level, getPreferenceName());
        	} else {
        		// TODO:  Check whether this is right
        		value = preferencesService.getRawStringPreference(getPreferenceName());
        	}
         	// We're putting the level's value here directly, not inheriting it, so ...
        	levelFromWhichLoaded = level;
        	setInherited(false);
           	setPresentsDefaultValue(IPreferencesService.DEFAULT_LEVEL.equals(level));
        	setStringValue(value);	// calls valueChanged();
        }
    }



	/**	
     * Do the work of setting the currently applicable value for this field,
     * inheriting the value from a higher level if the value is not stored
     * on the level associated with the field. (The "default" level should
     * always have a value.)  Load nothing and return null if no value is found.
     * 
     * Should set varous fields such as levelFromWhichLoaded, previousValue,
     * isInherited, and fieldModified.  Should also adjust the appearance of
     * the field on the preferences page to reflect inherited state.
     * 
     * @return	The level from which the applicable value was loaded or
     * 			null if no value found.
     */
    protected String doLoadWithInheritance()
    {
    	String levelLoaded = null;
    	
    	String[] levels = IPreferencesService.levels;
    	int fieldLevelIndex = 0;

    	// If we're loading with inheritance for some field that is
    	// not attached to a preferences level (such as the "applicable"
    	// field, which inherits values from all of the real fields)
    	// then assume that we should search from the bottom up
    	String tmpPreferencesLevel = 
    		(preferencesLevel == null)?
    			levels[0]:
    			preferencesLevel;
    	
    	// Find the index of the level to which this field belongs
    	for (int i = 0; i < levels.length; i++) {
    		if (tmpPreferencesLevel.equals(levels[i])) {
    			fieldLevelIndex = i;
    			break;
    		}
    	}
    	
    	String value = null;
    	int levelAtWhichFound = -1;
    	
    	for (int level = fieldLevelIndex; level < levels.length; level++) {
       		value = preferencesService.getRawStringPreference(levels[level], getPreferenceName());
       		if (value == null) continue;
       		if (value.equals("") && !isEmptyValueAllowed()) continue;
       		levelAtWhichFound = level;
       		levelLoaded = levels[levelAtWhichFound];
       		break;	
    	}
    	
    	String previousLevelFromWhichLoaded = levelFromWhichLoaded; //mmk fix inappropriate change modifier set
    	
    	levelFromWhichLoaded = levelLoaded;
    	setInherited(fieldLevelIndex != levelAtWhichFound);
       	setPresentsDefaultValue(IPreferencesService.DEFAULT_LEVEL.equals(levelFromWhichLoaded));
       	boolean valueChanged = previousValue==null && value!=null || previousValue!=null && value==null || !previousValue.equals(value);
       	boolean levelChanged = previousLevelFromWhichLoaded==null && levelFromWhichLoaded!=null || previousLevelFromWhichLoaded!=null && levelFromWhichLoaded==null || !previousLevelFromWhichLoaded.equals(levelFromWhichLoaded);
       	if (levelChanged || valueChanged) {
       		setStringValue(value);		// sets fieldModified and previousValue
       	}
       	
       	// Set the background color of the field according to where found
        Text text = getTextControl(parent);
        if (isInherited())
        	text.setBackground(PreferencesUtilities.colorBluish);
        else
        	text.setBackground(PreferencesUtilities.colorWhite);
        
        return levelLoaded;
    }

    
    
    /*
     * Method related to storing String values for this field
     */
    
    protected void doStore() {
    	String oldValue = preferencesService.getStringPreference(preferencesLevel, getPreferenceName());
    	String 	newValue = getTextControl(parent).getText();
    	boolean isEmpty = newValue.equals(emptyValue);	
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
    		return;
    	}

    	// We have a value (possibly empty, if that is allowed) that has changed
    	// from the previous value, so store it
    	preferencesService.setStringPreference(preferencesLevel, getPreferenceName(), newValue);
        getPreferenceStore().firePropertyChangeEvent(getPreferenceName(), oldValue, newValue);

   		fieldModified = false;
   		levelFromWhichLoaded = preferencesLevel;
   		isInherited = false;
   		setPresentsDefaultValue(
   				newValue.equals(preferencesService.getRawStringPreference(IPreferencesService.DEFAULT_LEVEL, getPreferenceName())));
            
   		
   		// If we've stored the field then it's not inherited, so be sure it's
   		// color indicates that.
   		// For text fields, the background color is the backgroud color within
   		// the field, so don't have to worry about matching anything else
   		getTextControl(parent).setBackground(PreferencesUtilities.colorWhite);
    	
    	IEclipsePreferences node = preferencesService.getNodeForLevel(preferencesLevel);
    	try {
    		if (node != null) node.flush();
    	} catch (BackingStoreException e) {
    		System.err.println("StringFieldEditor.doStore():  BackingStoreException flushing node;  node may not have been flushed:" + 
    				"\n\tnode path = " + node.absolutePath() + ", preferences level = "  + preferencesLevel);
    	}
    }
    
 
    /*
     * Methods related to getting and setting the String value for this field
     */
    
    
    /**
     * @return the previous value held by this field
     */
    protected String getPreviousStringValue() {
    	return (String) previousValue;
    }
    
    
    /**
     * Sets the previous value held by this field
     */
    protected void setPreviousStringValue(String value) {
    	previousValue = value;
    }

    
    /**
     * Set the value of this field directly, from outside of
     * the field, without loading a value from the preferences
     * service.
     *  	
     * Intended for use by external clients of the field.
     * 
     * In addition to setting the value of the field this method
     * also sets several attributes to appropriately characterize
     * a field that has been set in this way.
     * 
     * @param newValue
     */
    public void setFieldValueFromOutside(String newValue) {
    	setPreviousStringValue(getStringValue());
    	setInherited(false);
    	setPresentsDefaultValue(false);
    	levelFromWhichLoaded = null;
    	setStringValue(newValue);
    }
  
    
    
    /**
     * Returns the field editor's current value.
     *
     * @return the current value
     */
    public String getStringValue() {
    	String value = getTextControl(parent).getText();
    	return value;
    }


    /**
     * Set this field editor's value.
     * Likewise set previousValue and fieldModified flags,
     * and call valueChanged() to signal that the value has
     * changed.
     * 
     *
     * @param value the new value, or <code>null</code> meaning the empty string
     */
    protected void setStringValue(String value) {	
        if (getTextControl(parent) != null) {
            if (value == null)
                value = "";//$NON-NLS-1$
            setPreviousStringValue(getTextControl(parent).getText());
        	getTextControl(parent).setText(value);
        	// Set fieldModified here because we know we just set it
        	// and we consider it modified even if the old and new
        	// values are the same
        	fieldModified = true;
        	// Set on this level, and so not inherited
        	// SMS 17 Nov 2006:  level and inherited should be set in doLoad methods
        	// Or wherever else setStringValue is called from (e.g., listeners)
        	//levelFromWhichLoaded = preferencesLevel;
        	//isInherited = levelFromWhichLoaded == preferencesLevel;
        	valueChanged();
        	// valueChanged() takes care of setting the previous value
        	// and presents default value
	//            }
//       		setPresentsDefaultValue(
//       				value.equals(preferencesService.getStringPreference(IPreferencesService.DEFAULT_LEVEL, getPreferenceName())));
        }
    }

    
    
 
   
    /**
     * Informs this field editor's listener, if it has one, about a change
     * to the value (<code>VALUE</code> property) provided that the old and
     * new values are different.  Also informs the listener (if there is one)
     * of a change in the validity of the field (<code>IS_VALID</code> property). 
     * 
     * This hook is <em>not</em> called when the text is initialized 
     * (or reset to the default value) from the preference store.
     * (That comment is taken from the original implementation of this
     * method.  I've tried to follow it consistently for IMP preferences,
     * but I'm not sure if the original intention translates into the
     * multi-level model.  Still, so far there seems to be no problem
     * with it.  SMS 16 Nov 2006)
     * 
     * Copied from StringFieldEditor and adapted to use in IMP.
     * Added return of a boolean value.  Not intended to set any attributes
     * of the field editor, just to signal changes to listeners.
     */
    protected boolean valueChanged() {
    	return valueChanged(false);
    }
    
    protected boolean valueChanged(boolean assertChanged)
    {
        // Check for change in value
    	boolean valueChanged = assertChanged || inheritanceChanged();
        String newValue = textField.getText();
        if (!valueChanged) {
        	valueChanged = newValue.equals(getPreviousStringValue());
        }

        if (valueChanged) {
        	// Check for change in validity
            fireValueChanged(VALUE, getPreviousStringValue(), newValue);
	        boolean wasValid = isValid;
	        refreshValidState();
	        if (isValid != wasValid)
	            fireStateChanged(IS_VALID, wasValid, isValid);
	
            fieldModified = true;
            setPreviousStringValue(newValue);
	        setModifiedMarkOnLabel();
        	// mmk moved here from doLoadWithInheritence to coordinate background color change with changed-value indicator
            // SMS 28 Nov 2006 added here
           	// Set the background color of the field according to where found
            Text text = getTextControl(parent);
            if (isInherited())
            	text.setBackground(PreferencesUtilities.colorBluish);
            else
            	text.setBackground(PreferencesUtilities.colorWhite);

        }
        
        return valueChanged;
    }

    
 
    
    /**
     * Checks whether the text input field contains a valid value or not.
     *
     * @return <code>true</code> if the field value is valid,
     *   and <code>false</code> if invalid
     */
    protected boolean checkState()
    {
        boolean result = true;
    
        if (!emptyStringAllowed && getStringValue().length() == 0) {
        	setErrorMessage(getFieldMessagePrefix() + "String is empty when empty string is not allowed");
            result = false;
        }

        if (result && getTextControl(parent) == null) {
        	setErrorMessage(getFieldMessagePrefix() + "Text control is null; no valid value represented");
            result = false;
        }
        
        if (result && getTextControl(parent) != null) {
        	String txt = getStringValue();
        	result = (txt.trim().length() > 0) || emptyStringAllowed;
        	if (!result) {
        		setErrorMessage(getFieldMessagePrefix() + "String is blank when empty string is not allowed");
        	}
        }
        
        if (result)
        	result = result && doCheckState();
        
        return notifyState(result);
    }
    
 
    
    /**
     * A hook for subclasses to implement more specialized checks
     * 
     * @return	Whether the state is okay based on additional checks
     */
    protected boolean doCheckState() {
    	clearErrorMessage();
    	return true;
    }
    
    

    protected boolean textNull = true;

    
    public Text getTextControl() {
    	return getTextControl(parent);
    }
    
    
    /**
     * Returns this field editor's text control.
     * 
     * The control is created if it does not yet exist
     * 
     * Copied from StringFieldEditor and adapted to this location.
     *
     * @param parent the parent
     * @return the text control
     */
    public Text getTextControl(Composite parent) {
        if (textField == null) {
            textField = new Text(parent, SWT.SINGLE | SWT.BORDER);
            textField.setFont(parent.getFont());
            switch (validateStrategy) {
            case org.eclipse.jface.preference.StringFieldEditor.VALIDATE_ON_KEY_STROKE:
                textField.addKeyListener(new KeyAdapter() {

                    /* (non-Javadoc)
                     * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
                     */
                    public void keyReleased(KeyEvent e) {
	                	// Should call setInherited(..) before calling valueChanged() because
	                	// valueChanged() will mark the field as modified, but only if isInherited
	                	// is false, which it now should be
                        refreshValidState();
    					setInherited(false);
                        boolean changed = valueChanged(true);
    					fieldModified = changed;

                    }
                });

                break;
            case org.eclipse.jface.preference.StringFieldEditor.VALIDATE_ON_FOCUS_LOST:
                textField.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent e) {
                        clearErrorMessage();
                    }
                });
                textField.addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) {
                        refreshValidState();
                    }

                    public void focusLost(FocusEvent e) {
                    	// Assume  no change in inheritance with this event
                        valueChanged();
                        clearErrorMessage();
                    }
                });
                break;
            default:
                Assert.isTrue(false, "Unknown validate strategy");//$NON-NLS-1$
            }
            // SMS 16 Nov 2006
            // The original StringFieldEditor just had a text-field modify listener,
            // as copied below.  But that would be redundant with the listeners copied
            // from StringFieldEditor above.  The use of more than one listener for the
            // same event is problematic if they all check for changes in the text value,
            // because only the first will see a change, and other will set fieldModified
            // to false, in effect cancelling processing that should occur following the
            // triggering change
//	    	textField.addModifyListener(
//	    			new ModifyListener() {
//	    				public void modifyText(ModifyEvent e) {
//	    					//System.out.println("STFE.text modify listener (from getTextControl):  textModified set to true");
//	                        valueChanged();
//	    					fieldModified = true;
//	    					setInherited(false);
//	    				}
//	    			}
//	    	);
            textField.addDisposeListener(new DisposeListener() {
                public void widgetDisposed(DisposeEvent event) {
                    textField = null;
                }
            });
            if (textLimit > 0) {//Only set limits above 0 - see SWT spec
                textField.setTextLimit(textLimit);
            }
        } else {
            checkParent(textField, parent);
        }
        return textField;
    }

    
    
    /*
     * UI-related methods copied from StringFieldEditor
     */
    
    
    /**
     * Does something with the columns
     * (but the IMP templates adjust columns separately)
     */
    protected void adjustForNumColumns(int numColumns) {
        GridData gd = (GridData) textField.getLayoutData();
        gd.horizontalSpan = numColumns - 1;
        // We only grab excess space if we have to
        // If another field editor has more columns then
        // we assume it is setting the width.
        gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
    }

    
    /**
     * Fills this field editor's basic controls into the given parent.
     * <p>
     * The string field implementation of this <code>FieldEditor</code>
     * framework method contributes the text field. Subclasses may override
     * but must call <code>super.doFillIntoGrid</code>.
     * </p>
     */
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        getLabelControl(parent);

        textField = getTextControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns - 1;
        if (widthInChars != org.eclipse.jface.preference.StringFieldEditor.UNLIMITED) {
            GC gc = new GC(textField);
            try {
                Point extent = gc.textExtent("X");//$NON-NLS-1$
                gd.widthHint = widthInChars * extent.x;
            } finally {
                gc.dispose();
            }
        } else {
            gd.horizontalAlignment = GridData.FILL;
            gd.grabExcessHorizontalSpace = true;
        }
        textField.setLayoutData(gd);
    }
    
    
    /**
     * Returns the number of controls in this editor.
     * These are two:  the text control and the label control.
     * 
     * @return	The number of controls in this editor
     */
    public int getNumberOfControls() {
        return 2;
    }

    
    
}
