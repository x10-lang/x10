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

import x10.io.Printer;
import x10.io.Console;

@NativeRep("java", "java.lang.Throwable", null, null)
@NativeRep("c++", "x10aux::ref<x10::lang::Throwable>", "x10::lang::Throwable", null)
public class Throwable {
    @Native("java", "#0.getCause()")
    @Native("c++", "(#0)->getCause()")

    global val cause:Throwable;

    @Native("java", "#0.getMessage()")
    @Native("c++", "(#0)->getMessage()")
    global val message: String;

    public def this() = this("");
    public def this(message: String) {
        super();
        this.cause = null;
        this.message = message;
    }
    public def this(cause: Throwable) = this("", cause);
    public def this(message: String, cause: Throwable): Throwable {
        super();
    	this.cause = cause;
        this.message = message;
    }
    
    @Native("java", "#0.getMessage()")
    @Native("c++", "(#0)->getMessage()")
    public global safe def getMessage() = message;
    
    @Native("java", "#0.getCause()")
    @Native("c++", "(#0)->getCause()")
    public final global def getCause():Throwable = cause;
    
    @Native("java", "#0.toString()")
    @Native("c++", "x10aux::to_string(#0)")
    public global safe def toString() = typeName() + ": " + getMessage();
   
    @Native("java", "x10.core.ThrowableUtilities.getStackTrace(#0)")
    @Native("c++", "(#0)->getStackTrace()")
    public global native def getStackTrace() : ValRail[String];

    @Native("java", "#0.printStackTrace()")
    @Native("c++", "(#0)->printStackTrace()")
    public global native def printStackTrace() : Void;
    
    @Native("java", "x10.core.ThrowableUtilities.printStackTrace(#0, #1)")
    @Native("c++",  "(#0)->printStackTrace(#1)")
    public global native def printStackTrace(p: Printer) : Void;

    @Native("java", "#0.fillInStackTrace()")
    @Native("c++", "(#0)->fillInStackTrace()")
    public native def fillInStackTrace() : Throwable;

    /*
    public void setStackTrace(java.lang.StackTraceElement[]);
    */
}
