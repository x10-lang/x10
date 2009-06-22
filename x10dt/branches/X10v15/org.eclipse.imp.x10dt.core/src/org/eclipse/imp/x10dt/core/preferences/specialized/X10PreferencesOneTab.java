/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*    Matthew Kaplan (mmk@us.ibm.com) - specialization to show only one tab (to simplify options for novices/typical users)
*******************************************************************************/

package org.eclipse.imp.x10dt.core.preferences.specialized;

import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.InstancePreferencesTab;
import org.eclipse.imp.preferences.PreferencesInitializer;
import org.eclipse.imp.preferences.PreferencesTab;
import org.eclipse.imp.preferences.TabbedPreferencesPage;
import org.eclipse.imp.x10dt.core.X10Plugin;
import org.eclipse.imp.x10dt.core.preferences.generated.X10PreferencesInitializer;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.TabFolder;

public class X10PreferencesOneTab extends TabbedPreferencesPage implements IPropertyChangeListener{
	
    /** 
     * The first invalid field editor, or <code>null</code>
     * if all field editors are valid.
     */
	private FieldEditor invalidFieldEditor=null;
	
	public X10PreferencesOneTab () {
		super();
        prefService = X10Plugin.getInstance().getPreferencesService();
		setPreferenceStore(X10Plugin.getInstance().getPreferenceStore());

	}

	protected PreferencesTab[] createTabs(IPreferencesService prefService,
			TabbedPreferencesPage page, TabFolder tabFolder) {
		PreferencesTab[] tabs = new PreferencesTab[1];

		InstancePreferencesTab instanceTab = new X10PreferencesInstanceTabNoDetails(prefService);
		instanceTab.createTabContents(page, tabFolder);
		tabs[0] = instanceTab;

		return tabs;
	}

    public PreferencesInitializer getPreferenceInitializer() {
        return new X10PreferencesInitializer();
    }

	/**
     * The field editor preference page implementation of this <code>IPreferencePage</code>
     * (and <code>IPropertyChangeListener</code>) method intercepts <code>IS_VALID</code> 
     * events but passes other events on to its superclass.
     */
    public void propertyChange(PropertyChangeEvent event) {

        if (event.getProperty().equals(FieldEditor.IS_VALID)) {
            boolean newValue = ((Boolean) event.getNewValue()).booleanValue();
            // If the new value is true then we must check all field editors.
            // If it is false, then the page is invalid in any case.
            if (newValue) {
                checkState();
            } else {
                invalidFieldEditor = (FieldEditor) event.getSource();
                setValid(newValue);
            }
        }
    }

    /**
     * Recomputes the page's error state by calling <code>isValid</code> for
     * every field editor.
     */
    protected void checkState() {
    	// Valid if all fields, on all tabs, are valid.  Delegate to each tab and remember first offending field, if any.
        invalidFieldEditor = null;
        PreferencesTab tabs[] = getTabs();
        for (int i=0; i<tabs.length; i++) {
        	FieldEditor fe = ((X10PreferencesInstanceTabNoDetails)tabs[i]).doCheckState();
        	if (fe!=null) {
        		invalidFieldEditor = fe;
        		setValid(false);
        		return;
        	}
        }
        setValid(true);
     }
    
    public boolean performCancel()
    {
    	// SMS 4 Dec 2006
    	// Previously just used to return true; now trying to
    	// allow for a negative return
        PreferencesTab tabs[] = getTabs();
    	boolean result = true;
		for (int i = 0; i < tabs.length; i++) {
			if (tabs[i]==null) continue;
			result = result && tabs[i].performCancel();
		}

        return result;
    }


}
