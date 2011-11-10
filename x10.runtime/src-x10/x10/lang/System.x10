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

import x10.compiler.Native;
import x10.io.Console;
import x10.util.HashMap;
import x10.util.Map;
import x10.util.Timer;
import x10.util.Pair;

public class System {

    private def this() {}

    /**
     * Provides the current time in milliseconds.
     *
     * @return The current time in milliseconds.
     */
    public static def currentTimeMillis():Long = Timer.milliTime();

    /**
     * Provides the current time in nanoseconds, as precise as the system timers provide.
     *
     * @return The current time in nanoseconds.
     */
    public static def nanoTime():Long = Timer.nanoTime();

    /**
     * Terminates the application with a given exit code, as quickly as possible.
     * All finally blocks for the currently executing activities are executed.
     * LIMITATION: will only work if invoked from the main thread in place 0 (see XTENLANG-874).
     *
     * @deprecated
     * @see #exit()
     * @see #setExitCode(Int)
     */
    @Native("java", "java.lang.System.exit(#code)")
    @Native("c++", "x10aux::system_utils::exit(#code)")
    static native def exit(code: Int): void;

    /**
     * Terminates the application with exit code -1, as quickly as possible.
     * Invoking this method is equivalent to invoking {@link #exit(Int)} with argument -1.
     * LIMITATION: will only work if invoked from the main thread in place 0 (see XTENLANG-874).
     *
     * @deprecated
     * @see #exit(Int)
     * @see #setExitCode(Int)
     */
    static def exit():void { exit(-1); }

    /**
     * Sets the system exit code.
     * The exit code will be returned from the application when main() terminates.
     * Can only be invoked in place 0.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.setExitCode(#exitCode)")
    @Native("c++", "(x10aux::exitCode = (#exitCode))")
    public static def setExitCode(exitCode: int){here==Place.FIRST_PLACE}: void {}

    /**
     * Provides an estimate in bytes of the size of the X10 heap
     * allocated to the current place. The accuracy of this estimate
     * is highly dependent on the implementation details of the
     * underlying memory management scheme being used by the X10 runtime,
     * and in some cases may simply return Long.MAX_VALUE or some other similarly
     * over conservative approximation.
     *
     * @return An upper bound in bytes on the size of the X10 heap allocated to the current place.
     */
    @Native("java", "java.lang.Runtime.getRuntime().totalMemory()")
    @Native("c++", "x10aux::heap_size()")
    public static native def heapSize():long;

    /**
     * Trigger a garbage collection.
     */
    @Native("java", "java.lang.System.gc()")
    @Native("c++", "x10aux::trigger_gc()")
    public static native def gc():void;

    /**
     * Returns an immutable map from environment variables to values.
     */
    public static def getenv():Map[String,String] = Runtime.env;

    /**
     * Sets the system property with the given name to the given value.
     *
     * @param p the name of the system property.
     * @param v the value for the system property.
     * TODO: @ return The previous value of the property, or null if it did not have one.
     */
    // TODO: XTENLANG-180.  Provide full System properties API in straight X10
    @Native("java", "java.lang.System.setProperty(#p,#v)")
    @Native("c++", "printf(\"not setting %s\\n\", (#p)->c_str())") // FIXME: Trivial definition to allow XRX compilation to go through.
    public static native def setProperty(p:String,v:String):void;

    /**
     * Sleep for the specified number of milliseconds.
     * [IP] NOTE: Unlike Java, x10 sleep() simply exits when interrupted.
     * @param millis the number of milliseconds to sleep
     * @return true if completed normally, false if interrupted
     */
    public static def sleep(millis:long):Boolean {
        try {
            Runtime.increaseParallelism();
            Thread.sleep(millis);
            Runtime.decreaseParallelism(1);
            return true;
        } catch (e:InterruptedException) {
            Runtime.decreaseParallelism(1);
            return false;
        }
    }
}
