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
 * Thrown to indicate that an attempt has been made to 
 * create a Rail or Array with a negative size.
 */
@NativeRep("java", "java.lang.NegativeArraySizeException", null, "x10.rtt.Types.NEGATIVE_ARRAY_SIZE_EXCEPTION")
public class NegativeArraySizeException extends Exception {

    /**
     * Construct an NegativeArraySizeException with no detail message.
     */
    @Native("java", "new java.lang.NegativeArraySizeException()")
    public def this() { super(); }
}

// vim:tabstop=4:shiftwidth=4:expandtab
