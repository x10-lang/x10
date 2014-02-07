/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.util;

import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;

@NativeCPPInclude("x10/lang/RuntimeNatives.h")
public class Timer {
    /**
     * Milliseconds since the Epoch: midnight, Jan 1, 1970. 
     */
    @Native("java", "java.lang.System.currentTimeMillis()")
    @Native("c++", "::x10::lang::RuntimeNatives::currentTimeMillis()")
    public native static def milliTime():Long;
        
    /** 
     * Current value of the system timer, in nanoseconds.  
     * May be rounded if system timer does not have nanosecond precision. 
     */
    @Native("java", "java.lang.System.nanoTime()")
    @Native("c++", "::x10::lang::RuntimeNatives::nanoTime()")
    public native static def nanoTime(): Long;
}
