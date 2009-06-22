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

import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesTab;
import org.eclipse.imp.x10dt.core.preferences.fields.CompilerOptionsStringFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;


public class PreferencesUtilities extends org.eclipse.imp.preferences.PreferencesUtilities {
	public PreferencesUtilities(IPreferencesService service)
	{
		super(service);
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
			addStringPropertyChangeListeners(service, level, field, key, fieldHolder);
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
}
