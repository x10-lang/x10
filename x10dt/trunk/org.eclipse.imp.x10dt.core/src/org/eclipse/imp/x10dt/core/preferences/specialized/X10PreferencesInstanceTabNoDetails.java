/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Matthew Kaplan (mmk@us.ibm.com) - initial implementation
*******************************************************************************/

/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*    Matthew Kaplan (mmk@us.ibm.com) - specialization to minimize fields/eliminate details
*******************************************************************************/

package org.eclipse.imp.x10dt.core.preferences.specialized;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.Markings;
import org.eclipse.imp.preferences.PreferenceCache;
import org.eclipse.imp.preferences.PreferenceConstants;
import org.eclipse.imp.preferences.TabbedPreferencesPage;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.imp.x10dt.core.preferences.PreferencesTab;
import org.eclipse.imp.x10dt.core.preferences.PreferencesUtilities;
import org.eclipse.imp.x10dt.core.preferences.fields.FieldEditor;
import org.eclipse.imp.x10dt.core.preferences.fields.FontFieldEditor;
import org.eclipse.imp.x10dt.core.preferences.fields.IntegerFieldEditor;
import org.eclipse.imp.x10dt.core.preferences.generated.X10PreferencesInstanceTab;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;

public class X10PreferencesInstanceTabNoDetails extends
		X10PreferencesInstanceTab {

	public X10PreferencesInstanceTabNoDetails(IPreferencesService prefService) {
		super(prefService);
	}
	
	/**
	 * Creates specific preference fields with settings appropriate to
	 * the instance preferences level.
	 *
	 * Overrides an unimplemented method in PreferencesTab.
	 *
	 * @return    An array that contains the created preference fields
	 *
	 */
	protected FieldEditor[] createFields(
		TabbedPreferencesPage page, PreferencesTab tab, String tabLevel,
		Composite parent)
	{
		List fields = new ArrayList();

		IntegerFieldEditor TabSize = fPrefUtils_x10.makeNewIntegerField(
			page, tab, fPrefService,
			"instance", PreferenceConstants.P_TAB_WIDTH, "Tab size",
			parent,
			true, true,
			true, String.valueOf(8),
			false, "0",
			true);
//			Link TabSizeDetailsLink = fPrefUtils.createDetailsLink(parent, TabSize, TabSize.getTextControl().getParent(), "Details ...");

		fields.add(TabSize);

		IntegerFieldEditor NumPlaces = fPrefUtils_x10.makeNewIntegerField(
				page, tab, fPrefService,
				"instance", "NumPlaces", "Number of Places",
				parent,
				true, true,
				true, String.valueOf(8),
				false, "0",
				true);
//				Link NumPlacesDetailsLink = fPrefUtils.createDetailsLink(parent, NumPlaces, NumPlaces.getTextControl().getParent(), "Details ...");

		fields.add(NumPlaces);
			
		FontFieldEditor fontField= fPrefUtils_x10.makeNewFontField(
				page, tab, fPrefService,
				"instance", "x10Font", "Source font:",
				null,
				parent,
				true, false,
				null, true);
		fields.add(fontField);
		
		page.getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
		    public void propertyChange(PropertyChangeEvent event) {
			IPreferenceStore prefStore= RuntimePlugin.getInstance().getPreferenceStore();
			// Hack: Forward the property change to the global IMP preference store, until
			// it supports language-specific settings for editor characteristics like font,
			// tab size, etc.
			if (event.getProperty().equals(PreferenceConstants.P_TAB_WIDTH)) {
			    prefStore.setValue(PreferenceConstants.P_TAB_WIDTH, Integer.parseInt((String) event.getNewValue()));
			} else if (event.getProperty().equals("x10Font")) {
			    PreferenceConverter.setValue(prefStore, PreferenceConstants.P_SOURCE_FONT, (FontData[]) event.getNewValue());
			}
		    }
		});

		FieldEditor[] fieldsArray = new FieldEditor[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			fieldsArray[i] = (FieldEditor) fields.get(i);
		}
		return fieldsArray;
	}

    public Composite createInstancePreferencesTab(TabbedPreferencesPage page, final TabFolder tabFolder) {
		
        fPrefPage = page;

        final Composite composite= new Composite(tabFolder, SWT.NONE);
        composite.setFont(tabFolder.getFont());
        final GridData gd= new GridData(SWT.FILL, SWT.CENTER, true, false);
        gd.widthHint= 0;
        gd.heightHint= SWT.DEFAULT;
        gd.horizontalSpan= 1;
        composite.setLayoutData(gd);
		
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		composite.setLayout(gl);
		
		fTabItem = new TabItem(tabFolder, SWT.NONE);
		fTabItem.setText("Workspace");
		fTabItem.setControl(composite);
		PreferencesTab.TabSelectionListener listener = 
			new PreferencesTab.TabSelectionListener(fPrefPage, fTabItem);
		tabFolder.addSelectionListener(listener);
		
		
		/*
		 * Add the elements relating to preferences fields and their associated "details" links.
		 */	
		fFields = createFields(page, this, IPreferencesService.INSTANCE_LEVEL, composite);

		
		PreferencesUtilities.fillGridPlace(composite, 2);
		

		// Don't want newly created fields to be flagged as modified
		clearModifiedMarksOnLabels();
		
		
		// Put notes on bottom
		
		final Composite bottom = new Composite(composite, SWT.BOTTOM | SWT.WRAP);
        GridLayout layout = new GridLayout();
        bottom.setLayout(layout);
        bottom.setLayoutData(new GridData(SWT.BOTTOM));
        
        Label bar = new Label(bottom, SWT.WRAP);
        GridData data = new GridData();
        data.verticalAlignment = SWT.WRAP;
        bar.setLayoutData(data);
        bar.setText(Markings.MODIFIED_NOTE + "\n\n" +
        			Markings.TAB_ERROR_NOTE);
        
		PreferencesUtilities.fillGridPlace(bottom, 1);
		
		 
		// Put buttons on the bottom
        fButtons = fPrefUtils_x10.createDefaultAndApplyButtons(composite, this);
        Button defaultsButton = (Button) fButtons[0];
        Button applyButton = (Button) fButtons[1];
		
		return composite;
	}
	/** 
	 * The field editor preference page implementation of this 
	 * <code>PreferencePage</code> method saves all field editors by
	 * calling <code>FieldEditor.store</code>. Note that this method
	 * does not save the preference store itself; it just stores the
	 * values back into the preference store.
	 *
	 * @see FieldEditor#store()
	 */
	public boolean performOk() {
	    if (fFields != null) {
	    	for (int i=0; i<fFields.length; i++) {
	            FieldEditor pe = (FieldEditor) fFields[i];
	            pe.store();
	            pe.doSetPresentsDefaultValue(false);
	        }
	    }
	    return true;
	}
	
	/**
	 * Recomputes the tab's error state by calling <code>isValid</code> for
	 * every field editor.
	 */
	FieldEditor doCheckState() {
        boolean valid = true;
        FieldEditor invalidFieldEditor = null;
        // The state can only be set to true if all
        // field editors contain a valid value. So we must check them all
        if (fFields != null) {
        	for (int i=0; i<fFields.length; i++) {
                FieldEditor editor = (FieldEditor) fFields[i];
                if (editor!=null) {
                	valid = valid && editor.isValid();
                	if (!valid) {
                		invalidFieldEditor = editor;
                		break;
                	}
                }
        	}
        }
        setValid(valid);
        return invalidFieldEditor;
	}
}
