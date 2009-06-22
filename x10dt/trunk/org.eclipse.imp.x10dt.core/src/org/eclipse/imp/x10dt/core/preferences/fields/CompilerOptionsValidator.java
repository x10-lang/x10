package org.eclipse.imp.x10dt.core.preferences.fields;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.imp.preferences.fields.StringFieldEditor.Validator;

import polyglot.main.Main;
import polyglot.main.UsageError;
import polyglot.util.QuotedStringTokenizer;
import x10.config.ConfigurationError;
import x10.config.OptionError;

public class CompilerOptionsValidator implements Validator {
    public String validate(String value) {
       String argString = "";
        argString += value;
        argString += " ."; // need to specify at
        // least one file
        Set sources = new HashSet();
        sources.add(".");

        //String[] args = argString.split("\\s+");
        // Don't break args up on spaces only.  Consider things in quotes.
        // allows handling of e.g.  -report "my -very- weird report topic=42" 
        QuotedStringTokenizer st = new QuotedStringTokenizer(argString);
        String[] args = new String[st.countTokens()];
        for (int i = 0; i < args.length; i++)
          args[i] = st.nextToken();
        
        
        // TODO need api to list the possible options?  to have field specific content assist here
        polyglot.ext.x10.X10CompilerOptions options = new polyglot.ext.x10.X10CompilerOptions(null); // Doesn't actually need an ExtensionInfo unless you ask about the version current directory
        try {
            for(int i=0; i<args.length; i++) {
                if (args[i].length() == 0) {
                    continue;
                }
                // allow options that don't start with '-' - revisit this when options.checkCommand can be smarter???]
//                if (!args[i].startsWith("-") && !args[i].equals(".")) {
//                    throw new OptionError("Invalid option: " + args[i]);
//                }
                try {
                    options.checkCommand(args, i, sources);
                } catch (Main.TerminationException e) {
                    // Don't stop processing after -version
                }
            }
        } catch (OptionError e) {
            String msg = e.getMessage();
            // Inform the prefs UI component about the validation failure
            return e.getMessage();
        } catch (UsageError e) {
        } catch (ConfigurationError e) {
        }

        return null;
    }

}
