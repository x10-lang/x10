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
import x10.compiler.NativeRep;
import x10.compiler.NonEscaping;
import x10.io.Printer;
import x10.io.Console;

@NativeRep("java", "java.lang.RuntimeException", null, "x10.rtt.Types.EXCEPTION")
@NativeRep("c++", "x10aux::ref<x10::lang::Exception>", "x10::lang::Exception", null)
public class Exception extends CheckedException {
    @Native("java", "#this.getCause()")
    @Native("c++", "(#this)->getCause()")
    val cause:Exception;

    @Native("java", "#this.getMessage()")
    @Native("c++", "(#this)->getMessage()")
    val message: String;

    @Native("java", "new java.lang.RuntimeException()")
    public def this() = this("");
    @Native("java", "new java.lang.RuntimeException(#message)")
    public def this(message: String) {
        super();
        this.cause = null;
        this.message = message;
    }
    @Native("java", "new java.lang.RuntimeException(#cause)")
    public def this(cause: Exception) = this("", cause);
    @Native("java", "new java.lang.RuntimeException(#message, #cause)")
    public def this(message: String, cause: Exception): Exception {
        super();
        	this.cause = cause;
        this.message = message;
    }
    
    @Native("java", "#this.getMessage()")
    @Native("c++", "(#this)->getMessage()")
    public def getMessage():String = message;
    
    @Native("java", "#this.getCause()")
    @Native("c++", "(#this)->getCause()")
    public final def getCause():Exception = cause;
    
    @Native("java", "#this.toString()")
    @Native("c++", "x10aux::to_string(#this)")
    public def toString():String {
        val m = getMessage();
        return m == null ? typeName() : typeName() + ": " + getMessage();
    }

    // @Native("java", "x10.core.ExceptionUtilities.getStackTrace(#this)")
    @Native("java", "#this.$getStackTrace()")
    @Native("c++", "(#this)->getStackTrace()")
    public final native def getStackTrace() : Array[String](1);

    @Native("java", "#this.printStackTrace()")
    @Native("c++", "(#this)->printStackTrace()")
    public native def printStackTrace() : void;

    // @Native("java", "x10.core.ExceptionUtilities.printStackTrace(#this, #p)")
    @Native("java", "#this.printStackTrace(#p)")
    @Native("c++",  "(#this)->printStackTrace(#p)")
    public native def printStackTrace(p: Printer) : void;

    @Native("java", "#this.fillInStackTrace()")
    @Native("c++", "(#this)->fillInStackTrace()")
    public native def fillInStackTrace() : Exception;

    /*
    public void setStackTrace(java.lang.StackTraceElement[]);
    */
}
