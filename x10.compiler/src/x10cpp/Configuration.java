/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10cpp;

import x10.config.ConfigurationError;
import x10.config.OptionError;

/**
 * This class provides the configuration for the X10 C++ compiler backend.
 * The configuration is a set of values that can be used to
 * configure the compiler, for example in order to tune performance
 * of the generated code.
 *
 * @see x10.config.Configuration
 */
public final class Configuration extends x10.config.Configuration {

    public String MAIN_STUB_NAME = "xxx_main_xxx";
    private static final String MAIN_STUB_NAME_desc = "The name for the main invocation stub file";

    public boolean VIM_MODELINE = true;
    private static final String VIM_MODELINE_desc = "Generate a modeline (formatting instructions) for VIm";

	/**
	 * Parses one argument from the command line.  This allows the user
	 * to specify options also on the command line (in addition to the
	 * configuration file and the defaults).
	 *
	 * @param arg the current argument, e.g., -STATISTICS_DISABLE=all
	 * @throws OptionError if the argument is not recognized
	 * @throws ConfigurationError if there was a problem processing the argument
	 */
	public void parseArgument(String arg) throws OptionError, ConfigurationError {
		parseArgument(this, Configuration.class, arg);
	}

	public Object get(String key) throws ConfigurationError, OptionError {
	    return get(this, Configuration.class, key);
	}

	/**
	 * Return an array of (option,description) pairs.
	 */
	public String[][] options() {
		return options(this, Configuration.class);
	}

	public Configuration() {
	    super(Configuration.class);
	}
	
	public Configuration(String cfg) {
	    super(Configuration.class, cfg);
	}
}

