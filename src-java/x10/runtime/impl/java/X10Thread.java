/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime.impl.java;

/**
 * Thread in the thread pool that can be used to run multiple
 * activities over time.
 *
 * @author Christian Grothoff
 * @author vj
 * @author Raj Barik, Vivek Sarkar
 * 3/6/2006: replaced original PoolRunner by JCU implementation
 * @author tardieu
 */
public class X10Thread extends Thread {
	public static X10Thread currentThread() {
		Thread t = Thread.currentThread();
		if (t instanceof X10Thread) return (X10Thread) t;
		return null; // happens before main activity is spawned
	}

	private final int placeId;
	private Runnable runnable; // the activity currently executed

	X10Thread(ThreadGroup group, Runnable r, String name, int id) {
		super(group, r, name, 0);
		placeId = id;
	}

	public void setRunnable(Runnable r) {
		runnable = r;
	}

	public Runnable getRunnable() {
		return runnable;
	}

	public int getPlaceId() {
		return placeId;
	}
}
