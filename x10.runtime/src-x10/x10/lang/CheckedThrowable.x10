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
import x10.io.Printer;
import x10.io.Console;


@NativeRep("java", "java.lang.Throwable", null, "x10.rtt.Types.CHECKED_THROWABLE")
@NativeRep("c++", "x10::lang::CheckedThrowable*", "x10::lang::CheckedThrowable", null)
public class CheckedThrowable {

    @Native("java", "#this.getCause()")
    @Native("c++", "::x10aux::nullCheck(#this)->FMGL(cause)")
    val cause:CheckedThrowable;

    @Native("java", "#this.getMessage()")
    @Native("c++", "::x10aux::nullCheck(#this)->FMGL(message)")
    val message:String;

    @Native("java", "new java.lang.Throwable()")
    public native def this();

    @Native("java", "new java.lang.Throwable(#message)")
    public native def this(message:String);

    @Native("java", "new java.lang.Throwable(#cause)")
    public native def this(cause:CheckedThrowable);

    @Native("java", "new java.lang.Throwable(#message, #cause)")
    public native def this(message:String, cause:CheckedThrowable);
    
    @Native("java", "#this.getMessage()")
    public native def getMessage():String;
    
    @Native("java", "#this.getCause()")
    public native final def getCheckedCause():CheckedThrowable;

    // @Native("java", "x10.runtime.impl.java.ThrowableUtils.getUncheckedCause(#this)")
    @Native("java", "x10.rtt.Types.EXCEPTION.isInstance(#this.getCause()) ? (java.lang.RuntimeException)(#this.getCause()) : new x10.lang.WrappedThrowable(#this.getCause())")
    public native final def getCause():Exception;
    
    @Native("java", "x10.runtime.impl.java.ThrowableUtils.toString(#this)")
    //@Native("java", "#this.toString()")
    // virtual method with @Native needs to be final in managed x10
    public final native def toString():String;

    @Native("java", "x10.runtime.impl.java.ThrowableUtils.getStackTrace(#this)")
    public final native def getStackTrace():Rail[String];

    @Native("java", "#this.printStackTrace()")
    public native def printStackTrace():void;

    @Native("java", "x10.runtime.impl.java.ThrowableUtils.printStackTrace(#this, #p)")
    // @Native("java", "#this.printStackTrace(#p)")
    // virtual method with @Native needs to be final in managed x10
    public final native def printStackTrace(p:Printer):void;

    @Native("java", "#this.fillInStackTrace()")
    public native def fillInStackTrace():CheckedThrowable;

    // work-around for XTENLANG-3086
    // this code should be uncommented in x10.lang.Runtime when bug is fixed
    @Native("java", "if (false) throw (#T)null; else { }")
    @Native("c++", "do { } while (0)")
    public static native def pretendToThrow[T](){T<:CheckedThrowable}:void;

    @Native("java", "false ? throw (#T)null : (#x)")
    @Native("c++", "(#x)")
    public static native def pretendToThrow[T,R](x:R){T<:CheckedThrowable}:R;
}
