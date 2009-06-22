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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;


public class BooleanFieldEditor extends FieldEditor //BooleanFieldEditor
{
	/*
	 * Fields copied from BooleanFieldEditor
	 */
	
	/**
     * Style constant (value <code>0</code>) indicating the default
     * layout where the field editor's check box appears to the left
     * of the label.
     */
    public static final int DEFAULT = 0;

    /**
     * Style constant (value <code>1</code>) indicating a layout 
     * where the field editor's label appears on the left
     * with a check box on the right.
     */
    public static final int SEPARATE_LABEL = 1;

    /**
     * Style bits. Either <code>DEFAULT</code> or
     * <code>SEPARATE_LABEL</code>.
     */
    private int style;

    
    /**
     * The checkbox control, or <code>null</code> if none.
     */
    private Button checkBox = null;

	
	/**
     * Creates a boolean field editor in the given style.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param style the style, either <code>DEFAULT</code> or
     *   <code>SEPARATE_LABEL</code>
     * @param parent the parent of the field editor's control
     * @see #DEFAULT
     * @see #SEPARATE_LABEL
     */
    public BooleanFieldEditor(
			PreferencePage page, PreferencesTab tab,
    		IPreferencesService service, String level,
    		String name, String labelText, int style, final Composite parent)
    {
    	super(page, tab, service, level, name, labelText, parent);
    	this.style = style;
    	createControl(parent);
    }
	
    
    /**
     * Creates a boolean field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public BooleanFieldEditor(
		PreferencePage page, PreferencesTab tab,
   		IPreferencesService service, String level, String name, String labelText, Composite parent)
    {
        this(page, tab, service, level, name, labelText, DEFAULT, parent);
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
    


	/* (non-Javadoc)	
     * Method declared on FieldEditor.
     */
    protected void doLoad()
    {
        if (getChangeControl() != null) {	
	    	boolean value;
	    	if (preferencesLevel != null) {
	    		// The "normal" case, in which field corresponds to a preferences level
	    		value = preferencesService.getBooleanPreference(preferencesLevel, getPreferenceName());
	    		levelFromWhichLoaded = preferencesLevel;
	    		setInherited(false);
	    	}
	    	else {
	    		// Not normal, exactly, but possible if loading is being done into a
	    		// field that is not associated with a specific level
	    		value = preferencesService.getBooleanPreference(getPreferenceName());
	    		levelFromWhichLoaded = preferencesService.getApplicableLevel(getPreferenceName(), preferencesLevel);
				setInherited(true);
	    	}
        	setPresentsDefaultValue(IPreferencesService.DEFAULT_LEVEL.equals(levelFromWhichLoaded));
        	setBooleanValue(value);
        }
        
    }


 
    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doLoadDefault() {
        if (getChangeControl() != null) {
            boolean value = preferencesService.getBooleanPreference(IPreferencesService.DEFAULT_LEVEL, getPreferenceName());
        	levelFromWhichLoaded = IPreferencesService.DEFAULT_LEVEL;
            setInherited(false);	// We're putting the default value here directly, not inheriting it
            setPresentsDefaultValue(true);
            setBooleanValue(value);	// calls valueChanged();
        }
     }


    /* (non-Javadoc)
     * 
     */
    protected void doLoadLevel(String level) {
        if (getChangeControl() != null) {
        	boolean value;
        	if (preferencesLevel != null) {
        		value = preferencesService.getBooleanPreference(level, getPreferenceName());
        	} else {
        		value = preferencesService.getBooleanPreference(getPreferenceName());
        	}
         	// We're putting the level's value here directly, not inheriting it, so ...
        	levelFromWhichLoaded = level;
        	setInherited(false);
           	setPresentsDefaultValue(IPreferencesService.DEFAULT_LEVEL.equals(level));
        	setBooleanValue(value);	// calls valueChanged();
        }
    }


    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void refreshValidState() {
    	notifyState(true);
    }



	/*
     * Load into the boolean field the value for this preference that is either
     * the value defined on this preferences level, if any, or the value inherited
     * from the next applicable level, if any.  Return the level at which the
     * value loaded was found.  Load nothing and return null if no value is found.
     */
    protected String doLoadWithInheritance()
    {
    	String levelLoaded = null;
    	
    	String[] levels = IPreferencesService.levels;
    	int fieldLevelIndex = preferencesService.getIndexForLevel(preferencesLevel);
    		
    	// If we're loading with inheritance for some field that is
    	// not attached to a preferences level (such as the "applicable"
    	// field, which inherits values from all of the real fields)
    	// then assume that we should just search from the bottom up
    	String tmpPreferencesLevel = (preferencesLevel == null)?
    			levels[0] :
    			preferencesLevel;
     	
    	boolean value = false;
    	int levelAtWhichFound = -1;
    	
    	IEclipsePreferences[] nodes = preferencesService.getNodesForLevels();
    	
    	for (int level = fieldLevelIndex; level < levels.length; level++) {
    		// Must have a node from which to get a value
    		if (nodes[level] == null) continue;
    		
   			// Get the value from the node, not the service, because we can
    		// check the node to see whether there is a value there for this
    		// preference
    		String result = nodes[level].get(getPreferenceName(), null);
    		if (result != null) {
    			// We have a value at the node; get it as a boolean
    			// (presumably we could also convert the result to a boolean)
    			value = nodes[level].getBoolean(getPreferenceName(), false);
    			
    			levelAtWhichFound = level;
    			levelLoaded = levels[levelAtWhichFound];
    			break;
    		}
    	}

    	String previousLevelFromWhichLoaded = levelFromWhichLoaded; //mmk fix inappropriate change modifier set
   	
    	// Ok, now have all necessary information to set everyting that needs to be set
    	levelFromWhichLoaded = levelLoaded;
    	setInherited(fieldLevelIndex != levelAtWhichFound);
       	setPresentsDefaultValue(IPreferencesService.DEFAULT_LEVEL.equals(levelFromWhichLoaded));
       	setPreviousBooleanValue(getBooleanValue());
       	boolean valueChanged = previousValue==null || ((Boolean)previousValue).booleanValue()!=value;
       	boolean levelChanged = previousLevelFromWhichLoaded==null && levelFromWhichLoaded!=null || previousLevelFromWhichLoaded!=null && levelFromWhichLoaded==null || !previousLevelFromWhichLoaded.equals(levelFromWhichLoaded);
       	if (levelChanged || valueChanged) {
       		setBooleanValue(value);		// sets fieldModified and previousValue
       	}
   	
    	if (!isInherited())
    		getChangeControl().setBackground(PreferencesUtilities.colorWhite);
    	else
    		getChangeControl().setBackground(PreferencesUtilities.colorBluish);	
    	setPresentsDefaultValue(levelAtWhichFound == IPreferencesService.DEFAULT_INDEX);
   
        return levelLoaded;
    }


  
    protected void doStore()
    {				
    	boolean oldValue = preferencesService.getBooleanPreference(preferencesLevel, getPreferenceName());
    	boolean	newValue = getBooleanValue();
    	
    	// Not inherited, and modified:  field must have been set on this level, so store it.
    	// Storing here should trigger preference-change listeners at each level below this.
   		preferencesService.setBooleanPreference(preferencesLevel, getPreferenceName(), newValue);

        // If we've just stored the field, we've addressed any modifications
   		//System.out.println("SBFE.doStore:  setting fieldModified to FALSE");
   		fieldModified = (oldValue!=newValue);
   		if (fieldModified) {
   			getPreferenceStore().firePropertyChangeEvent(getPreferenceName(), oldValue, newValue);
   		}
   		
   		// The following copied from IntegerFieldEditor.. not sure whether needed.
   		levelFromWhichLoaded = preferencesLevel;
   		isInherited = false;
   		setPresentsDefaultValue(
   				newValue == preferencesService.getBooleanPreference(IPreferencesService.DEFAULT_LEVEL, getPreferenceName()));
  		// If we've stored the field then it's not inherited, so be sure it's
   		// color indicates that.
   		// Note that for the checkbox wiget (which is the only one used so far)
   		// the background color is the color behind the label (not the checkbox
   		// itself), so it should be light gray like the background in the rest
   		// of the tab.
   		// TODO:  figure out how to determine the actual prevailing background
   		// color and use that here
   		getChangeControl().setBackground(PreferencesUtilities.colorWhite);
    	
   		
    	// Now write out the node
    	IEclipsePreferences node = preferencesService.getNodeForLevel(preferencesLevel);

    	try {
    		if (node != null) node.flush();
    	} catch (BackingStoreException e) {
    		System.err.println("SBFE.doStore():  BackingStoreException 	;  node may not have been flushed:" + 
    				"\n\tnode path = " + node.absolutePath() + ", preferences level = "  + preferencesLevel);
    	}
    }
    
    
    /*
     * Preferences are stored by level, so we need to provide some
     * way to represent and set the applicable level.  Note that
     * preferences can be reset at the default level during exeuction
     * but default level preferences are never stored between
     * executions. 
     */

    	
    public	void setPreferencesLevel(String level) {
    	if (!preferencesService.isaPreferencesLevel(level)) {
    		throw new IllegalArgumentException("SafairBooleanFieldEditor.setPreferencesLevel:  given level = " + level + " is invalid");
    	}
    	if (level.equals(preferencesService.PROJECT_LEVEL) && preferencesService.getProject() == null) {
    		throw new IllegalStateException("SafairBooleanFieldEditor.setPreferenceLevel:  given level is '" + preferencesService.PROJECT_LEVEL +
    				"' but project is not defined for preferences service");
    	}
    	preferencesLevel = level;
    }
    
    
    public String getPreferencesLevel() {
    	return preferencesLevel;	
    }
    
    
    
    /**
     * Returns the field editor's value.
     *
     * @return the current value
     */
    public boolean getBooleanValue() {
            //return getChangeControl(parent).getSelection();
    	return getChangeControl().getSelection();
    }

    
    /**
     * Set the previous value for this field
     * @param value		The value to be set
     */
    
    protected void setPreviousBooleanValue(boolean value) {
    	previousValue = value ? Boolean.TRUE : Boolean.FALSE;
    }
    
    /**
     * Get the previous value for this field
     * @return	The previous value
     */
    protected boolean getPreviousBooleanValue() {
    	return ((Boolean)previousValue).booleanValue();
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
    public void setFieldValueFromOutside(boolean newValue) {
    	setPreviousBooleanValue(getBooleanValue());
    	setInherited(false);
    	setPresentsDefaultValue(false);
    	levelFromWhichLoaded = null;
    	setBooleanValue(newValue);
    }
    
    
    
    /**
     * Sets this field editor's value through the supertype.
     * Sets previous value and field modified.
     * Also calls valueChanged(..) to signal the change in
     * value.
     *
     * @param value 	the new value
     */
    protected void setBooleanValue(boolean newValue) {
    	Button button = getChangeControl();
        if (button != null && !button.isDisposed()) {
        		boolean currentValue = getBooleanValue();
        		if (previousValue == null)
        			setPreviousBooleanValue(!currentValue);
            	button.setSelection(newValue);
                fieldModified = true;
//                setModifiedMarkOnLabel();
                valueChanged();
        } else if (button.isDisposed()){
        	throw new IllegalStateException("BooleanFieldEditor.setBooleanValue:  button is disposed");
        } else if (button == null) {
        	throw new IllegalStateException("BooleanFieldEditor.setBooleanValue:  button is null");
        }
    }

    
    /**
     * Should be called whenever there is an update to the field,
     * regardless of whether the value has changed or not.
     * 
     * Informs this field editor's listener, if it has one, about a change
     * to the value (<code>VALUE</code> property) provided that the old and
     * new values are different.
     * 
     * Sets the "modified" mark on the fields label regardless of
     * whether the value has changed (on the assumption that the
     * field has, or may have, changed in some significant way.
     *
     * @param oldValue 	the old value
     * @param newValue 	the new value
     */
    protected boolean valueChanged() {
    	boolean changed = false;

    	boolean oldValue = getPreviousBooleanValue();
    	boolean newValue = getBooleanValue();
    	
        if (oldValue != newValue) {
            fireStateChanged(VALUE, oldValue, newValue);
            changed = true;
        }
        // Set modify mark in any case because field may
        // have changed, e.g., going from inherited to not
        // or vice versa, without the value changing
        setModifiedMarkOnLabel();
        return changed;
    }
    
//   public static final org.eclipse.swt.graphics.FontData changedFontData = new org.eclipse.swt.graphics.FontData("Monaco", 11, 2);
    private static final FontData[] arialFonts = PlatformUI.getWorkbench().getDisplay().getFontList("Arial", true);
    private static FontData[] labelFonts;
    
    /**
     * Initialize fonts for use in checkbox labels
     * @return true if appropriate fonts available on system; false if not.
     * @author mmk
     */
    private boolean getLabelFonts() {
    	if (labelFonts==null) {
    		labelFonts = new FontData[2];
	
	    	for (FontData fd: arialFonts) {
	    		if (fd.getHeight() <=10) {
	    			switch (fd.getStyle()) {
	    			case SWT.NORMAL: 
	    				labelFonts[0] = fd;
	    				break;
	    			case SWT.ITALIC:
	    				labelFonts[1] = fd;
	    				break;
	    			}
	    		}
	    	}
    	}
    	if (labelFonts[0]==null || labelFonts[1] == null) { // only vary font if reasonable fonts exist for both modified/unmodified
    		return false;
    	}
    	return true;
    	
    }

    /*
     * For boolean fields we override the following two methods because
     * the means of accessing the text to be modified is different.
     * @see org.eclipse.imp.preferences.fields.FieldEditor#setModifyMarkOnLabel()
     * @see org.eclipse.imp.preferences.fields.FieldEditor#clearModifyMarkOnLabel()
     */
    
    public void setModifiedMarkOnLabel() {
    	// SMS 27 Nov 2006
    	// Don't presume here to deal with inheritance.  If called then set mark.
    	// Let caller worry about whether field is inherited and how that affects
    	// the marking
    	// if (isInherited) return;
    	if (checkBox != null) {
//	        String labelText = checkBox.getText();
//	        if (!labelText.startsWith(Markings.MODIFIED_MARK)) {
//		        labelText = Markings.MODIFIED_MARK + labelText;
//		        checkBox.setText(labelText);
//	        }
        	// mmk replace changed mark by color to eliminate text-box overflow bug
			checkBox.setForeground(PreferencesUtilities.colorRed); // this doesn't work on MacOSX: use font if possible
	    	if (getLabelFonts()==false) return;
	    	checkBox.setFont(new Font(getPage().getShell().getDisplay(), labelFonts[1]));
    	}
    }

    
    public void clearModifiedMarkOnLabel() {
    	if (getLabelFonts()==false) return;
    	if (checkBox != null) {
//	        String labelText = checkBox.getText();
//	        if (labelText.startsWith(Markings.MODIFIED_MARK))
//	        		labelText = labelText.substring(1);
//	        checkBox.setText(labelText);
        	// mmk replace changed mark by color to eliminate text-box overflow bug
	        checkBox.setForeground(PreferencesUtilities.colorBlack); // this doesn't work on MacOSX: use font if possible
	    	if (getLabelFonts()==false) return;
	    	checkBox.setFont(new Font(getPage().getShell().getDisplay(), labelFonts[0]));

    	}
    }
 
    
     
    /*
     * Returns the change button for this field editor.
     * This overrides the corresponding superclass method so that we can set
     * a listener on the control for our purposes.
     * 
     */
    public Button getChangeControl() {
    	if (!parent.isDisposed()) {
    		if (checkBox == null) {
	        	// Should actually create checkbox if it doesn't exist
	        	// so should really never be null
	        	//checkBox = getChangeControl(parent);
                checkBox = new Button(parent, SWT.CHECK | SWT.LEFT);
                checkBox.setFont(parent.getFont());
	            checkBox.addSelectionListener(new SelectionAdapter() {
	                public void widgetSelected(SelectionEvent e) {
	                	// Whenever a new value is set, we have to record the previous value.
	                	// If we're here, that means that the current value has been changed
	                	// using the GUI.  Since the value in the GUI has changed, we can't
	                	// use that to retrieve the previous value.  But, since this is a
	                	// boolean field, we know that the previous value must have been the
	                	// negation of the current value, so we can set the previous value
	                	// from that.  It's important to set the previous value here before
	                	// calling valueChanged(), because valueChanged() can't assume that
	                	// a change has occurred--since a value loaded from the preferences
	                	// service may match the current value of the field--so valueChanged()
	                	// has to compare the new and previous values.  To make that comparison
	                	// work, we need to assure that the previous value is set properly here.
	                	setPreviousBooleanValue(!getBooleanValue());
	                	fieldModified = true;
	                	// Should call setInherited(..) before calling valueChanged() because
	                	// valueChanged() will mark the field as modified, but only if isInherited
	                	// is false, which it now should be
    					setInherited(false);
						levelFromWhichLoaded = preferencesLevel;
						setBooleanValue(getBooleanValue());
						// Set presentsDefaultValue to false on the basis that
						// we've set it independently of the encoded default value
						// even if we're on the default level.
				       	setPresentsDefaultValue(false);
	                	//valueChanged();
	                	//setPreviousBooleanValue(getBooleanValue());
	                }
	            });
	            checkBox.addDisposeListener(new DisposeListener() {
	                public void widgetDisposed(DisposeEvent event) {
	                    //System.out.println("SBFE.button dispose listener (from getChangeControl):  checkBoxNull set to true");
	                    checkBox = null;
	                }
	            });
    		} else {
	            checkParent(checkBox, parent);
	        }
	    	return checkBox; //getChangeControl(parent);
        }
        return null;
    }
    
   
    
    /*
     * Additional methods copied from BooleanFieldEditor
     */
    
    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void adjustForNumColumns(int numColumns) {
        if (style == SEPARATE_LABEL)
            numColumns--;
        ((GridData) checkBox.getLayoutData()).horizontalSpan = numColumns;
    }

    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        String text = getLabelText();
        switch (style) {
        case SEPARATE_LABEL:
            getLabelControl(parent);
            numColumns--;
            text = null;
        default:
            checkBox = getChangeControl();
            GridData gd = new GridData();
            gd.horizontalSpan = numColumns;
            checkBox.setLayoutData(gd);
            if (text != null)
                checkBox.setText(text);
        }
    }

    
    /* (non-Javadoc)
     * Method declared on FieldEditor.
     */
    public int getNumberOfControls() {
        switch (style) {
        case SEPARATE_LABEL:
            return 2;
        default:
            return 1;
        }
    }
    
    
  

}
