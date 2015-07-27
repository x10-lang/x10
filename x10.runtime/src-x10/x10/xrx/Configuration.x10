/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.xrx;

import x10.compiler.Native;

/**
 */
final class Configuration {

    @Native("c++", "PLATFORM_MAX_THREADS")
    private static PLATFORM_MAX_THREADS: Int = Int.MAX_VALUE;

    @Native("java", "java.lang.Runtime.getRuntime().availableProcessors()")
    @Native("c++", "::x10::lang::RuntimeNatives::availableProcessors()")
    private static native def availableProcessors():Int;

    @Native("java", "x10.runtime.impl.java.Runtime.loadenv()")
    @Native("c++", "::x10::lang::RuntimeNatives::loadenv()")
    static native def loadEnv():x10.util.HashMap[String,String];

    static def envOrElse(s:String, b:Boolean):Boolean {
        val v = Runtime.env.getOrElse(s, null);
        if (v == null) return b;
        return !(v.equalsIgnoreCase("false") || v.equalsIgnoreCase("f") || v.equals("0"));
    }

    @Native("java", "x10.runtime.impl.java.Runtime.sysPropOrElse(#s, #b)")
    static def sysPropOrElse(s:String, b:Boolean):Boolean {
        return b;
    }

    @Native("java", "x10.runtime.impl.java.Runtime.sysPropOrElse(#s, #i)")
    static def sysPropOrElse(s:String, i:Int):Int {
        return i;
    }

    static def strict_finish():Boolean = envOrElse("X10_STRICT_FINISH", false);

    static def stable_pool_size():Boolean = envOrElse("X10_STABLE_POOL_SIZE", false);

    static def static_threads():Boolean = envOrElse("X10_STATIC_THREADS", false);

    static def warn_on_thread_creation():Boolean = envOrElse("X10_WARN_ON_THREAD_CREATION", false);

    static def busy_waiting():Boolean = envOrElse("X10_BUSY_WAITING", false);

    /**
     * Enable support for job cancellation
     * Off by default to mitigate performance penalty
     */
    static def cancellable():Boolean { 
        val envVar = envOrElse("X10_CANCELLABLE", false);
        val sysProp = sysPropOrElse("X10_CANCELLABLE", envVar);
        return sysProp;
    }

    static def silenceInternalWarnings():Boolean {
        val envVar = envOrElse("X10_SILENCE_INTERNAL_WARNINGS", false);
        val sysProp = sysPropOrElse("x10.SILENCE_INTERNAL_WARNINGS", envVar);
        return sysProp;
    }

    static def nthreads():Int {
        var v:Int = 0n;
        try {
            v = Int.parse(Runtime.env.getOrElse("X10_NTHREADS", "0"));
        } catch (NumberFormatException) {
        }
        if (v <= 0) v = availableProcessors();
        if (v > PLATFORM_MAX_THREADS) v = PLATFORM_MAX_THREADS;
        return v;
    }

    static def max_threads():Int {
        var v:Int = 0n;
        try {
           v = Int.parse(Runtime.env.getOrElse("X10_MAX_THREADS", "0"));
       } catch (NumberFormatException) {
       }
       if (v <= 0) v = nthreads() + num_immediate_threads();
       if (!static_threads() && v < 1000) v = 1000n;
       if (v > PLATFORM_MAX_THREADS) v = PLATFORM_MAX_THREADS;
       return v;
    }

    static def num_immediate_threads():Int {
        var v:Int = 1n;
        try {
            val defVal = Runtime.x10rtBlockingProbeSupport() ? "1" : "0";
            v = Int.parse(Runtime.env.getOrElse("X10_NUM_IMMEDIATE_THREADS", defVal));
        } catch (NumberFormatException) {
        }
        return v;
    }
    
    // Note that "X10_RESILIENT_MODE" is also checked in x10rt/sockets/Launcher.cc
    static val RESILIENT_MODE_NONE = 0n;
    static val RESILIENT_MODE_DEFAULT     = 1n;  // Most stable implementation of resilient finish (see FinishResilient.x10)
    static val RESILIENT_MODE_PLACE0      = 11n; // FinishResilientPlace0
    static val RESILIENT_MODE_HC          = 12n; // FinishResilientHC
    static val RESILIENT_MODE_X10RT_ONLY  = 99n; // Resilient/Elastic X10RT, no resilient finish
    // The modes below are under development and not yet complete.
    static val RESILIENT_MODE_PLACE0_OPTIMIZED = 21n; // FinishResilientPlace0opt
    static val RESILIENT_MODE_HC_OPTIMIZED     = 22n; // FinishResilientHCopt
    static val RESILIENT_MODE_SAMPLE      = 91n; // FinishResilientSample + ResilientStorePlace0
    static val RESILIENT_MODE_SAMPLE_HC   = 92n; // FinishResilientSample + ResilientStoreHC

    static def resilient_mode():Int { // called from Runtime.x10
        var v:Int = RESILIENT_MODE_NONE;
        try {
            v = Int.parse(Runtime.env.getOrElse("X10_RESILIENT_MODE", "0"));
        } catch (NumberFormatException) {
        }
        v = sysPropOrElse("X10_RESILIENT_MODE", v);
        return v;
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
