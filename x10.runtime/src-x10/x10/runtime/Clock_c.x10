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
public class Clock_c extends Clock {
    private global val state = new ClockState();
    
    private global val hashCode = state.hashCode();

    public static def make(name:String) {
        val c = new Clock_c(name);
        Runtime.clockPhases().put(c, ClockState.FIRST_PHASE);
        return c;
    }

    private def this(name:String) {
        super(name);
    }

    public global def registered():boolean = Runtime.clockPhases().containsKey(this);

    public global def dropped():boolean = !registered();

    public global def resume():Void {
        if (dropped()) throw new ClockUseException();
        val ph = ph_c();
        if (ph < 0) throw new ClockUseException();
        finish async (state.location) state.resume();
        Runtime.clockPhases().put(this, -ph);
    }

    public global def next():Void {
        if (dropped()) throw new ClockUseException();
        next_c();
    }    

    public global def phase():Int {
        if (dropped()) throw new ClockUseException();
        return Math.abs(ph_c());
    }
    
    public global def drop():Void {
        if (dropped()) throw new ClockUseException();
        val ph = Runtime.clockPhases().remove(this) as Int;
        async (state.location) state.drop(ph);
    }
    
    public global def hashCode():Int {
        return hashCode;
    }
    
    global def resume_c():Void {
        val ph = ph_c();
        if (ph < 0) return;
        finish async (state.location) state.resume();
        Runtime.clockPhases().put(this, -ph);
    }

    global def next_c():Void {
        val ph = ph_c();
        finish async (state.location) state.next(ph);
        Runtime.clockPhases().put(this, Math.abs(ph) + 1);
    }    

    global def register_c():Int {
        if (dropped()) throw new ClockUseException();
        val ph = ph_c();
        finish async (state.location) state.register(ph);
        return ph;
    }    

    global def drop_c():Void {
        val ph = ph_c(); 
        async (state.location) state.drop(ph);
    }
    
    private global def ph_c():Int {
        return Runtime.clockPhases()(this) as Int;
    }
}
