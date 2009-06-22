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
import org.eclipse.imp.preferences.Markings;
import org.eclipse.imp.preferences.PreferencesTab;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


/**
 * Points to note:
 * - Preferences service instead of a preference store
 * - Preferences have a level
 *   - Four fixed levels
 *   - Named by strings because ...
 * - Makes some provision for loading, etc. into fields
 *   on pages that are *not* associated with a level (just in case)
 * - Preferences are displayed on a tab on a page
 * - Preference values may be inherited
 * 
 * @author sutton, mmk@us.ibm.com
 *
 */



public abstract class FieldEditor extends org.eclipse.imp.preferences.fields.FieldEditor
{
	/**
	 *  The preferences page on which the tab that contains this
	 *  field is displayed
	 */ 
	// Relationship to dialogPage in FieldEditor?
	protected PreferencePage prefPage = null;
	
	/**
	 * The preferences tab on which the field is displayed
	 */ 
	protected PreferencesTab prefTab = null;
	
	
	/**
	 * The preferences service in which the values for this
	 * field are stored
	 */ 
	protected IPreferencesService preferencesService = null;
	
	/**
	 * The preferences level with which this field is associated
	 */
    protected String preferencesLevel = null;

    /**
     * The Composite control that contains the field
     */
	protected Composite parent = null;
	
	/**
	 *  Whether the value shown for this field is stored on
	 *  the level for this field or inherited from a higher level
	 */
    protected boolean isInherited = false;
    
	/**
	 *  Whether the value shown for this field was previously stored on
	 *  the level for this field or inherited from a higher level
	 */
    protected boolean wasInherited = false;
    
    /**
     * Whether the value stored for this field (if any) can be
     * removed.  (Removal of a value on a lower level will
     * generally result in the inheritance of a value from a
     * higher level.  The effects of removal of values at the
     * top ("default") are not defined and this should generally
     * not be allowed.)
     */
	protected boolean isRemovable = false;
	
    /**
     * Flags whether the field has an associated "special" 
     * (distinguished) value (the purpose of which is
     * generally field specific)
     */
	protected boolean hasSpecialValue = false;
	
	/**
	 * A general placeholder for a special (distinguished) value
	 * that may be associated with this field (the purpose of
	 * which is generally field specific)
	 */
	// Not sure whether such a general representation will be
	// useful, but it conveys the idea
	protected Object specialValue = null;
    
	
	/**
	 * Name of the preferences level from which the value
	 * displayed by this field was loaded.  If non-null then
	 * will generally be one of the four standard level names.
	 */
    protected String levelFromWhichLoaded = null;
	
	
	/**
	 * Whether the value held by the field is the same as the
	 * value stored in the corresponding preferences node (that
	 * is, the node from which it was most recently loaded or
	 * the node into which it was most recently stored)
	 */
    protected boolean fieldModified = false;
    
    
    protected Object previousValue = null;
    
    //
    // Fields in FieldEditor:
    //
    
    // private String preferenceName has public get and set methods
    
    // private *** preferenceStore is deprecated (along with any associated methods)
    
    // private boolean field isDefaultPresented has public read and write methods
    
    // private String labelText has public (or protected) get and set methods;
    // also settable through init (called by constructors but protected and not
    // apparently limited to use at construction time)
    
    // private Label label has public and protected get methods; public
    // one creates when first called; no other set methods (makes sense)
    
    // private field IPropertyChangeListener propertyChangeListener has a public
    // set method but no get method (needed here?)
    
    // private field DialogPage page has public get and set methods
    
    // Note:  there is no parent field
    
    
    
    
    /**
     * Parameterless constructor that mimics the one in FieldEditor
     */
    protected FieldEditor() {
    	super();
    }
    
    
    /**
     * Creates a field editor, taking the information that is
     * specific to IMP field editors but not that used by
     * field editors in general.  Calls the empty constructor for
     * a field editor.
     * 
     * @param	page	The preferences page on which the tab is shown
     * @param	tab		The tab on which field editor is shown
     * @param	service	The preferences service in which the preference
     * 					values are stored
     * @param	level	The level at which this preference is assigned
     */
    // Able to do anything useful without a parent?
    public FieldEditor(
			PreferencePage page, PreferencesTab tab,
    		IPreferencesService service, String level)
    {
    	super();
    	preferencesService = service;
    	preferencesLevel = level;
    	prefPage = page;
    	setPage(prefPage);
    	prefTab = tab;	
    }
    
    
    /**
     * Creates a field editor, taking all of the relevant information
     * (IMP specific and field-editor generic). Calls the non-empty
     * constructor for a field editor.
     * 
     * @param	page	The preferences page on which the tab is shown
     * @param	tab		The tab on which field editor is shown
     * @param	service	The preferences service in which the preference
     * 					values are stored
     * @param	level	The level at which this preference is assigned
     * @param	name	The name of this preference
     * @param	labelText	The text used to label the field on the page
     * @param	parent	The composite control that contains this editor
     */
    public FieldEditor(
			PreferencePage page, PreferencesTab tab,
    		IPreferencesService service, String level,
    		String name, String labelText, Composite parent)
    {
    	//super(name, labelText, parent);
    	super();
    	init(name, labelText);
    	preferencesService = service;
    	preferencesLevel = level;
    	this.parent = parent;
    	prefPage = page;
    	setPage(prefPage);
    	prefTab = tab;	
    }
	
	
    
    /*
     * Methods to get and set the preferences level from which the value
     * of this field was loaded (or set)
     */
    
    

    /**
     * Get the preferences level from which the value of this field
     * was set.  Should be the name of a preferences level if the
     * value was set from a preferences node, or null if the value
     * was set directly into the field.
     * 
     * @return	The preferencesl level from which the current value
     * 			for this field was loaded, or null if the current value
     * 			was set directly
     */
    public String getLevelFromWhichLoaded() {
    	return levelFromWhichLoaded;
    }
  

   	
  
    /*
     * Methods to get and set the isInherited field for this editor
     */
    
    /**
     * @return		Whether the value displayed for this field is
     * 				stored at the level of this field or inherited from
     * 				a higher level
     */
    public boolean isInherited() { return isInherited; }
    
    
    /**
     * Sets the field that indicates whether the value displayed for
     * this field is stored on the level for this field or inherited from
     * a higher level.
     * 
     * @param inherited		Whether the value is stored or inherited
     */
    protected void setInherited(boolean inherited) {
    	wasInherited = isInherited;
    	isInherited = inherited;
    }
    
    
    /**
     * @return Whether the inheritence state of this field has changed
     * from its previous value	
     */
    protected boolean inheritanceChanged() {
    	return wasInherited != isInherited;
    }
    
    
    /*
     * Methods relating to a special value for this field.
     * 
     * The value is an Object, which is not very specific
     * (but is very general).
     * 
     * Fields that are intended to represent value types
     * (which does not seem to be a common practice) can
     * convert values to objects if necessary to use these.	
     */
    
	
    /**
     * @return	Whether there is a special (distinguished) value
     * 			associated with this field	
     */
	public boolean hasSpecialValue() { return hasSpecialValue; }
	
	
	/**
	 * Sets the flag hasSpecialValue to false and nullifies
	 * the specialValue field.  
	 *
	 */
	public void setNoSpecialValue() {
		hasSpecialValue = false;
		specialValue = null;
	}
 
	/**
	 * @return	The special value associated with this field,
	 * 			if any
	 * @throws	IllegalStateException if this field has no
	 * 			associated special value
	 */
	public Object getSpecialValue() { 
		if (hasSpecialValue) return specialValue;
		throw new IllegalStateException("FieldEditor.getSpecialValue():  called when field does not have a special value");
	}
	
   
	/**
	 * Assigns the given object to the special value for this
	 * field and sets the hasSpecialValue flag to true.
	 * 
	 * @param specialValue	The special value associated with this field.
	 */
	public void setSpecialValue(Object specialValue) {
		hasSpecialValue = true;
		this.specialValue = specialValue;
	}

	
// Empty is a sting-related concept, not for fields in general
//
//	public boolean isEmptyValueAllowed() {
//		return isEmptyStringAllowed();
//	}
//	
//	public void setEmptyValueAllowed(boolean allowed) {
//		setEmptyStringAllowed(allowed);
//	}
//	
//
//	public String getEmptyValue() {
//		if (isEmptyStringAllowed())
//			return emptyValue;
//		throw new IllegalStateException("StringFieldEditor.getEmptyValue:  called when field does not allow an empty value");
//	}
	
	
	/*
	 * Methods relating to the removal of field values
	 */
	
	
	/**
	 * @return	Whether the value stored for this field (if any)
	 * 			can be removed
	 */
	public boolean isRemovable() { return isRemovable; }
	
	
	/**
	 * Sets whether the value stored for this field (if any)
	 * can be removed
	 * 
	 * @param isRemovable	Whether ...
	 */
	public void setRemovable(boolean isRemovable) {
		this.isRemovable = isRemovable;
	}
	
    
	////////////////////////////////////////////////////////////////////////////
	
	
	/*
	 * Methods related to loading a value for the field.  "Loading" a value for
	 * a field means setting the value that the field is to display.
	 * Because of multiple preference levels and inheritance of preference values
	 * this is a more varied and complicated concern than with FieldEditor.
	 */
	
	
    /**
     * Initializes this field editor with a preference value from
     * the preference service.
     * 
     * If the field is associated with a specific preference level (the
     * usual case) then the value from that level, if any, is loaded.
     * 
     * If the field is not associated with a specific preference level,
     * then a value obtained from some applicable level may be loaded.
     * (This is a feature that supports preferences pages that are not
     * strictly aligned with preferences levels, unlike the "usual"
     * approach assumed here.)
     * 
     * Because it cannot be guaranteed here that the value that is loaded
     * is not inherited, the flag isDefaultPresented cannot be set to
     * false here (as it can in FieldEditor).
     */
    public void load() {
        if (preferencesService != null) {
            //isDefaultPresented = false;
            doLoad();
            refreshValidState();
        }
    }

    
    
    

	/**
	 * Do the work of loading
	 */
    abstract protected void doLoad();
    
//    // This is an example implementation from the IMP String field editor
//    // with level-specific and level-independent branches:
//    {
//        if (getTextControl() != null) {
//        	String value = null;
//        	if (preferencesLevel != null) {
//        		// The "normal" case, in which field corresponds to a preferences level
//        		value = preferencesService.getStringPreference(preferencesLevel, getPreferenceName());
//        		levelFromWhichLoaded = preferencesLevel;
//        		setInherited(false);
//        	}
//        	else {
//        		// Not normal, exactly, but possible if loading is being done into a
//        		// field that is not associated with a specific level
//        		value = preferencesService.getStringPreference(getPreferenceName());
//        		levelFromWhichLoaded = preferencesService.getApplicableLevel(getPreferenceName(), preferencesLevel);
//    			setInherited(true);	
//        	}
//            if (IPreferencesService.DEFAULT_LEVEL.equals(levelFromWhichLoaded))
//            	setPresentsDefaultValue(true);
//        	previousValue = value;
//            setStringValue(value);
//        }	
//    }

    
    
    /**
     * Initializes this field editor with the value associated with the
     * default level of the preference store.
     */
    public void loadDefault() {
        if (preferencesService != null) {
        	setPresentsDefaultValue(true);
            doLoadDefault();
            refreshValidState();
        }
    }

    /**
     * Do the work of loading the default value into the field.
     * Generally depends on the type of the field. 
     */
    abstract protected void doLoadDefault();
    
//  // This is an example implementation from the IMP String field editor:
//    {
//        if (getTextControl() != null) {
//            String value = preferencesService.getStringPreference(IPreferencesService.DEFAULT_LEVEL, getPreferenceName());
//            setStringValue(value);
//        }
//        // empty in FieldEditor:
//		  refreshValidState();
//        // Comments on valueChanged() says it is not called when 
//        // a value is initialized or restored from default so
//        // don't call that here
//    }

    
   
    /**
     * Set this field with the preference value associated with
     * the given level.  Sets presentsDefaultValue to true if the
     * given level is "default".
     */
    public void loadLevel(String level) {
        if (preferencesService != null &&
        	preferencesService.isaPreferencesLevel(level))
        {
        	doLoadLevel(level);
        	if (IPreferencesService.DEFAULT_LEVEL.equals(level))
        		setPresentsDefaultValue(true);
        	// SMS 25 Nov 2006
        	// still need to signal valueChanged on default level?
//        	else
        	// SMS 15 Nov 2006:  try this if not default level
        	valueChanged();	
            refreshValidState();
        }
    }

    /**
     * Do the work of loading the value for the given level into the field.
     * Generally depends on the type of the field. 
     */
    abstract protected void doLoadLevel(String level);
    	
//      // This is an example implementation from the IMP String field editor:
//		{
//        if (getTextControl() != null) {
//        	String value = null;
//        	if (preferencesLevel != null) {
//        		value = preferencesService.getStringPreference(level, getPreferenceName());
//        	} else {
//        		value = preferencesService.getStringPreference(getPreferenceName());
//        	}
//        	setStringValue(value);
//        }
//        //valueChanged();
//    }


    
    /**
     * Set this field with the currently applicable preference value,
     * inheriting the value from a higher level if the value is not
     * stored on the level associated with the field. (The "default"
     * level should always have a value.)
     * 
     * @return	The level from which the value was loaded
     */
    public String loadWithInheritance() {
        if (preferencesService != null) {
        	levelFromWhichLoaded = doLoadWithInheritance();
        	if (IPreferencesService.DEFAULT_LEVEL.equals(levelFromWhichLoaded))
            	setPresentsDefaultValue(true);
            refreshValidState();
        }
        return levelFromWhichLoaded;
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
    abstract protected String doLoadWithInheritance();
    
//  // This is an example implementation from the IMP String field editor:
//    {
//    	String levelLoaded = null;
//    	
//    	String[] levels = IPreferencesService.levels;
//    	int fieldLevelIndex = 0;
//
//    	// If we're loading with inheritance for some field that is
//    	// not attached to a preferences level (such as the "applicable"
//    	// field, which inherits values from all of the real fields)
//    	// then assume that we should just search from the bottom up
//    	String tmpPreferencesLevel = 
//    		(preferencesLevel == null)?
//    			levels[0]:
//    			preferencesLevel;
//    	
//    	// Find the index of the level to which this field belongs
//    	for (int i = 0; i < levels.length; i++) {
//    		if (tmpPreferencesLevel.equals(levels[i])) {
//    			fieldLevelIndex = i;
//    			break;
//    		}
//    	}
//    	
//    	String value = null;
//    	int levelAtWhichFound = -1;
//    	
//    	for (int level = fieldLevelIndex; level < levels.length; level++) {
//       		value = preferencesService.getStringPreference(levels[level], getPreferenceName());
//       		if (value == null) continue;
//       		if (value.equals("") && !isEmptyStringAllowed()) continue;
//       		levelAtWhichFound = level;
//       		levelLoaded = levels[levelAtWhichFound];
//       		break;	
//    	}
//    	
//    	// Set the field to the value we found
//        setStringValue(value);
//        
//    	// We loaded it at this level or inherited it from some other level
//    	setInherited(fieldLevelIndex != levelAtWhichFound);
//    	
//    	// Since we just loaded some new text, it won't be modified yet
//    	fieldModified = false;
//    	
//    	// TODO:  Check on use of previous value
//       	previousValue = value;
//       	
//       	// Set the background color of the field according to where found
//        Text text = getTextControl();
//        if (isInherited())
//        	text.setBackground(PreferencesUtilities.colorBluish);
//        else
//        	text.setBackground(PreferencesUtilities.colorWhite);
//  	
//        //System.out.println("doLoadWithInheritance:  preferencesName = " + getPreferenceName() + "; preferenceLevel = " + preferencesLevel + "; levelLoaded = " + levelLoaded);
//        
//        return levelLoaded;
//    }

    
  
 
    /*
     * Methods relating to storing the value of a field.
     */
    
    
    /**
     * Store the value in this field, using the preferences service,
     * at the level associated with this field.
     * 
     * Checks many preconditions.  Throws an exception if the preferences
     * service or preferences level is null or if the associated level is
     * the project level but no project is set.  Returns without effect if
     * the value is inherited, if the value is the default value, or if the
     * field is not modified.
     * 
     * Why not store inherited values?  Inherited values are shown for a
     * field when the field has no value of its own.  They are shown because
     * they apply on the field's level but they are not actually set (or stored)
     * on that level.  Since there is no value actually set for the field,
     * there is no value to store for it.  Note that separate controls on
     * a preferene page may make it possible to adopt an inherited value for
     * a field (that is, to copy it into the field), after which it can be stored.
     *   
     * Also sets fieldModified to false (since it was just stored and hasn't
     * been changed yet) and levelFromWhichLoaded to the level associated with
     * the current field.  
     *  
     * @throws	IllegalStateException
     * 				if the preferences service is null, if the preferences
     * 				level is null, or if the associated level is the project
     * 				level but no project is set
     * 
     */
    public void store() {

    	// Don't store if the preferences service is null, since that may
    	// represent an illegal state and anyway we need to refer to it below
    	if (preferencesService == null) {
    		throw new IllegalStateException("SafairFieldEditor.store():  attempt to store when preferences service is null");
    	}
    	
    	// Can't store the value If there is no valid level (not having a preferences level
    	// isn't necessarily an error, but it does prevent storing)
        //if (preferencesLevel == null) return;
    	if (preferencesService == null) {
    		throw new IllegalStateException("SafairFieldEditor.store():  attempt to store when preferences level is null");
    	}

        // Don't store a value that comes from some other level
        if (isInherited) return;
        //if (presentsDefaultValue()) return;
        
        // Don't bother storing if the field hasn't been modified
        if (!fieldModified) return;
        
        // Don't store the value if the field's level is the project level
        // but no project is selected
        if (IPreferencesService.PROJECT_LEVEL.equals(preferencesLevel) &&
        	preferencesService.getProject() == null)
        {
       		throw new IllegalStateException("SafairFieldEditor.store():  attempt to store project preference when project is not set"); 
        }
        
        // If the level is the default level, go ahead and store it even
        // though preferences on the default level aren't persistent:
        // the preference still needs to be stored into the default preference
        // node (since that is needed to put the new value into effect, and
        // why provide a new value if you don't want it to go into effect?)
        // and the flushing of that node doesn't have any effect in any case.
        // In other words, do not return if the level is the default level

        // Store the value
        doStore();
        
        // If we've just stored the field, we've addressed any modifications
   		//System.out.println("STFE.store:  setting fieldModified to FALSE");
   		fieldModified = false;
   		levelFromWhichLoaded = preferencesLevel;  	
    }
    

    /**
     * Do the work of actually storing the value.
     * Generally depends on the specific type of the field.
     * May also need to check field-specific conditions (e.g., whether
     * empty values are allowed).
     * If necessary also adjust appearance of field on the preference
     * page to reflect the non-inherited state.
     */
    abstract protected void doStore();
    
//  // This is an example implementation from the IMP String field editor:
//  {
//    	String 	value = getTextControl().getText();
//    	boolean isEmpty = value.equals(emptyValue);			// Want empty value, but can't call method to retrieve it
//    														// with fields where empty is not allowed
//    	// getText() will return an empty string if the field is empty,
//    	// and empty strings can be stored in the preferences service,
//    	// but an empty string is recognized by the preferences service
//    	// as a valid value--when usually it is not.  Once it is recognized
//    	// as a valid value, it precludes the searching of subsequent
//    	// levels that might contain a non-empty (and actually valid) value.
//    	// We would like to be able to store a null value with the preferences
//    	// service so as to not short-circuit the search process, but we can't	
//    	// do that.  So, if the field value is empty, we have to eliminate the
//    	// preference entirely.  (Will that work in general???)
//    	if (isEmpty && !isEmptyStringAllowed()) {
//    		// We have an empty value where that isn't allowed, so clear the
//    		// preference.  Expect that clearing the preferences at a level will
//    		// trigger a loading with inheritance at that level
//    		preferencesService.clearPreferenceAtLevel(preferencesLevel, getPreferenceName());
//    		// If the preference value was previously empty (e.g., if previously inherited)
//    		// then clearing the preference node now doesn't cause a change event, so
//    		// doesn't trigger reloading with inheritance.  So we should just load the
//    		// field again to make sure any inheritance occurs if needed
//    		loadWithInheritance();
//    		return;
//    	}
//    	// Shouldn't need this check here now
//    	if (isInherited() && !fieldModified) {	
//    		// We have a value	 but it's inherited	
//    		// (left over from after the last time we cleared the field)
//    		// so don't need (or want) to store it	
//    		return;
//    	}
//    	// We have a value (possibly empty, if that is allowed) that has changed
//    	// from the previous value, so store it
//    	preferencesService.setStringPreference(preferencesLevel, getPreferenceName(), value);
//
//        // If we've just stored the field, we've addressed any modifications
//    	// Shouldn't need these here now
//   		fieldModified = false;
//   		levelFromWhichLoaded = preferencesLevel;
//   		// If we've stored the field then it's not inherited, so be sure it's
//   		// color indicates that.
//   		// For text fields, the background color is the backgroud color within
//   		// the field, so don't have to worry about matching anything else
//   		getTextControl().setBackground(PreferencesUtilities.colorWhite);
//    	
//    	IEclipsePreferences node = preferencesService.getNodeForLevel(preferencesLevel);
//    	try {
//    		if (node != null) node.flush();
//    	} catch (BackingStoreException e) {
//    		System.err.println("StringFieldEditor.	():  BackingStoreException flushing node;  node may not have been flushed:" + 
//    				"\n\tnode path = " + node.absolutePath() + ", preferences level = "  + preferencesLevel);
//    	}
//    }
    
    
    /*
     * Methods relating the preferences level associated with this field
     */

    
    /**
     * Set the preference level associated with this field to the given level.
     * 
     * @param	The string name of a preference level
     * @throws	IllegalArgumentException
     * 				if the given value does not denote a preference level			
     */
    public	void setPreferencesLevel(String level) {
    	if (!preferencesService.isaPreferencesLevel(level)) {
		throw new IllegalArgumentException("FieldEditor.setPreferencesLevel():  given level = " + level + " is invalid");
    	}
    	preferencesLevel = level;
    }
    
    
    /**
     * @return	The string name of the preference level associated with
     * 			this field
     */
    public String getPreferencesLevel() {
    	return preferencesLevel;	
    }
    
    
    /*
     * Subtypes will require methods to get and set the value of the field; these
     * will depend on the type of the value.
     * 
     * The method to set the value should set previousValue to the value
     * in effect at the time of the call, set fieldModified to true, set
     * levelFromWhichLoaded to the level associated with the field, and
     * call valueChanged().  It should also set presentsDefaultValue
     * according to whether the value set is the same as the value that
     * is set on the "default" level (regardless of whether the value has
     * been set from that level).
     */
    
    /*
     * Subtypes will also require methods to get the applicable UI Control.
     * These methods will be dependent on the type of the field, which will
     * determine the type of control (e.g., TextControl, ChangeControl).
     * 
     * The typical pattern for such methods is to check whether the control
     * exists and, if not, to create it, adding a ModifyListener and a
     * DisposeListener.
     * 
     * If the parent control (a Composite) is needed for obtaining the field's
     * control, then it is also advisable to check whether the paretn is disposed
     * (as attempts to get controls from a disposed parent typically fail).
     * 
     * A null value can be returned if no real control is found.
     */

    
    /**
     * @return	The parent control of this field
     */
    public Composite getParent() {
    	return parent;
    }
    
    
    
    /**
     * Should call the supertype method fireValueChanged() to inform this
     * field editor's listener, if it has one, about a change to the value
     * (<code>VALUE</code> property), provided that the old and new values
     * are different.
     * 
     * This hook is <em>not</em> called when the text is initialized 
     * (or reset to the default value) from the preference store.
     * 
     * The means of obtaining the current value (for comparison with the
     * old) will probably depend on the type of the field and its control.
     * 
     * This method should probably not adjust any of the fields associated
     * with the field editor, as those should have been set appropriately
     * at the point where valueChanged() was called.
     */
    abstract protected boolean valueChanged();
    
   
    /*
     * FieldEditor contains a method refreshValidState() that has an
     * empty implementation.  That method can be overridden in subtypes
     * to update the validity status of a value.
     */
 
    
    /**
     * A utility method to trigger the reevaluation of the
     * validity state of the preferences tab.
     * (Started out doing more; may not be as useful now as
     * it was once expected to be.)
     * Need to promote notification of preference page
     * from field to tab.
     */
    protected boolean notifyState(boolean state)
    {
//    	if (prefPage != null)
//    		prefPage.setValid(state);
    	if (prefTab != null)
    		prefTab.setValid(state);
    	
    	return state;
    }
 
    
    /*
     * Methods related to marking modified fields
     */
    
    
    public void setModifiedMarkOnLabel() {
// SMS 27 Nov 2006:  needed here?  should be up to caller
//    	if (isInherited) return;
        Label label = getLabelControl(parent);
        if (label != null) {
	        String labelText = label.getText();
	        if (!labelText.startsWith(Markings.MODIFIED_MARK)) {
		        labelText = Markings.MODIFIED_MARK + labelText;
		        label.setText(labelText);
	        }
        }
    }
    
    
    public void clearModifiedMarkOnLabel() {
        Label label = getLabelControl(parent);
        if (label != null) {
	        String labelText = label.getText();
	        if (labelText.startsWith(Markings.MODIFIED_MARK))
	        		labelText = labelText.substring(Markings.MODIFIED_MARK.length());
	        label.setText(labelText);
        }
    }
 
    
    
    /*
     * Methods related to error messages
     */
    
    protected void clearErrorMessage() {
    	prefTab.clearErrorMessages(this);
    }

    
    protected void setErrorMessage(String msg) {
    	prefTab.setErrorMessage(this, msg);
    }

    
	public boolean hasErrorMessage() {
		return prefTab.errorMessages.containsKey(this);
	}

	
	public String getFieldMessagePrefix() {
		return /*prefTab.getTabItem().getText() + " Tab:  " +*/ getLabelText() + ":  ";
	}
	
	
	public void doSetPresentsDefaultValue(boolean b) {
		setPresentsDefaultValue(b);
	}
	protected void setPresentsDefaultValue(boolean b) {
		super.setPresentsDefaultValue(b);
	}   
}
