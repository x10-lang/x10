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

// XTENLANG-2686: x10.lang.Throwable is now mapped to x10.core.X10Throwable, which is a subclass of x10.core.Throwable
@NativeRep("java", "x10.core.X10Throwable", null, "x10.core.X10Throwable.$RTT")
@NativeRep("c++", "x10aux::ref<x10::lang::Throwable>", "x10::lang::Throwable", null)
public class Throwable {
    @Native("java", "#this.getCause()")
    @Native("c++", "(#this)->getCause()")
    val cause:Throwable;

    @Native("java", "#this.getMessage()")
    @Native("c++", "(#this)->getMessage()")
    val message: String;

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
    
    @Native("java", "#this.getMessage()")
    @Native("c++", "(#this)->getMessage()")
    public def getMessage():String = message;
    
    @Native("java", "#this.getCause()")
    @Native("c++", "(#this)->getCause()")
    public final def getCause():Throwable = cause;
    
    @Native("java", "#this.toString()")
    @Native("c++", "x10aux::to_string(#this)")
    public def toString():String {
        val m = getMessage();
        return m == null ? typeName() : typeName() + ": " + getMessage();
    }

    // @Native("java", "x10.core.ThrowableUtilities.getStackTrace(#this)")
    @Native("java", "#this.$getStackTrace()")
    @Native("c++", "(#this)->getStackTrace()")
    public final native def getStackTrace() : Array[String](1);

    @Native("java", "#this.printStackTrace()")
    @Native("c++", "(#this)->printStackTrace()")
    public native def printStackTrace() : void;

    // @Native("java", "x10.core.ThrowableUtilities.printStackTrace(#this, #p)")
    @Native("java", "#this.printStackTrace(#p)")
    @Native("c++",  "(#this)->printStackTrace(#p)")
    public native def printStackTrace(p: Printer) : void;

    @Native("java", "#this.fillInStackTrace()")
    @Native("c++", "(#this)->fillInStackTrace()")
    public native def fillInStackTrace() : Throwable;

    /*
    public void setStackTrace(java.lang.StackTraceElement[]);
    */
}
