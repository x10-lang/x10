/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;


// TODO rename Exception23 to Exception
@NativeRep("java", "java.lang.RuntimeException", null, "x10.rtt.Types.EXCEPTION")
@NativeRep("c++", "x10aux::ref<x10::lang::Exception>", "x10::lang::Exception", null)
public class Exception23 extends CheckedException {

    @Native("java", "new java.lang.RuntimeException()")
    public def this() {
        super();
    }

    @Native("java", "new java.lang.RuntimeException(#message)")
    public def this(message: String) {
        super(message);
    }

    @Native("java", "new java.lang.RuntimeException(#cause)")
    public def this(cause: CheckedThrowable) {
        super(cause);
    }

    @Native("java", "new java.lang.RuntimeException(#message, #cause)")
    public def this(message: String, cause: CheckedThrowable) {
        super(message, cause);
    }
    
}
