/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

/**
 * @author tardieu
 */
public value Clock(name: String) {
	private val state = new ClockState();
	
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
		property(name);
    	Activity.current().clocks().put(this, ClockState.FIRST_PHASE);
	}

    public def registered(): boolean {
    	return Activity.current().clocks().containsKey(this);
    }

    public def dropped(): boolean {
    	return !registered();
    }

    public def resume(): void {
    	if (dropped()) throw new ClockUseException();
		val ph = Activity.current().clocks()(this);
		if (ph < 0) throw new ClockUseException();
		finish async (state) state.resume();
    	Activity.current().clocks().put(this, -ph);
    }

    public def next(): void {
    	if (dropped()) throw new ClockUseException();
    	_next();
    }    

    public def phase(): int { 
    	if (dropped()) throw new ClockUseException();
    	return abs(Activity.current().clocks()(this));
    }
    
    public def drop(): void {
    	if (dropped()) throw new ClockUseException();
    	val ph = Activity.current().clocks().remove(this);
    	async (state) state.drop(ph);
    }
    
	public def hashCode(): int {
		return state.hashCode();
	}
	
	// methods called by the Activity object
	
    def _register(clocks: Clocks): void {
       	if (dropped()) throw new ClockUseException();
    	val ph = Activity.current().clocks()(this);
    	finish async (state) state.register(ph);
    	clocks.put(this, ph);
    }

    def _resume(): void {
		val ph = Activity.current().clocks()(this);
		if (ph < 0) return;
		finish async (state) state.resume();
    	Activity.current().clocks().put(this, -ph);
    }

    def _next(): void {
    	val ph = Activity.current().clocks()(this);
		finish async (state) state.next(ph);
    	Activity.current().clocks().put(this, abs(ph) + 1);
    }    

    def _drop(): void {
    	val ph = Activity.current().clocks()(this); 
    	async (state) state.drop(ph);
    }
}