/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime.clock;

import java.util.List;

import x10.runtime.Activity;
import x10.runtime.Clock;
/**
 * Instantiate the default Clock Manager implementation.
 * 
 * @author vcave
 *
 */
public class ClockManagerFactory {
	
	/**
	 * Creates and associate a clock manager to an activity with an initial list of clocks
	 * @param activity The activity to associates with created manager.
	 * @param clocks The list of clock to register
	 * @return A newly created instance of a ClockManager
	 */
	public static ClockManager getClockManager(Activity activity, List clocks) {
		assert (clocks != null);
		if(!clocks.isEmpty())
			return new ClockManagerImpl(activity, clocks);
		
		return null;
	}
	
	/**
	 * Creates and associate a clock manager to an activity with an initial clock
	 * @param activity The activity to associates with created manager.
	 * @param clocks The clock to register.
	 * @return A newly created instance of a ClockManager
	 */
	public static ClockManager getClockManager(Activity activity, Clock clock) {
		assert (clock != null);
		return new ClockManagerImpl(activity, clock);
	}
}
