package x10.util.concurrent;

import x10.compiler.NativeClass;
import x10.io.Unserializable;

/**
 * X10 wrapper class for a single-use condition variable.
 * This class has no dependency on the X10 runtime context.
 * Methods of this class may be safely invoked from native threads.
 */
@NativeClass("c++", "x10.lang", "Condition")
@NativeClass("java", "x10.core.concurrent", "Condition")
public class Condition implements Unserializable {
    public native def this();
    /**
     * Releases the thread waiting on the condition if any.
     * May be called more than once (subsequent calls are ignored).
     */
    public native def release():void;
    /**
     * Blocks the calling thread until release is invoked.
     * Returns instantly if release has already been invoked.
     * Does not return spuriously.
     * Cannot be called by more than one thread.
     */
    public native def await():void;
    /**
     * Blocks the calling thread until release is invoked.
     * Returns instantly if release has already been invoked.
     * Timout after timeout nano seconds.
     * May return spuriously.
     * Cannot be called by more than one thread.
     */
    public native def await(timeout:Long):void;
    
    /**
     * Returns true if release() has been called on this condition.
     */
    public native def complete():Boolean;
}
