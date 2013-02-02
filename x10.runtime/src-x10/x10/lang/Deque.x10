package x10.lang;

import x10.compiler.NativeRep;

/**
 * Native deque. Only to be used in the runtime implementation.
 * Temporarily public to enable use in WS runtime classes.
 */
@NativeRep("java", "x10.core.Deque", null, "x10.core.Deque.$RTT")
@NativeRep("c++", "x10::lang::Deque*", "x10::lang::Deque", null)
public final class Deque {
    public native def size():Int;

    public native def poll():Any;

    public native def push(t:Any):void;

    public native def steal():Any;
}
