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

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;


/**
 * The class Exception and its subclasses are a form of Throwable that indicates conditions that
 * a reasonable application might want to catch.
 */
@NativeRep("java", "java.lang.RuntimeException", null, "x10.rtt.Types.EXCEPTION")
public class Exception extends CheckedException {

    /**
     * Construct an Exception with no detail message and no cause.
     */
    @Native("java", "new java.lang.RuntimeException()")
    public def this() { super(); }

    /**
     * Construct an Exception with the specified detail message and no cause.
     *
     * @param message the detail message
     */
    @Native("java", "new java.lang.RuntimeException(#message)")
    public def this(message: String) { super(message); }

    /**
     * Construct an Exception with no detail message and the specified cause.
     *
     * @param cause the cause
     */
    @Native("java", "new java.lang.RuntimeException(#cause)")
    public def this(cause: CheckedThrowable) { super(cause); }

    /**
     * Construct an Exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    @Native("java", "new java.lang.RuntimeException(#message, #cause)")
    public def this(message: String, cause: CheckedThrowable) { super(message, cause); }

    /**
     * Cast to Exception, or if not possible, wrap in WrappedThrowable.
     *
     * @param e Either gets wrapped or returned.
     */
    @Native("java", "x10.rtt.Types.EXCEPTION.isInstance(#e) ? (java.lang.RuntimeException)(#e) : new x10.lang.WrappedThrowable(#e)")
    public static def ensureException(e:CheckedThrowable) : Exception = e instanceof Exception ? e as Exception : new WrappedThrowable(e);
}

// vim:tabstop=4:shiftwidth=4:expandtab
