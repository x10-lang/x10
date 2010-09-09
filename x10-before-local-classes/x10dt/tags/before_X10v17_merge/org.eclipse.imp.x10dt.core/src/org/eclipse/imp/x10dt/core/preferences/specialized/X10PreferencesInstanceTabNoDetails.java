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

package org.eclipse.imp.x10dt.core.preferences.specialized;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.InstancePreferencesTab;
import org.eclipse.imp.preferences.Markings;
import org.eclipse.imp.preferences.PreferenceConstants;
import org.eclipse.imp.preferences.PreferencesTab;
import org.eclipse.imp.preferences.PreferencesUtilities;
import org.eclipse.imp.preferences.TabbedPreferencesPage;
import org.eclipse.imp.preferences.fields.BooleanFieldEditor;
import org.eclipse.imp.preferences.fields.FieldEditor;
import org.eclipse.imp.preferences.fields.FontFieldEditor;
import org.eclipse.imp.preferences.fields.IntegerFieldEditor;
import org.eclipse.imp.preferences.fields.StringFieldEditor;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.imp.x10dt.core.X10PreferenceConstants;
import org.eclipse.imp.x10dt.core.preferences.fields.CompilerOptionsValidator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class X10PreferencesInstanceTabNoDetails extends InstancePreferencesTab {
    protected PreferencesUtilities fPrefUtils;

    IntegerFieldEditor TabSize;
    FontFieldEditor  fontField;
    BooleanFieldEditor badPlaceRuntimeCheckCompilerOption;
    BooleanFieldEditor loopOptimizationsCompilerOption;
    BooleanFieldEditor arrayOptimizationsCompilerOption;
    BooleanFieldEditor assertCompilerOption;
    StringFieldEditor additionalCompilerOptions;
    IntegerFieldEditor NumPlaces;

	public X10PreferencesInstanceTabNoDetails(IPreferencesService prefService) {
		super(prefService, true);
        fPrefUtils = new PreferencesUtilities(prefService);
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
	protected FieldEditor[] createFields(TabbedPreferencesPage page, Composite parent)
	{
		List<FieldEditor> fields = new ArrayList<FieldEditor>();

		// EDITOR OPTIONS
		
//		fPrefService.setIntPreference(IPreferencesService.DEFAULT_LEVEL, PreferenceConstants.P_TAB_WIDTH, 4);
		fPrefService.setStringPreference(IPreferencesService.DEFAULT_LEVEL, PreferenceConstants.P_TAB_WIDTH, "4");
		TabSize = fPrefUtils.makeNewIntegerField(
			page, this, fPrefService,
			"instance", PreferenceConstants.P_TAB_WIDTH, "Tab size", null,
			parent,
			true, true,
			true, String.valueOf(8),
			false, "4",
			true);
		fields.add(TabSize);


		fontField= fPrefUtils.makeNewFontField(
				page, this, fPrefService,
				"instance", PreferenceConstants.P_SOURCE_FONT, "Source font:",
				null,
				parent,
				true, false,
				true);
		fields.add(fontField);
		

		// COMPILER OPTIONS
		
		// -BAD_PLACE_RUNTIME_CHECK=boolean
		fPrefService.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10PreferenceConstants.P_BAD_PLACE_CHECK, true);
		badPlaceRuntimeCheckCompilerOption = fPrefUtils.makeNewBooleanField(page, this, fPrefService, "instance", X10PreferenceConstants.P_BAD_PLACE_CHECK, "Bad Place Runtime Check", null, parent, true, true, false, false, true, true, false);
		fields.add(badPlaceRuntimeCheckCompilerOption);

		// -LOOP_OPTIMIZATIONS=boolean
		fPrefService.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10PreferenceConstants.P_LOOP_OPTIMIZATIONS, true);
		loopOptimizationsCompilerOption = fPrefUtils.makeNewBooleanField(page, this, fPrefService, "instance", X10PreferenceConstants.P_LOOP_OPTIMIZATIONS, "Loop Optimizations", null, parent, true, true, false, false, true, true, false);
		fields.add(loopOptimizationsCompilerOption);

		// -ARRAY_OPTIMIZATIONS=boolean
		fPrefService.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10PreferenceConstants.P_ARRAY_OPTIMIZATIONS, true);
		arrayOptimizationsCompilerOption = fPrefUtils.makeNewBooleanField(page, this, fPrefService, "instance", X10PreferenceConstants.P_ARRAY_OPTIMIZATIONS, "Array Optimizations", null, parent, true, true, false, false, true, true, false);
		fields.add(arrayOptimizationsCompilerOption);

		// -assert
		fPrefService.setBooleanPreference(IPreferencesService.DEFAULT_LEVEL, X10PreferenceConstants.P_ASSERT, true);
		assertCompilerOption = fPrefUtils.makeNewBooleanField(page, this, fPrefService, "instance", X10PreferenceConstants.P_ASSERT, "Permit 'assert' Keyword", null, parent, true, true, false, false, true, true, false);
		fields.add(assertCompilerOption);
		
		// additional compiler options (string field)
		fPrefService.setStringPreference(IPreferencesService.DEFAULT_LEVEL, X10PreferenceConstants.P_ADDITIONAL_COMPILER_OPTIONS, "");
		additionalCompilerOptions = fPrefUtils.makeNewStringField(
				page, this, fPrefService,
				"instance", X10PreferenceConstants.P_ADDITIONAL_COMPILER_OPTIONS, "Additional Compiler Options:", null,
				parent,
				true, true,
				false, null,
				true, null,
				true);
		additionalCompilerOptions.setValidator(new CompilerOptionsValidator());
		fields.add(additionalCompilerOptions);

		// RUNTIME OPTIONS
		
//		fPrefService.setIntPreference(IPreferencesService.DEFAULT_LEVEL, X10PreferenceConstants.P_NUM_PLACES, 4);
		fPrefService.setStringPreference(IPreferencesService.DEFAULT_LEVEL, X10PreferenceConstants.P_NUM_PLACES, "4");
		NumPlaces = fPrefUtils.makeNewIntegerField(
				page, this, fPrefService,
				"instance", X10PreferenceConstants.P_NUM_PLACES, "Number of Places", null,
				parent,
				true, true,
				true, String.valueOf(8),
				false, "4",
				true);
		fields.add(NumPlaces);
		
		
		page.getPreferenceStore().addPropertyChangeListener(
			new IPropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent event) {
					IPreferenceStore prefStore = RuntimePlugin
							.getInstance().getPreferenceStore();
					// Hack: Forward property change to global IMP
					// preference store
					if (event.getProperty().equals(PreferenceConstants.P_TAB_WIDTH)) {
						prefStore.setValue(PreferenceConstants.P_TAB_WIDTH, (Integer) event.getNewValue());
					} else if (event.getProperty().equals(PreferenceConstants.P_SOURCE_FONT)) {
						PreferenceConverter.setValue(prefStore, PreferenceConstants.P_SOURCE_FONT, (FontData[]) event.getNewValue());
					} else if (event.getProperty().equals(X10PreferenceConstants.P_NUM_PLACES)) {
						prefStore.setValue(X10PreferenceConstants.P_NUM_PLACES, (Integer) event.getNewValue());
					} else if (event.getProperty().equals(X10PreferenceConstants.P_BAD_PLACE_CHECK)) {
						prefStore.setValue(X10PreferenceConstants.P_BAD_PLACE_CHECK, (Boolean) event.getNewValue());
					} else if (event.getProperty().equals(X10PreferenceConstants.P_LOOP_OPTIMIZATIONS)) {
						prefStore.setValue(X10PreferenceConstants.P_LOOP_OPTIMIZATIONS, (Boolean) event.getNewValue());
					} else if (event.getProperty().equals(X10PreferenceConstants.P_ARRAY_OPTIMIZATIONS)) {
						prefStore.setValue(X10PreferenceConstants.P_ARRAY_OPTIMIZATIONS, (Boolean) event.getNewValue());
					} else if (event.getProperty().equals(X10PreferenceConstants.P_ASSERT)) {
						prefStore.setValue(X10PreferenceConstants.P_ASSERT, (Boolean) event.getNewValue());
					} else if (event.getProperty().equals(X10PreferenceConstants.P_ADDITIONAL_COMPILER_OPTIONS)) {
						prefStore.setValue(X10PreferenceConstants.P_ADDITIONAL_COMPILER_OPTIONS, (String) event.getNewValue());
					}
				}
			});


		FieldEditor[] fieldsArray = new FieldEditor[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			fieldsArray[i] = (FieldEditor) fields.get(i);
		}
		return fieldsArray;
	}

    public Composite createTabContents(TabbedPreferencesPage page, final TabFolder tabFolder) {
		
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
		fFields = createFields(page, composite);

		
		PreferencesUtilities.fillGridPlace(composite, 2);
		

		// Don't want newly created fields to be flagged as modified
		clearModifiedMarksOnLabels();
		
		
		// Put notes on bottom
		
		final Composite bottom = new Composite(composite, SWT.BOTTOM | SWT.WRAP);
		final String X10_MODIFIED_NOTE = "Modified fields are labeled in red.";
        GridLayout layout = new GridLayout();
        bottom.setLayout(layout);
        bottom.setLayoutData(new GridData(SWT.BOTTOM));
        
        Label bar = new Label(bottom, SWT.WRAP);
        GridData data = new GridData();
        data.verticalAlignment = SWT.WRAP;
        bar.setLayoutData(data);
        bar.setText(X10_MODIFIED_NOTE + "\n\n" +
        			Markings.TAB_ERROR_NOTE);
        
		PreferencesUtilities.fillGridPlace(bottom, 1);
		
		 
		// Put buttons on the bottom
        fButtons = fPrefUtils.createDefaultAndApplyButtons(composite, this);
		
		return composite;
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
	
	public void performDefaults() {
		super.performDefaults();
		// change properties to cause propagate defaults -- why doesn't this get done in performDefaults?  It seems it does load but no store?
		IPreferenceStore prefStore = RuntimePlugin.getInstance().getPreferenceStore();
		prefStore.setValue(PreferenceConstants.P_TAB_WIDTH, new Integer(TabSize.getIntValue()));
		
		// font has no default
//		PreferenceConverter.setValue(prefStore, PreferenceConstants.P_SOURCE_FONT, (FontData[]) event.getNewValue());
		
		prefStore.setValue(X10PreferenceConstants.P_NUM_PLACES, new Integer(NumPlaces.getIntValue()));
		prefStore.setValue(X10PreferenceConstants.P_BAD_PLACE_CHECK, badPlaceRuntimeCheckCompilerOption.getBooleanValue());
		prefStore.setValue(X10PreferenceConstants.P_LOOP_OPTIMIZATIONS, loopOptimizationsCompilerOption.getBooleanValue());
		prefStore.setValue(X10PreferenceConstants.P_ARRAY_OPTIMIZATIONS, arrayOptimizationsCompilerOption.getBooleanValue());
		prefStore.setValue(X10PreferenceConstants.P_ASSERT, assertCompilerOption.getBooleanValue());
		prefStore.setValue(X10PreferenceConstants.P_ADDITIONAL_COMPILER_OPTIONS, additionalCompilerOptions.getStringValue());
	}
}
