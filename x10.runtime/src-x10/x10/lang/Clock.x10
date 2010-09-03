/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.lang;

/**
 * @author tardieu
 *
 * TODO: Ported from 2.0 to 2.1 via naive simulation of 
 *       2.0 style global object by injecting a root field
 *       that is a GlobalRef(this) and always accessing fields 
 *       as this.root().f instead of this.f.
 *
 *       Needs to be cleaned up and simplified
 *
 */
public class Clock(name:String) {
    private val root:GlobalRef[Clock] = GlobalRef[Clock](this);

    public static def make(): Clock = make("");

    public const FIRST_PHASE = 1;

    public static def make(name:String):Clock {
        val clock = new Clock(name);
        Runtime.clockPhases().put(clock, FIRST_PHASE);
        return clock;
    }

    // NOTE: all transient fields must always be accessed as root().f, not .f
    private transient var count:Int = 1;
    private transient var alive:Int = 1;
    private transient var phase:Int = FIRST_PHASE;

    private def this(name:String) {
        property(name);
    }

    private def get() = Runtime.clockPhases().get(this.root).value;

    private def put(ph:Int) = Runtime.clockPhases().put(this.root, ph);

    private def remove() = Runtime.clockPhases().remove(this.root).value;

    private atomic def resumeLocal(){here==root.home} {
        if (--root().alive == 0) {
            root().alive = root().count;
            ++root().phase;
        }
    }

    private def dropLocal(ph:Int){here==root.home} {
        --root().count;
        if (-ph != root().phase)
            resumeLocal();
    }

    def register() {
        if (dropped()) throw new ClockUseException();
        val ph = get();
        at (this.root) atomic {
            ++root().count;
            if (-ph != root().phase) ++root().alive;
        }
        return ph;
    }

    def resumeUnsafe() {
        val ph = get();
        if (ph < 0) return;
        at (this.root) resumeLocal();
        put(-ph);
    }

    def nextUnsafe() {
        val ph = get();
        val abs = Math.abs(ph);
        at (this.root) {
            if (ph > 0) resumeLocal();
            await (abs < root().phase);
        }
        put(abs + 1);
    }

    def dropUnsafe() {
        val ph = remove();
        async (this.root) dropLocal(ph);
    }

    def dropInternal() {
        val ph = get();
        async (this.root) dropLocal(ph);
    }

    public def registered():Boolean = Runtime.clockPhases().containsKey(this.root);

    public def dropped():Boolean = !registered();

    public def phase():Int {
        if (dropped()) throw new ClockUseException();
        return Math.abs(get());
    }

    public def resume():Void {
        if (dropped()) throw new ClockUseException();
        resumeUnsafe();
    }

    public def next():Void {
        if (dropped()) throw new ClockUseException();
        nextUnsafe();
    }

    public def drop():Void {
        if (dropped()) throw new ClockUseException();
        dropUnsafe();
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
