/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.util.ClockState;

/**
 * Implements the clock API
 */
public value Clock implements ClockInterface {
	private val state = new ClockState();
	val name: String;
	
    private static def abs(z: int): int {
    	return (z<0) ? -z : z;
    }

    public static def make(): Clock {
    	return make("");
    }
    
    public static def make(name: String): Clock {
    	return new Clock(name);
    }

	private def this(name: String): Clock {
		this.name = name;
    	ActivityClocks.put(this, ClockState.FIRST_PHASE);
	}

    public def registered(): boolean {
    	return ActivityClocks.contains(this);
    }

    public def dropped(): boolean {
    	return !registered();
    }

    public def _register(): void {
    	if (dropped()) throw new ClockUseException();
    	val ph = ActivityClocks.get(this);
    	finish async (state) state.register(ph);
    }

    public def _resume(): void {
		val ph = ActivityClocks.get(this);
		if (ph < 0) return;
		finish async (state) state.resume();
    	ActivityClocks.put(this, -ph);
    }

    public def resume(): void {
    	if (dropped()) throw new ClockUseException();
		val ph = ActivityClocks.get(this);
		if (ph < 0) throw new ClockUseException();
		finish async (state) state.resume();
    	ActivityClocks.put(this, -ph);
    }

    public def _next(): void {
    	val ph = ActivityClocks.get(this);
    	ActivityClocks.put(this, (future (state) state.next(ph))());
    }    

    public def next(): void {
    	if (dropped()) throw new ClockUseException();
    	_next();
    }    

    public def phase(): int { 
    	if (dropped()) throw new ClockUseException();
    	return abs(ActivityClocks.get(this));
    }
    
    public def _drop(): void {
    	val ph = ActivityClocks.get(this); 
    	async (state) state.drop(ph);
    }

    public def drop(): void {
    	if (dropped()) throw new ClockUseException();
    	val ph = ActivityClocks.remove(this);
    	async (state) state.drop(ph);
    }
    
	public def hashCode(): int {
		return state.hashCode();
	}
}
