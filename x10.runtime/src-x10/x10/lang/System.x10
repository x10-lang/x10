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
import x10.util.Timer;
import x10.util.Pair;

public class System {

    private def this() {}

    /**
     * Provides the current time in milliseconds.
     *
     * @return The current time in milliseconds.
     */
    public static def currentTimeMillis() = Timer.milliTime();

    /**
     * Provides the current time in nanoseconds, as precise as the system timers provide.
     *
     * @return The current time in nanoseconds.
     */
    public static def nanoTime() = Timer.nanoTime();

    /**
     * Terminates the application with a given exit code, as quickly as possible.
     * All finally blocks for the currently executing activities are executed.
     * LIMITATION: will only work if invoked from the main thread in place 0 (see XTENLANG-874).
     *
     * @deprecated
     * @see #exit()
     * @see #setExitCode(Int)
     */
    @Native("java", "java.lang.System.exit(#1)")
    @Native("c++", "x10aux::system_utils::exit(#1)")
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
    static def exit() = exit(-1);

    /**
     * Sets the system exit code.
     * The exit code will be returned from the application when main() terminates.
     * Can only be invoked in place 0.
     */
    @Native("java", "x10.runtime.impl.java.Runtime.setExitCode(#1)")
    @Native("c++", "(x10aux::exitCode = (#1))")
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
    @Native("java", "java.lang.Runtime.totalMemory()")
    @Native("c++", "x10aux::heap_size()")
    public static native def heapSize():long;

    /**
     * Sets the system property with the given name to the given value.
     *
     * @param p the name of the system property.
     * @param v the value for the system property.
     * TODO: @ return The previous value of the property, or null if it did not have one.
     */
    // TODO: XTENLANG-180.  Provide full System properties API in straight X10
    @Native("java", "java.lang.System.setProperty(#1,#2)")
    @Native("c++", "printf(\"not setting %s\\n\", (#1)->c_str())") // FIXME: Trivial definition to allow XRX compilation to go through.
    public static native def setProperty(p:String,v:String):void;


}
