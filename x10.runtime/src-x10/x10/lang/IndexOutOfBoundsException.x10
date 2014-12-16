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
 * Thrown to indicate that an index of some sort (such as to an array, or to a
 * string) is out of range.
 */
@NativeRep("java", "java.lang.IndexOutOfBoundsException", null, "x10.rtt.Types.INDEX_OUT_OF_BOUNDS_EXCEPTION")
public class IndexOutOfBoundsException extends Exception {

    /**
     * Construct an IndexOutOfBoundsException with no detail message.
     */
    @Native("java", "new java.lang.IndexOutOfBoundsException()")
    public def this() { super(); }

    /**
     * Construct an IndexOutOfBoundsException with the specified detail message.
     *
     * @param message the detail message
     */
    @Native("java", "new java.lang.IndexOutOfBoundsException(#message)")
    public def this(message: String) { super(message); }
}

// vim:tabstop=4:shiftwidth=4:expandtab
