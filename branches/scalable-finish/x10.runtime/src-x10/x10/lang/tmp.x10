
/**
 * @author tardieu
 */
public final class Runtime {

    public native static def println(o:Object) : Void;

    // Configuration options
    public const NO_STEALS = false;
    public const INIT_THREADS = 1;
    public const STATIC_THREADS = false;

    /**
     * Run body at place(id).
     * May be implemented synchronously or asynchronously.
     * Body cannot spawn activities, use clocks, or raise exceptions.
     */

    public static def runAtNative(id:Int, body:()=>Void):Void { body(); }

    /**
     * Java: run body synchronously at place(id) in the same node as the current place.
     * C++: run body. (no need for a native implementation)
     */
    public static def runAtLocal(id:Int, body:()=>Void):Void { body(); }

    /**
     * Java: pretend receiver is local.
     */
    public static def pretendLocal[T](x:T):T! = x as T!;

    /**
     * Return true if place(id) is in the current node.
     */

    public static def isLocal(id:Int):Boolean = id == here.id;

    /**
     * Process one incoming message if any (non-blocking).
     */

    public static def event_probe():Void {}

    /** Accessors for native performance counters
     */

    static def getAsyncsSent() = 0 as Long;

    static def setAsyncsSent(v:Long) { }

    static def getAsyncsReceived() = 0 as Long;

    static def setAsyncsReceived(v:Long) { }

    static def getSerializedBytes() = 0 as Long;

    static def setSerializedBytes(v:Long) { }

    static def getDeserializedBytes() = 0 as Long;

    static def setDeserializedBytes(v:Long) { }

    public static def deallocObject (o:Object) { }

    public static def dealloc[T] (o:()=>T) { }

    public static def dealloc (o:()=>Void) { }

    public static def registerHandlers() {}

    static final class Deque {
        public native def this();

        public native def size():Int;

        public native def poll():Object;

        public native def push(t:Object):Void;

        public native def steal():Object;
    }


    static class Lock {
        public native def this();

        public native def lock():Void;

        public native def tryLock():Void;

        public native def unlock():Void;

        public native def getHoldCount():Int;
    }


    static class Monitor extends Lock {
        /**
         * Parked threads
         */
        private val threads = new Stack[Thread]();

        /**
         * Park calling thread
         * Increment blocked thread count
         * Must be called while holding the lock
         * Must not be called while holding the lock more than once
         */
        def await():Void {}

        /**
         * Unpark every thread
         * Decrement blocked thread count
         * Release the lock
         * Must be called while holding the lock
         */
        def release():Void {}
    }


    static class Latch extends Monitor implements ()=>Boolean {
        private var state:Boolean = false;

        public def release():Void {}

        public def await():Void {}

        public def apply():Boolean = state; // memory model?
    }


    static class Semaphore {
        private val lock = new Lock();

        private val threads = new Stack[Thread]();

        private var permits:Int;

        def this(n:Int) {}

        private static def min(i:Int, j:Int):Int = i<j ? i : j;

        def release(n:Int):Void {}

        def release():Void {}

        def reduce(n:Int):Void {}

        def acquire():Void {}

        def available():Int = permits;
    }


    static class ClockPhases extends HashMap[Clock,Int] {
        static def make(clocks:ValRail[Clock], phases:ValRail[Int]):ClockPhases! {}
        def register(clocks:ValRail[Clock]) {}
        def next() {}
        def drop() {}
    }
}