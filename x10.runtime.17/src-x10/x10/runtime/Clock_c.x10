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
	
    private static def abs(z:Int):Int = z < 0 ? -z : z;

	public def this(name:String) {
		super(name);
    	Runtime.clockPhases().put(this, ClockState.FIRST_PHASE);
	}

    public def registered():boolean = Runtime.clockPhases().containsKey(this);

    public def dropped():boolean = !registered();

    public def resume():Void {
    	if (dropped()) throw new ClockUseException();
		val ph = ph_c();
		if (ph < 0) throw new ClockUseException();
		finish async (state) state.resume();
    	Runtime.clockPhases().put(this, -ph);
    }

    public def next():Void {
    	if (dropped()) throw new ClockUseException();
    	next_c();
    }    

    public def phase():Int = abs(phase_c());
    
    public def drop():Void {
    	if (dropped()) throw new ClockUseException();
    	val ph = Runtime.clockPhases().remove(this) to Int;
    	async (state) state.drop(ph);
    }
    
	public def hashCode():Int {
		return state.hashCode();
	}
	
    def register_c(clockPhases:ClockPhases, ph:Int):Void {
    	finish async (state) state.register(ph);
    	clockPhases.put(this, ph);
    }

    def resume_c():Void {
		val ph = ph_c();
		if (ph < 0) return;
		finish async (state) state.resume();
    	Runtime.clockPhases().put(this, -ph);
    }

    def next_c():Void {
    	val ph = ph_c();
		finish async (state) state.next(ph);
    	Runtime.clockPhases().put(this, abs(ph) + 1);
    }    

    def phase_c():Int { 
    	if (dropped()) throw new ClockUseException();
    	return ph_c();
    }    

    def drop_c():Void {
    	val ph = ph_c(); 
    	async (state) state.drop(ph);
    }
    
    def ph_c():Int {
        return Runtime.clockPhases()(this) to Int;
    }
}
