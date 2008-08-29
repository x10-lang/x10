/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime.clock;

import x10.runtime.Clock;

/**
 * Defines the interface between: 
 * - An Activity and his Clock Manager.
 * - An X10 Program and an Activity.
 * 
 * @author vcave
 *
 */
public interface ClockManager {

	/** Add a clock to this activity's clock list. (Called when
	 * this activity creates a new clock.)
	 * @thread mythread
	 * @param c
	 */
	public abstract void addClock(Clock c);

	/**
	 * Drop a clock from this activity's clock list.
	 * @thread mythread
	 * @param c
	 */
	public abstract void dropClock(Clock c);

	/**
	 * Drop all clocks associated with this activity, and deregister this
	 * activity from all these clocks.
	 * @thread mythread
	 */
	public abstract void dropAllClocks();

	/**
	 * Implement next; for an activity. Blocks until each clock 
	 * this activity is registered on has moved to the next phase.
	 * @thread mythread
	 */
	public abstract void doNext();

	/** Check whether it is ok to use the given clock c (by spawning an async
	 * registered on the clock), throwing a ClockUseException if it is not.
	 * Checks that the clock has not been resumed or dropped, and that the 
	 * async is not being spawned inside a finish.
	 * Invoked from code generated from the X10 source by the translator.
	 * @param c -- The clock being checked for.
	 */
	public abstract Clock checkClockUse(Clock c);

	/**
	 *
	 * @return  The number of clocks registered in the current clock manager.
	 */
	public abstract int getNbRegisteredClocks();
	
	public abstract void registerClocks();

}
