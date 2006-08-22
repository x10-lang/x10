// Licensed Materials - Property of IBM
// (C) Copyright IBM Corporation 2004,2005,2006. All Rights Reserved.
// Note to U.S. Government Users Restricted Rights:  Use, duplication or
// disclosure restricted by GSA ADP  Schedule Contract with IBM Corp.
//
// ---------------------------------------------------------------------------

/*
 * Created on Sep 28, 2004
 */
package polyglot.ext.x10;

import x10.runtime.util.ConfigurationError;

/**
 * This class provides the configuration for the X10 compiler.
 * The configuration is a set of values that can be used to
 * configure the compiler, for example in order to tune performance
 * of the generated code.
 *
 * @see x10.runtime.util.Configuration
 *
 * @author Christian Grothoff
 */
public final class Configuration extends x10.runtime.util.Configuration {

	/**
	 * The error received when attempting to load the configuration from
	 * the specified resource, or null if successful.
	 */
	public static final ConfigurationError LOAD_ERROR;

	public static String COMPILER_FRAGMENT_DATA_DIRECTORY = "data/";
	private static final String COMPILER_FRAGMENT_DATA_DIRECTORY_desc = "Advanced functionality: Directory for compiler templates";

	public static boolean BAD_PLACE_RUNTIME_CHECK = true;
	private static final String BAD_PLACE_RUNTIME_CHECK_desc = "Generate runtime place checks";

	/**
	 * Parses one argument from the command line.  This allows the user
	 * to specify options also on the command line (in addition to the
	 * configuration file and the defaults).
	 *
	 * @param arg the current argument, e.g., -STATISTICS_DISABLE=all
	 * @return true if the argument is recognized; false otherwise
	 */
	public static boolean parseArgument(String arg) {
		return parseArgument(Configuration.class, arg);
	}

	/**
	 * Return an array of (option,description) pairs.
	 */
	public static String[][] options() {
		return options(Configuration.class);
	}

	static {
		String cfg = getConfigurationResource();
		ConfigurationError loadError = null;
		try {
			readConfiguration(Configuration.class, cfg);
		} catch (ConfigurationError err) {
			System.err.println("Failed to read configuration file " + cfg + ": " + err);
			System.err.println("Using defaults");
			loadError = err;
		}
		LOAD_ERROR = loadError;
	}
}

