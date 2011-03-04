package org.eclipse.imp.x10dt.core.preferences.fields;

import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesTab;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;

public class ComboFieldEditor extends
		org.eclipse.imp.preferences.fields.ComboFieldEditor {

	public ComboFieldEditor(PreferencePage page, PreferencesTab tab,
			IPreferencesService service, String level, String name,
			String labelText, String[][] entryNamesAndValues, Composite parent,
			boolean isEnabled, boolean hasSpecialValue, String specialValue,
			boolean isRemovable) {
		super(page, tab, service, level, name, labelText, entryNamesAndValues,
				parent, isEnabled, hasSpecialValue, specialValue, isRemovable);
		// TODO Auto-generated constructor stub
	}

}
