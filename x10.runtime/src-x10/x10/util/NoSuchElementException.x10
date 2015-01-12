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

package x10.util;

import x10.compiler.Native;
import x10.compiler.NativeRep;


@NativeRep("java", "java.util.NoSuchElementException", null, "x10.rtt.Types.NO_SUCH_ELEMENT_EXCEPTION")
public class NoSuchElementException extends Exception {

    @Native("java", "new java.util.NoSuchElementException()")
    public def this() { super(); }

    @Native("java", "new java.util.NoSuchElementException(#message)")
    public def this(message: String) { super(message); } 

}