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
public /*value*/ class Throwable {
    @Native("java", "(#0).getCause()")
    @Native("c++", "(#0)->getCause()")
    val cause: Throwable;

    @Native("java", "#0.getMessage()")
    @Native("c++", "(#0)->getMessage()")
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
    
    @Native("java", "#0.getMessage()")
    @Native("c++", "(#0)->getMessage()")
    public def getMessage() = message;
    
    @Native("java", "(#0).getCause()")
    @Native("c++", "(#0)->getCause()")
    public final def getCause(): Throwable = cause;
    
    @Native("java", "#0.toString()")
    @Native("c++", "x10aux::to_string(#0)")
    public def toString() = className() + ": " + getMessage();
   
    @Native("java", "x10.core.ThrowableUtilities.getStackTrace(#0)")
    @Native("c++", "(#0)->getStackTrace()")
    public native def getStackTrace() : ValRail[String];

    @Native("java", "#0.printStackTrace()")
    @Native("c++", "(#0)->printStackTrace()")
    public native def printStackTrace() : Void;
    
    @Native("java", "#0.printStackTrace(new java.io.PrintStream((#1).getNativeOutputStream()))")
    @Native("c++", "do {\n"+
                   "    (#1)->println((#0)->toString());\n"+
                   "    x10aux::ref<ValRail<x10aux::ref<String> > > trace = (#0)->getStackTrace();\n"+
                   "    for (int i=0 ; i<trace->FMGL(length) ; ++i) {\n"+
                   "        (#1)->print(String::Lit(\"\\tat \"));\n"+
                   "        (#1)->println((*trace)[i]);\n"+
                   "    }\n"+
                   "} while (0)")
    public native def printStackTrace(p: Printer) : Void;

    /*
    public synchronized native java.lang.Throwable fillInStackTrace();
    public void setStackTrace(java.lang.StackTraceElement[]);
    */
}
