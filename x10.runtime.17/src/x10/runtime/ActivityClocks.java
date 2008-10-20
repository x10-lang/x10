/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

/**
 * Used to access clocks of current activity from XRX 
 * 
 * @author tardieu
 *
 */
public class ActivityClocks {
	public static boolean contains(Clock clock) {
		return Runtime.getCurrentActivity().clocks.contains(clock);
	}
	
	public static int get(Clock clock) {
		return Runtime.getCurrentActivity().clocks.get(clock);
	}

	public static void put(Clock clock, int phase) {
		Runtime.getCurrentActivity().clocks.put(clock, phase);
	}

	public static int remove(Clock clock) { 
		return Runtime.getCurrentActivity().clocks.remove(clock);
	}
}
