package org.eclipse.imp.x10dt.core.preferences.fields;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesTab;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;

import polyglot.main.UsageError;

public class CompilerOptionsStringFieldEditor extends StringFieldEditor {

	public CompilerOptionsStringFieldEditor(PreferencePage page,
			PreferencesTab tab, IPreferencesService service, String level,
			String name, String labelText, Composite parent) {
		super(page, tab, service, level, name, labelText, parent);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected boolean doCheckState() {
		try {
			String argString = "";
			argString += getStringValue();
			argString += " ."; // need to specify at
								// least one file
			Set sources = new HashSet();
			sources.add(".");

			polyglot.main.Options options = new polyglot.ext.x10.X10CompilerOptions(null); // Doesn't actually need an ExtensionInfo unless you ask about the version current directory
			String[] args = argString.split(" ");
			options.parseCommandLine(args, sources);
		} catch (UsageError e) {
			String msg = e.getMessage();
			// Inform the prefs UI component about the validation failure
			setErrorMessage(e.getMessage());
			return false;
		}
		return super.doCheckState();
	}

}
