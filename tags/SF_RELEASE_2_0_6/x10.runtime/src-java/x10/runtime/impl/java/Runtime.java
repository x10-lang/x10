/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.runtime.impl.java;

import java.lang.reflect.InvocationTargetException;

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
		main(x10.core.RailFactory.<java.lang.String>makeRailFromJavaArray(new x10.rtt.RuntimeType<java.lang.String>(java.lang.String.class),args));
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
	public static final int INIT_THREADS = Integer.getInteger("x10.INIT_THREADS", java.lang.Runtime.getRuntime().availableProcessors());

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

	/**
	 * Redirect to the specified user class's main().
	 */
	public static void main(String[] args) throws Throwable {
	    boolean verbose = false;
	    String className = null;
	    for (int i = 0; i < args.length; i++) {
	        String arg = args[i];
	        if (arg.equals("-v") || arg.equals("-verbose") || arg.equals("--verbose")) {
	            verbose=true;
	        } else if (arg.charAt(0)=='-') {
	            int eq = arg.indexOf('=');
	            String key = "x10." + (eq<0 ? arg.substring(1) : arg.substring(1, eq));
	            String value = eq<0 ? "true" : arg.substring(eq+1);
	            System.setProperty(key, value);
	        } else {
	            int dotx10 = arg.indexOf(".x10");
	            className = (dotx10<0 ? arg : arg.substring(0, dotx10)) + "$Main";
	            int len = args.length-i-1;
	            System.arraycopy(args, i+1, args = new String[len], 0, len);
	        }
	    }
	    if (verbose) {
	        System.err.println("Invoking user class: "+className+" with classpath '"+System.getProperty("java.class.path")+"'");
	    }
	    try {
	        Class.forName(className).getMethod("main", String[].class).invoke(null, (Object)args);
	    } catch (ClassNotFoundException e) {
	        System.err.println("Class not found: "+className);
	    } catch (InvocationTargetException e) {
	        throw e.getCause();
	    } catch (Exception e) {
	        System.err.println("Unable to invoke user program: "+e);
	        if (verbose)
	            e.printStackTrace();
	    }
	}
}
