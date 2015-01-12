/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
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
import polyglot.util.CollectionUtil;
import x10.util.CollectionFactory;
import x10.config.ConfigurationError;
import x10.config.OptionError;

public class X10CPPCompilerOptions extends x10.X10CompilerOptions {

    public final Configuration x10cpp_config;
    
    /**
     * Enable gprof-style profiling by passing -pg to the 
     * post compiler & linker.
     */
    public boolean pg = false;
    
    /**
     * Enable profiling with google performance tools
     */
    public boolean gpt = false;

    public final List<String> extraPreArgs = new ArrayList<String>();
    public final List<String> extraPostArgs = new ArrayList<String>();
    
    public X10CPPCompilerOptions(ExtensionInfo extension) {
        super(extension);
        x10cpp_config = new Configuration();
    }

    protected int parseCommand(String args[], int index, Set<String> source) 
        throws UsageError, Main.TerminationException
    {
        int i = super.parseCommand(args, index, source);
        if (i != index) return i;
        
        if (args[i].equals("-pg")) {
            pg = true;
            return ++i;
        }
        
        if (args[i].equals("-gpt")) {
            macros.add("__GPT__");
            gpt = true;
            return ++i;
        }

        if (args[i].equals("-cxx-prearg")) {
            extraPreArgs.add(args[++i]);
            return ++i;
        }
 
        if (args[i].equals("-cxx-postarg")) {
            extraPostArgs.add(args[++i]);
            return ++i;
        }

        // FIXME: [IP] allow overriding super's option processing
        try {
            x10cpp_config.parseArgument(args[index]);
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
		
        usageForFlag(out, "-pg", "generate code with additional instrumentation to write profile data in gprof format");
        usageForFlag(out, "-gpt", "link the google perftools library to the generated executable");
        usageForFlag(out, "-post <compiler>", 
                     "run a C++ compiler after translation.  " +
                     "The structure of <compiler> is " +
                     "\"[pre-command with options (usually g++)] " +
                     "[(#|%) [post-options (usually extra files)] " +
                     "[(#|%) [library options]]]\".  " +
                     "Using '%' instead of '#' to delimit a section will cause " +
                     "the default values in that section to be omitted.");
        usageForFlag(out, "-cxx-prearg <arg>", "Add <arg> to the C++ compilation command line before the list of files");
        usageForFlag(out, "-cxx-postarg <arg>", "Add <arg> to the C++ compilation command line after the list of files");
        usageForFlag(out, "-c", "compile only to .cc");


		String[][] options = x10cpp_config.options();
		for (int i = 0; i < options.length; i++) {
			String[] optinfo = options[i];
			String optflag = "-"+optinfo[0]+"="+optinfo[1];
			String optdesc = optinfo[2]+"(default = "+optinfo[3]+")";
			usageForFlag(out, optflag, optdesc);
		}
	}
	
	private Set<String> compilationUnits = CollectionFactory.newHashSet();
    
	public Set<String> compilationUnits() { return compilationUnits; }	
}
