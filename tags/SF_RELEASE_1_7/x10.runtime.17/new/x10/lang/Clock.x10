/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "x10.runtime.Clock")
public value Clock extends Value {

    @Native("java", "new x10.runtime.Clock()")
    public native static def make(): Clock;
    
    @Native("java", "new x10.runtime.Clock(#1)")
    public native static def make(name:String): Clock;

    private native def this():Clock;
    private native def this(name:String):Clock;

    @Native("java", "#0.resume()")
    public native def resume():void;

    @Native("java", "#0.drop()")
    public native def drop():void;

    @Native("java", "#0.registered()")
    public native def registered():boolean;

    @Native("java", "#0.dropped()")
    public native def dropped():boolean;

    @Native("java", "#0.next()")
    public native def next():void;

}
