/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime;

/**
 * @author tardieu
 */
public value Clock_c extends Clock {
	private val state = new ClockState();
	
    private static def abs(z: int): int {
    	return (z<0) ? -z : z;
    }

	public def this(name: String) {
		super(name);
    	Runtime.clocks().put(this, ClockState.FIRST_PHASE);
	}

    public def registered(): boolean {
    	return Runtime.clocks().containsKey(this);
    }

    public def dropped(): boolean {
    	return !registered();
    }

    public def resume(): void {
    	if (dropped()) throw new ClockUseException();
		val ph = Runtime.clocks()(this);
		if (ph < 0) throw new ClockUseException();
		finish async (state) state.resume();
    	Runtime.clocks().put(this, -ph);
    }

    public def next(): void {
    	if (dropped()) throw new ClockUseException();
    	next_c();
    }    

    public def phase(): int { 
    	if (dropped()) throw new ClockUseException();
    	return abs(Runtime.clocks()(this));
    }
    
    public def drop(): void {
    	if (dropped()) throw new ClockUseException();
    	val ph = Runtime.clocks().remove(this);
    	async (state) state.drop(ph);
    }
    
	public def hashCode(): int {
		return state.hashCode();
	}
	
    def register_c(clocks: Clocks): void {
       	if (dropped()) throw new ClockUseException();
    	val ph = Runtime.clocks()(this);
    	finish async (state) state.register(ph);
    	clocks.put(this, ph);
    }

    def resume_c(): void {
		val ph = Runtime.clocks()(this);
		if (ph < 0) return;
		finish async (state) state.resume();
    	Runtime.clocks().put(this, -ph);
    }

    def next_c(): void {
    	val ph = Runtime.clocks()(this);
		finish async (state) state.next(ph);
    	Runtime.clocks().put(this, abs(ph) + 1);
    }    

    def drop_c(): void {
    	val ph = Runtime.clocks()(this); 
    	async (state) state.drop(ph);
    }
}
