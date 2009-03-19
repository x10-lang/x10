package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "x10.runtime.Clock")
public value Clock extends Value {

    public native def this():Clock;
    public native def this(name:String):Clock;

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
