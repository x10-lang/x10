/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Sep 28, 2004
 */
package x10.runtime;

import x10.runtime.util.ConfigurationError;
import x10.runtime.util.OptionError;
import x10.runtime.util.ShowUsageNotification;

import java.util.StringTokenizer;

/**
 * This class provides the configuration for the X10 runtime.
 * The configuration is a set of values that can be used to
 * configure the runtime, for example in order to tune performance.
 *
 * @see x10.runtime.util.Configuration
 *
 * @author Christian Grothoff
 *
 * Extensions for JUC implementation 
 * @author Raj Barik, Vivek Sarkar
 */
public final class Configuration extends x10.runtime.util.Configuration {

	/**
	 * The error received when attempting to load the configuration from
	 * the specified resource, or null if successful.
	 */
	public static final ConfigurationError LOAD_ERROR;

	public static int NUMBER_OF_LOCAL_PLACES = 4;
	private static final String NUMBER_OF_LOCAL_PLACES_desc = "The number of places";

	public static int INIT_THREADS_PER_PLACE = 3;
	private static final String INIT_THREADS_PER_PLACE_desc = "Initial number of Java threads allocated to thread pool for a single place";

	/**
	 * Perform additional instrumentation in the X10 runtime to dump out
	 * abstract parallel execution statistics for the X10 program.
	 * Default is false.
	 */
	public static boolean ABSTRACT_EXECUTION_STATS = false;
	private static final String ABSTRACT_EXECUTION_STATS_desc = "Dump out parallel execution statistics";

	/**
	 * Dump out unblocked execution times in each place.  The time that an
	 * activity spent blocked in the X10 runtime is not counted.  However,
	 * this is still an approximate estimate because it does account for time
	 * that an activity is not executing because its Java thread in the thread
	 * pool does not have an available processor.
	 *
	 * NOTE: this option is only enabled when ABSTRACT_EXECUTION_STATS==true;
	 */
	public static boolean ABSTRACT_EXECUTION_TIMES = false;
	private static final String ABSTRACT_EXECUTION_TIMES_desc = "If dumping out statistics, also dump out unblocked execution times";

    public static boolean BIND_THREADS = false;
    private static final String BIND_THREADS_desc = "Use platform-specific calls to bind Java threads to CPUs";
 
    public static boolean BIND_THREADS_DIAGNOSTICS = false;
    private static final String BIND_THREADS_DIAGNOSTICS_desc = "Print diagnostics related to platform-specific calls to bind Java threads to CPUs";
    
	// Set Bad_PLACE_RUNTIME_CHECK = false as the default value in support of implicit-place syntax
    public static boolean BAD_PLACE_RUNTIME_CHECK = false;
	private static final String BAD_PLACE_RUNTIME_CHECK_desc = "Perform runtime place checks";

	/**
	 * The name of the main class of the application.
	 * TODO: Move this to Runtime.main().
	 */
	public static String MAIN_CLASS_NAME = null;
	private static final String MAIN_CLASS_NAME_desc = "The name of the main class";

	/**
	 * Enables new (experimental) ateach and foreach optimization (code by
	 * Christian Grothoff and Raj Barik).  Default to off until that code is
	 * sufficiently tested...
	 */
	public static boolean OPTIMIZE_FOREACH = false;
	private static final String OPTIMIZE_FOREACH_desc = "Experimental: Enable runtime loop optimizations";

	/**
	 * Pre-load all classes recursively on startup.  This is needed for JIT
	 * optimizations.  Default to off until the initialization sequence is
	 * sufficiently tested...
	 */
	public static boolean PRELOAD_CLASSES = false;
	private static final String PRELOAD_CLASSES_desc = "Pre-load all classes on recursively startup";

	/**
	 * Pre-load all strings in all classes on startup.  This is needed for
	 * JIT optimizations.  This option only takes effect if PRELOAD_CLASSES
	 * is also on.  Default to off until the JIT can actually take advantage
	 * of it.
	 */
	public static boolean PRELOAD_STRINGS = false;
	private static final String PRELOAD_STRINGS_desc = "If pre-loading classes, also pre-load all strings";

	/**
	 * Which shared libraries should be loaded?  The format
	 * is libraryname[:libraryname]*.  Null or "" are valid
	 * entries (for not loading any libraries).
	 */
	public static String LOAD = null;
	private static final String LOAD_desc = "Load specified shared library";

	/**
	 * Parses the command line.  This allows the user to specify
	 * options also on the command line (in addition to the
	 * configuration file and the defaults).  The name of the
	 * main class is the first argument that does not start with
	 * a "-".  All arguments after the main class are returned
	 * and should be passed to the application.
	 *
	 * @param args arguments.  Example: -BAD_PLACE_RUNTIME_CHECK=true
	 * @return the arguments for the application
	 */
	public static String[] parseCommandLine(String[] args) throws ConfigurationError {
		if (args == null)
			return new String[0];

		int pos = 0;
		while (pos < args.length && args[pos].length() > 0 && args[pos].charAt(0) == '-') {
			if (args[pos].equals("-h") ||
					args[pos].equals("-help") ||
					args[pos].equals("--help"))
				throw new ShowUsageNotification();
			// vj: added to allow the runtime to use Polyglot's report mechanism
			if (args[pos].equals("-report")) {
				pos++;
				StringTokenizer st = new StringTokenizer(args[pos], "=");
				String topic = ""; int level = 0;
				if (st.hasMoreTokens()) topic = st.nextToken();
				if (st.hasMoreTokens()) {
					try {
						level = Integer.parseInt(st.nextToken());
					}
					catch (NumberFormatException e) {}
				}

				Report.addTopic(topic, level);
				pos++;
				continue;
			}

			try {
				parseArgument(Configuration.class, args[pos]);
			} catch (OptionError e) {
				throw new ConfigurationError("Invalid argument: '"+args[pos]+"'");
			}
			pos++;
		}
		// TODO: Move MAIN_CLASS_NAME processing to Runtime.main()
		MAIN_CLASS_NAME = args[pos++];
		// vj hack to let Eclipse x10 command run with ${resource_loc}
		// get rid of prefix pathname.
		MAIN_CLASS_NAME = MAIN_CLASS_NAME.substring(MAIN_CLASS_NAME.lastIndexOf("\\")+1);
		if (MAIN_CLASS_NAME.endsWith(".java"))
			MAIN_CLASS_NAME = MAIN_CLASS_NAME.substring(0, MAIN_CLASS_NAME.length()-5);
		else if (MAIN_CLASS_NAME.endsWith(".x10"))
			MAIN_CLASS_NAME = MAIN_CLASS_NAME.substring(0, MAIN_CLASS_NAME.length()-4);

		// Sanity checks for values.
		if (NUMBER_OF_LOCAL_PLACES < 0) {
			System.err.println("Negative value |" + NUMBER_OF_LOCAL_PLACES
					+ "| for NUMBER_OF_LOCAL_PLACES rejected. Set to 4." );
			NUMBER_OF_LOCAL_PLACES = 4;
		}
		if (INIT_THREADS_PER_PLACE < 0) {
			System.err.println("Negative value |" + INIT_THREADS_PER_PLACE
					+ "| for INIT_THREADS_PER_PLACE rejected. Set to 3." );
			INIT_THREADS_PER_PLACE = 3;
		}

		int aa = args.length-pos;
		String[] appArgs = new String[aa];
		System.arraycopy(args, pos, appArgs, 0, aa);
		if (Report.should_report(Report.ACTIVITY, 3)) {
			Report.report(3, Thread.currentThread() +  " user class is |"
					+ Configuration.MAIN_CLASS_NAME+ "|.");
		}
		return appArgs;
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

