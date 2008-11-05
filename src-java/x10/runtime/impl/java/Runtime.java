/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime.impl.java;

import java.lang.reflect.Constructor;

import x10.config.ConfigurationError;
import x10.runtime.util.ShowUsageNotification;

public class Runtime {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("usage: java x10.runtime.Runtime MainClass [args]");
			System.exit(1);
		}
		try {
			String[] strippedArgs = Configuration.parseCommandLine(args);
			loadAndInitLibs();
			run(strippedArgs);
		} catch (ShowUsageNotification e) {
			Usage.usage(System.out, null);
		} catch (ConfigurationError e) {
			Usage.usage(System.err, e);
		}
	}

	/**
	 * Load and init shared library
	 */
	private static void loadAndInitLibs() {
		if (null != Configuration.LOAD) {
			String[] libs = Configuration.LOAD.split(":");
			for (int i=libs.length-1; i>=0; i--) System.loadLibrary(libs[i]);
		}
	}

	public static final int MAX_PLACES = Configuration.NUMBER_OF_LOCAL_PLACES;
	
	public static final int INIT_THREADS_PER_PLACE = Configuration.INIT_THREADS_PER_PLACE;

	private static int exitCode = 0;

	private static final Class[] STRING_ARRAYS = new Class[] { String[].class };

	/**
	 * Instantiate and run main activity
	 */
	private static void run(String[] strippedArgs) {
		// instantiation using reflection
		Runnable r;
		try {
			java.lang.Object[] args = { strippedArgs };
			Class main = Class.forName(Configuration.MAIN_CLASS_NAME + "$Main");
			Constructor ctor = main.getDeclaredConstructor(STRING_ARRAYS);
			r = (Runnable) ctor.newInstance(args);
			if (Configuration.PRELOAD_CLASSES) {
				PreLoader.preLoad(main, Configuration.PRELOAD_STRINGS);
			}
		} catch (ClassNotFoundException e) {
			System.err.println("Could not find main class '"
					+ Configuration.MAIN_CLASS_NAME + "$Main" + "'!");
			throw new Error(e);
		} catch (Exception e) {
			System.err.println("Could not find constructor of main class '"
					+ Configuration.MAIN_CLASS_NAME + "$Main" + "'!");
			throw new Error(e);
		}

		// run the main activity in its own thread
		Thread t = new Thread(r, "main");
		t.start();
		try { t.join(); } catch (InterruptedException e) {}

		System.exit(exitCode);
	}

	public static void setExitCode(int code) {
		exitCode = code;
	}
}
