/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime.impl.java;

public class Runtime {
	/**
	 * Execute main x10 thread
	 */
	public static void start(final Object place, final Runnable runnable) {
		// load libraries
		String property = System.getProperty("x10.LOAD");
		if (null != property) {
			String[] libs = property.split(":");
			for (int i = libs.length-1; i>=0; i--) System.loadLibrary(libs[i]);
		}

		Runnable r = new Runnable() {
			public void run() {
				// preload classes
				if (Boolean.getBoolean("x10.PRELOAD_CLASSES")) {
					PreLoader.preLoad(runnable.getClass().getEnclosingClass(),
							Boolean.getBoolean("x10.PRELOAD_STRINGS"));
				}
				// execute thread body
				runnable.run();
			}

		};
		Thread thread = new Thread(place, r, "thread-main");
		thread.start();
		try { thread.join(); } catch (InterruptedException e) {}
		System.exit(exitCode);
	}

	private static int exitCode = 0;

	public static void setExitCode(int code) {
		exitCode = code;
	}
}
