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

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;


/**
 * Thrown to indicate that a string has been accessed with an illegal index.
 * For example, the point is outside of the string's region.
 */
@NativeRep("java", "java.lang.StringIndexOutOfBoundsException", null, "x10.rtt.Types.STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION")
public class StringIndexOutOfBoundsException extends IndexOutOfBoundsException {

    /**
     * Construct a StringIndexOutOfBoundsException with no detail message.
     */
    @Native("java", "new java.lang.StringIndexOutOfBoundsException()")
    public def this() { super(); }

    /**
     * Construct a StringIndexOutOfBoundsException with the specified detail message.
     *
     * @param message the detail message
     */
    @Native("java", "new java.lang.StringIndexOutOfBoundsException(#message)")
    public def this(message: String) { super(message); }
}

// vim:tabstop=4:shiftwidth=4:expandtab
