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

import x10.util.NoSuchElementException;

/**
 * @author tardieu
 */
final class Configuration {

    @Native("c++", "PLATFORM_MAX_THREADS")
    private static PLATFORM_MAX_THREADS: Int = Int.MAX_VALUE;

    @Native("c++", "DEFAULT_STATIC_THREADS")
    private static DEFAULT_STATIC_THREADS: Boolean = false;

    @Native("java", "x10.runtime.impl.java.Runtime.loadenv()")
    @Native("c++", "x10aux::loadenv()")
    static native def loadEnv():x10.util.HashMap[String,String];

    static def envOrElse(s:String, b:Boolean):Boolean {
        try {
            val v = Runtime.env.getOrThrow(s);
            return !(v.equalsIgnoreCase("false") || v.equalsIgnoreCase("f") || v.equals("0"));
        } catch (NoSuchElementException) {
        }
        return b;
    }

    static def strict_finish():Boolean = envOrElse("X10_STRICT_FINISH", false);

    static def static_threads():Boolean = envOrElse("X10_STATIC_THREADS", DEFAULT_STATIC_THREADS);

    static def warn_on_thread_creation():Boolean = envOrElse("X10_WARN_ON_THREAD_CREATION", false);

    static def busy_waiting():Boolean = envOrElse("X10_BUSY_WAITING", false);

    static def nthreads():Int {
        var v:Int = 0;
        try {
            v = Int.parse(Runtime.env.getOrThrow("X10_NTHREADS"));
        } catch (NoSuchElementException) {
        } catch (NumberFormatException) {
        }
        if (v <= 0) v = 1;
        if (v > PLATFORM_MAX_THREADS) v = PLATFORM_MAX_THREADS;
        return v;
    }

    static def max_threads():Int {
        var v:Int = 0;
        try {
           v = Int.parse(Runtime.env.getOrThrow("X10_MAX_THREADS"));
       } catch (NoSuchElementException) {
       } catch (NumberFormatException) {
       }
       if (v <= 0) v = nthreads();
       if (!static_threads() && v < 1000) v = 1000;
       if (v > PLATFORM_MAX_THREADS) v = PLATFORM_MAX_THREADS;
       return v;
    }
}

// vim:shiftwidth=4:tabstop=4:expandtab
