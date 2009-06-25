// Licensed Materials - Property of IBM
// (C) Copyright IBM Corporation 2004,2005,2006. All Rights Reserved. 
// Note to U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP  Schedule Contract with IBM Corp. 
//                                                                             
// --------------------------------------------------------------------------- 


/*
 */
package polyglot.ext.x10;

import java.io.PrintStream;
import java.util.Set;
import polyglot.frontend.ExtensionInfo;
import polyglot.main.Main;
import polyglot.main.UsageError;
import x10.runtime.util.ConfigurationError;
import x10.runtime.util.OptionError;

public class X10CompilerOptions extends polyglot.main.Options {

	public X10CompilerOptions(ExtensionInfo extension) {
		super(extension);
	}

	protected int parseCommand(String args[], int index, Set source) 
		throws UsageError, Main.TerminationException
	{
		int i = super.parseCommand(args, index, source);
		if (i != index) return i;

		try {
			Configuration.parseArgument(args[index]);
			return ++index;
		}
		catch (OptionError e) { }
		catch (ConfigurationError e) { }
		return index;
	}

	public int checkCommand(String args[], int index, Set source)
		throws UsageError, Main.TerminationException, OptionError, ConfigurationError
	{
		int i = super.parseCommand(args, index, source);
		if (i != index) return i;
		
		Configuration.parseArgument(args[index]);
		return ++index;
	}

	/**
	 * Print usage information
	 */
	public void usage(PrintStream out) {
		super.usage(out);
		String[][] options = Configuration.options();
		for (int i = 0; i < options.length; i++) {
			String[] optinfo = options[i];
			String optflag = "-"+optinfo[0]+"="+optinfo[1];
			String optdesc = optinfo[2]+"(default = "+optinfo[3]+")";
			usageForFlag(out, optflag, optdesc);
		}
	}
}
