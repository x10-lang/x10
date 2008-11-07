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

	private Object place; // the current place
	private Object activity; // the current activity

	public Thread(Object place, Runnable runnable, String name) {
		super(runnable, name);
		this.place = place;
	}

	public void activity(Object activity) {
		this.activity = activity;
	}

	public Object activity() {
		return activity;
	}

	public void place(Object place) {
		this.place = place;
	}

	public Object place() {
		return place;
	}
}
