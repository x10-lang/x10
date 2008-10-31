/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

import x10.config.ConfigurationError;
import x10.runtime.util.ShowUsageNotification;

public class Runtime {
	public static JavaRuntime java;

	/* Pre-load the VMInterface class */
	static { VMInterface foo = null; }

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("usage: java x10.runtime.Runtime MainClass [args]");
			System.exit(1);
		}
		try {
			String[] strippedArgs = Configuration.parseCommandLine(args);
			java = new JavaRuntime();
			loadAndInitLibs();
			run(strippedArgs);
		} catch (ShowUsageNotification e) {
			Usage.usage(System.out, null);
		} catch (ConfigurationError e) {
			Usage.usage(System.err, e);
		} catch (Exception e) {
			Runtime.java.error("Unexpected Exception in X10 runtime.", e);
		}
	}

	/**
	 * Load and init shared library
	 */
	private static void loadAndInitLibs() {
		if (null != Configuration.LOAD) {
			String[] libs = Configuration.LOAD.split(":");
			for (int i=libs.length-1;i>=0;i--)
				System.loadLibrary(libs[i]);
		}
	}

	public static final int MAX_PLACES = Configuration.NUMBER_OF_LOCAL_PLACES;

	private static final Class[] STRING_ARRAYS = new Class[] { String[].class };

	/**
	 * Instantiate and run the main activity
	 */
	private static void run(String[] strippedArgs) {
		java.lang.Object[] args = { strippedArgs };
		try {
			// instantiation using reflection
			Class main = Class.forName(Configuration.MAIN_CLASS_NAME + "$Main");
			Runnable r = (Runnable) main.getDeclaredConstructor(STRING_ARRAYS).newInstance(args);
			if (Configuration.PRELOAD_CLASSES)
				PreLoader.preLoad(main, Configuration.PRELOAD_STRINGS);
			// run the main activity
			r.run();
		} catch (Exception e) {
			System.err.println("Could not find default constructor of main class '"
					+ Configuration.MAIN_CLASS_NAME + "$Main" + "'!");
			throw new Error(e);
		}
		exit();
	}

	private static int exitCode = 0;

	public static void setExitCode(int code) {
		exitCode = code;
	}

	public static void exit() {
		System.exit(exitCode);
	}

	public static void exit(int code) {
		setExitCode(code);
		exit();
	}
}
