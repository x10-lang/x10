/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

import java.util.List;
import java.util.Map;

/**
 * Map registered clocks to phases
 * 
 * @author tardieu
 *
 */
class Clocks {
	private Map<Clock,Integer> clocks = new java.util.HashMap<Clock,Integer>();

	public boolean contains(Clock clock) {
		return clocks.containsKey(clock);
	}
	
	public int get(Clock clock) {
		return clocks.get(clock);
	}

	public void put(Clock clock, int phase) {
		clocks.put(clock, phase);
	}

	public int remove(Clock clock) { 
		return clocks.remove(clock);
	}
	
	/**
	 * Register activity with one clock
	 * 
	 * @param clock
	 */
	void register(Clock clock) {
		clock._register();
		clocks.put(clock, Runtime.getCurrentActivity().clocks.get(clock));
	}

	/**
	 * Register activity with multiple clocks
	 * 
	 * @param list
	 */
	void register(List<Clock> list) {
		for(Clock clock: list) register(clock);
	}

	/**
	 * Drop all clocks
	 */
	void drop() {
		for(Clock clock: clocks.keySet()) clock._drop();
		clocks.clear();
	}

	/**
	 * Next in parallel on all clocks
	 */
	void next() {
		for(Clock clock: clocks.keySet()) clock._resume();
		for(Clock clock: clocks.keySet()) clock._next();
	}
}
