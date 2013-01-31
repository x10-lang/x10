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
import x10.io.Printer;
import x10.io.Console;


@NativeRep("java", "java.lang.Throwable", null, "x10.rtt.Types.CHECKED_THROWABLE")
@NativeRep("c++", "x10::lang::CheckedThrowable*", "x10::lang::CheckedThrowable", null)
public class CheckedThrowable {

    @Native("java", "#this.getCause()")
    @Native("c++", "(#this)->getCause()")
    val cause:CheckedThrowable;

    @Native("java", "#this.getMessage()")
    @Native("c++", "(#this)->getMessage()")
    val message: String;


    @Native("java", "new java.lang.Throwable()")
    public def this() = this("");

    @Native("java", "new java.lang.Throwable(#message)")
    public def this(message: String) {
        super();
        this.cause = null;
        this.message = message;
    }

    @Native("java", "new java.lang.Throwable(#cause)")
    public def this(cause: CheckedThrowable) = this("", cause);

    @Native("java", "new java.lang.Throwable(#message, #cause)")
    public def this(message: String, cause: CheckedThrowable) {
        super();
        this.cause = cause;
        this.message = message;
    }
    
    @Native("java", "#this.getMessage()")
    @Native("c++", "(#this)->getMessage()")
    public def getMessage():String = message;
    
    @Native("java", "#this.getCause()")
    @Native("c++", "(#this)->getCause()")
    public final def getCheckedCause():CheckedThrowable = cause;

    // @Native("java", "x10.runtime.impl.java.ThrowableUtils.getUncheckedCause(#this)")
    @Native("java", "x10.rtt.Types.EXCEPTION.isInstance(#this.getCause()) ? (java.lang.RuntimeException)(#this.getCause()) : new x10.lang.WrappedThrowable(#this.getCause())")
    // @Native("c++", "(#this)->getCause()")
    public final def getCause():Exception = Exception.ensureException(cause);
    
    @Native("java", "x10.runtime.impl.java.ThrowableUtils.toString(#this)")
    //@Native("java", "#this.toString()")
    @Native("c++", "x10aux::to_string(#this)")
    // virtual method with @Native needs to be final in managed x10
    public final native def toString():String;

    @Native("java", "x10.runtime.impl.java.ThrowableUtils.getStackTrace(#this)")
    //@Native("java", "#this.$getStackTrace()")
    @Native("c++", "(#this)->getStackTrace()")
    public final native def getStackTrace() : Rail[String];

    @Native("java", "#this.printStackTrace()")
    @Native("c++", "(#this)->printStackTrace()")
    public native def printStackTrace() : void;

    @Native("java", "x10.runtime.impl.java.ThrowableUtils.printStackTrace(#this, #p)")
    // @Native("java", "#this.printStackTrace(#p)")
    @Native("c++",  "(#this)->printStackTrace(#p)")
    // virtual method with @Native needs to be final in managed x10
    public final native def printStackTrace(p: Printer) : void;

    @Native("java", "#this.fillInStackTrace()")
    @Native("c++", "(#this)->fillInStackTrace()")
    public native def fillInStackTrace() : CheckedThrowable;

    // work-around for XTENLANG-3086
    // this code should be uncommented in x10.lang.Runtime when bug is fixed
    @Native("java", "if (false) throw (#T)null; else { }")
    @Native("c++", "do { } while (0)")
    public static native def pretendToThrow[T] () { T<: CheckedThrowable } : void;

}
