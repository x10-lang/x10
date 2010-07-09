/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10cpp;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import polyglot.frontend.ExtensionInfo;
import polyglot.main.Main;
import polyglot.main.UsageError;
import x10.config.ConfigurationError;
import x10.config.OptionError;

public class X10CPPCompilerOptions extends x10.X10CompilerOptions {

    public String executable_path = null;

    public X10CPPCompilerOptions(ExtensionInfo extension) {
        super(extension);
    }

    protected int parseCommand(String args[], int index, Set source) 
        throws UsageError, Main.TerminationException
    {
        int i = super.parseCommand(args, index, source);
        if (i != index) return i;

        if (args[i].equals("-o")) {
            index++;
            executable_path = args[index];
            index++;
            return index;
        }

        // FIXME: [IP] allow overriding super's option processing
        try {
            Configuration.parseArgument(args[index]);
            return ++index;
        }
        catch (OptionError e) { }
        catch (ConfigurationError e) { }
        return index;
	}

	/**
	 * Print usage information
	 */
	public void usage(PrintStream out) {
		super.usage(out);
        usageForFlag(out, "-o path", "set generated executable path (for the post-compiler)");
		String[][] options = Configuration.options();
		for (int i = 0; i < options.length; i++) {
			String[] optinfo = options[i];
			String optflag = "-"+optinfo[0]+"="+optinfo[1];
			String optdesc = optinfo[2]+"(default = "+optinfo[3]+")";
			usageForFlag(out, optflag, optdesc);
		}
	}
	
	/**
	 * Override usage info for the -post flag.
	 * @see polyglot.main.Options#usageForFlag(java.io.PrintStream, java.lang.String, java.lang.String)
	 */
	protected void usageForFlag(PrintStream out, String flag, String description) {
	    if (flag.startsWith("-post ")) {
	        flag = "-post <compiler>";
	        description = "run a C++ compiler after translation.  " +
	                      "The structure of <compiler> is " +
	                      "\"[pre-command with options (usually g++)] " +
	                      "[(#|%) [post-options (usually extra files)] " +
	                      "[(#|%) [library options]]]\".  " +
	                      "Using '%' instead of '#' to delimit a section will cause " +
	                      "the default values in that section to be omitted.";
	    }
	    super.usageForFlag(out, flag, description);
	}
	
	private Set<String> compilationUnits = new HashSet<String>();
    
	public Set<String> compilationUnits() { return compilationUnits; }	
}
