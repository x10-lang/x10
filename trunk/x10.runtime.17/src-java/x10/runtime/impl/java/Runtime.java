/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime.impl.java;

public abstract class Runtime implements Runnable {
	private String[] args;

	// main java thread
	protected void start(final Object place, final String[] args) {
		this.args = args;
		
		// load libraries
		String property = System.getProperty("x10.LOAD");
		if (null != property) {
			String[] libs = property.split(":");
			for (int i = libs.length-1; i>=0; i--) System.loadLibrary(libs[i]);
		}

		// start and join main x10 thread
		Thread thread = new Thread(place, this, "thread-main");
		thread.start();
		try { thread.join(); } catch (InterruptedException e) {}
		
		// shutdown
		System.exit(exitCode);
	}

	// main x10 thread
	public void run() {
		// preload classes
		if (Boolean.getBoolean("x10.PRELOAD_CLASSES")) {
			PreLoader.preLoad(this.getClass().getEnclosingClass(), Boolean.getBoolean("x10.PRELOAD_STRINGS"));
		}
		
		// execute root x10 activity
		main(x10.core.RailFactory.<java.lang.String>makeRailFromJavaArray(args));
	}

	// root x10 activity
	public abstract void main(x10.core.Rail<java.lang.String> args);
	
	private static int exitCode = 0;

	public static void setExitCode(int code) {
		exitCode = code;
	}
}
