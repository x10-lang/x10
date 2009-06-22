/*******************************************************************************
* Copyright (c) 2008 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

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
import java.util.List;

import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.Markings;
import org.eclipse.imp.preferences.PreferencesTab;
import org.eclipse.imp.preferences.PreferencesUtilities;
import org.eclipse.imp.preferences.TabbedPreferencesPage;
import org.eclipse.imp.preferences.fields.FieldEditor;
import org.eclipse.imp.preferences.fields.IntegerFieldEditor;
import org.eclipse.imp.x10dt.core.preferences.generated.X10PreferencesInstanceTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

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

		IntegerFieldEditor TabSize = fPrefUtils.makeNewIntegerField(
			page, tab, fPrefService,
			"instance", "TabSize", "Tab size",
			parent,
			true, true,
			true, String.valueOf(8),
			false, "0",
			true);
//			Link TabSizeDetailsLink = fPrefUtils.createDetailsLink(parent, TabSize, TabSize.getTextControl().getParent(), "Details ...");
			Label l = new Label(parent, 0);

		fields.add(TabSize);

		IntegerFieldEditor NumPlaces = fPrefUtils.makeNewIntegerField(
				page, tab, fPrefService,
				"instance", "NumPlaces", "Number of Places",
				parent,
				true, true,
				true, String.valueOf(8),
				false, "0",
				true);
//				Link NumPlacesDetailsLink = fPrefUtils.createDetailsLink(parent, NumPlaces, NumPlaces.getTextControl().getParent(), "Details ...");

		fields.add(NumPlaces);
			
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
		gl.numColumns = 2;
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
        fButtons = fPrefUtils.createDefaultAndApplyButtons(composite, this);
        Button defaultsButton = (Button) fButtons[0];
        Button applyButton = (Button) fButtons[1];
		
		return composite;
	}
}
