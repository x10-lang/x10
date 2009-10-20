/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

import x10.io.Printer;
import x10.io.Console;

@NativeRep("java", "java.lang.Throwable", null, null)
@NativeRep("c++", "x10aux::ref<x10::lang::Throwable>", "x10::lang::Throwable", null)
public class Throwable {
    @Native("java", "new x10.lang.Box<java.lang.Throwable>(new x10.lang.Box.RTT(new x10.types.RuntimeType<java.lang.Throwable>(java.lang.Throwable.class)), #0.getCause())")
    @Native("c++", "(#0)->getCause()")
    // Box is to make it nullable
    global val cause: Box[Throwable];

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
    	this.cause = cause as Box[Throwable]; // BUG: should autobox
        this.message = message;
    }
    
    @Native("java", "#0.getMessage()")
    @Native("c++", "(#0)->getMessage()")
    public global def getMessage() = message;
    
    @Native("java", "new x10.lang.Box<java.lang.Throwable>(new x10.lang.Box.RTT(new x10.types.RuntimeType<java.lang.Throwable>(java.lang.Throwable.class)), #0.getCause())")
    @Native("c++", "(#0)->getCause()")
    public final global def getCause(): Box[Throwable] = cause;
    
    @Native("java", "#0.toString()")
    @Native("c++", "x10aux::to_string(#0)")
    public global def toString() = typeName() + ": " + getMessage();
   
    @Native("java", "x10.core.ThrowableUtilities.getStackTrace(#0)")
    @Native("c++", "(#0)->getStackTrace()")
    public global native def getStackTrace() : ValRail[String];

    @Native("java", "#0.printStackTrace()")
    @Native("c++", "(#0)->printStackTrace()")
    public global native def printStackTrace() : Void;
    
    @Native("java", "#0.printStackTrace(new java.io.PrintStream((#1).getNativeOutputStream()))")
    @Native("c++", "do {\n"+
                   "    (#1)->println((#0)->toString());\n"+
                   "    x10aux::ref<x10::lang::ValRail<x10aux::ref<x10::lang::String> > > trace = (#0)->getStackTrace();\n"+
                   "    for (int i=0 ; i<trace->FMGL(length) ; ++i) {\n"+
                   "        (#1)->print(x10::lang::String::Lit(\"\\tat \"));\n"+
                   "        (#1)->println((*trace)[i]);\n"+
                   "    }\n"+
                   "} while (0)")
    public global native def printStackTrace(p: Printer) : Void;

    @Native("java", "#0.fillInStackTrace()")
    @Native("c++", "(#0)->fillInStackTrace()")
    public native def fillInStackTrace() : Throwable;

    /*
    public void setStackTrace(java.lang.StackTraceElement[]);
    */
}
