/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime.impl.java;

/**
 * @author Christian Grothoff
 * @author vj
 * @author Raj Barik, Vivek Sarkar
 * @author tardieu
 */
public class Thread extends java.lang.Thread {
	public static Thread currentThread() {
		return (Thread) java.lang.Thread.currentThread();
	}

	private int home;    // the current place
	private Object worker;   // the current worker

	/**
	 * Create main x10 thread (called by native runtime only )
	 */
	Thread(int home, Runnable runnable, String name) {
		super(runnable, name);
		this.home = home;
	}

	/**
	 * Create additional x10 threads (called by xrx only)
	 */
	public Thread(final x10.core.fun.VoidFun_0_0 body, String name) {
		super(new Runnable() { public void run() { body.apply(); } }, name);
		home = currentThread().home;
	}

	/**
	 * Attach worker to thread
	 */
	public void worker(Object worker) {
		this.worker = worker;
	}

	/**
	 * Return current worker
	 */
	public Object worker() {
		return worker;
	}

	/**
	 * Update thread place (called by native runtime only)
	 */
	void home(int home) {
		this.home = home;
	}

	/**
	 * Return current place
	 */
	public int home() {
		return home;
	}

    public int locInt() {
        return home;
    }

    public String name() {
        return getName();
    }

    public void name(String name) {
        setName(name);
    }

    public static void park() {
        java.util.concurrent.locks.LockSupport.park();
    }

    public void unpark() {
        java.util.concurrent.locks.LockSupport.unpark(this);
    }

    public static void parkNanos(Long nanos) {
        java.util.concurrent.locks.LockSupport.parkNanos(nanos);
    }

    public static long getTid() {
        return Thread.currentThread().getId();
    }
}
