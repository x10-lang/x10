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

	/**
	 * Body of main java thread
	 */
	protected void start(final String[] args) {
		this.args = args;

		// load libraries
		String property = System.getProperty("x10.LOAD");
		if (null != property) {
			String[] libs = property.split(":");
			for (int i = libs.length-1; i>=0; i--) System.loadLibrary(libs[i]);
		}

		java.lang.Runtime.getRuntime().addShutdownHook(new java.lang.Thread() {
		    public void run() { System.out.flush(); }
		});

		// start and join main x10 thread in place 0
		Thread thread = new Thread(0, this, "thread-main");
		thread.start();
		try { thread.join(); } catch (InterruptedException e) {}

		// shutdown
		System.exit(exitCode);
	}

	/**
	 * Body of main x10 thread
	 */
	public void run() {
		try { Class.forName("x10.lang.Place"); } catch (ClassNotFoundException e) { }

		// execute root x10 activity
		main(x10.core.RailFactory.<java.lang.String>makeRailFromJavaArray(args));
	}

	/**
	 * User code provided by Main template
	 * - start xrx runtime
	 * - run main activity
	 */
	public abstract void main(x10.core.Rail<java.lang.String> args);

	/**
	 * Application exit code
	 */
	private static int exitCode = 0;

	/**
	 * Set the application exit code
	 */
	public static void setExitCode(int code) {
		exitCode = code;
	}

	/**
	 * Process place checks?
	 */
	public static final boolean PLACE_CHECKS = !Boolean.getBoolean("x10.NO_PLACE_CHECKS");

    /**
     * Disable steals?
     */
    public static final boolean NO_STEALS = Boolean.getBoolean("x10.NO_STEALS");

	/**
	 * The number of places in the system
	 */
	public static final int MAX_PLACES = Integer.getInteger("x10.NUMBER_OF_LOCAL_PLACES", 4);

	/**
	 * The number of threads to allocate in the thread pool
	 */
	public static final int INIT_THREADS = Integer.getInteger("x10.INIT_THREADS_PER_PLACE", java.lang.Runtime.getRuntime().availableProcessors());

	/**
	 * Whether or not to start more threads while blocking
	 */
	public static final boolean STATIC_THREADS = Boolean.getBoolean("x10.STATIC_THREADS");

	/**
	 * Synchronously executes body at place(id)
	 */
	public static void runAt(int id, x10.core.fun.VoidFun_0_0 body) {
		final Thread thread = Thread.currentThread();
		final int ret = thread.home();
		thread.home(id); // update thread place
		try {
			body.apply();
		} finally {
			thread.home(ret); // restore thread place
		}
	}

	/**
	 * Return true if place(id) is local to this node
	 */
	public static boolean local(int id) {
		return true; // single process implementation
	}
}
