package org.eclipse.imp.x10dt.core.preferences.fields;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesTab;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;

import polyglot.ext.x10.Configuration;
import polyglot.main.Main;
import polyglot.main.UsageError;
import x10.runtime.util.OptionsError;

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

			String[] args = argString.split(" ");
			parseCommandLine(args, sources);
		} catch (UsageError e) {
			String msg = e.getMessage();
			// Inform the prefs UI component about the validation failure
			setErrorMessage(e.getMessage());
			return false;
		} catch (OptionsError e) {
			String msg = e.getMessage();
			// Inform the prefs UI component about the validation failure
			setErrorMessage(e.getMessage());
			return false;
		}
		return super.doCheckState();
	}
	
    /**
     * Copied from polyglot.main to permit handling of new exception: OptionsError, introduced for x10dt error handling
     * Parse the command line
     * 
     * @throws UsageError if the usage is incorrect.
     */
    private void parseCommandLine(String args[], Set source) throws UsageError, OptionsError {
        if(args.length < 1) {
            throw new UsageError("No command line arguments given");
        }
		polyglot.ext.x10.X10CompilerOptions options = new polyglot.ext.x10.X10CompilerOptions(null); // Doesn't actually need an ExtensionInfo unless you ask about the version current directory
    
        for(int i = 0; i < args.length; ) {
            try {
                int ni = options.parseCommandForOptionsChecking(args, i, source);                
                if (ni == i) {
                    throw new UsageError("illegal option -- " + args[i]);
                }
                
                i = ni;

            }
            catch (ArrayIndexOutOfBoundsException e) {
                throw new UsageError("missing argument");
            }
            catch (Main.TerminationException e) {
            	i++;
            }
        }
                    
        if (source.size() < 1) {
          throw new UsageError("must specify at least one source file");
        }
    }
    

}
