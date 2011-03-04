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
public value Throwable {
    @Native("java", "(x10.core.Box<java.lang.Throwable>) x10.core.Box.<java.lang.Throwable>make(new x10.core.Box.RTT(new x10.types.RuntimeType<java.lang.Throwable>(java.lang.Throwable.class)), #0.getCause())")
    @Native("c++", "(#0)->getCause()")
    // Box is to make it nullable
	val cause: Box[Throwable];

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
    	this.cause = cause to Box[Throwable]; // BUG: should autobox
    	this.message = message;
    }
    
    @Native("java", "#0.getMessage()")
    @Native("c++", "(#0)->getMessage()")
    public def getMessage() = message;
    
    @Native("java", "(x10.core.Box<java.lang.Throwable>) x10.core.Box.<java.lang.Throwable>make(new x10.core.Box.RTT(new x10.types.RuntimeType<java.lang.Throwable>(java.lang.Throwable.class)), #0.getCause())")
    @Native("c++", "(#0)->getCause()")
    public final def getCause(): Box[Throwable] = cause;
    
    @Native("java", "#0.toString()")
    @Native("c++", "(#0)->toString()")
    public def toString() = className() + ": " + getMessage();
   
    @Native("java", "x10.core.ThrowableUtilities.getStackTrace(#0)")
    @Native("c++", "(#0)->getStackTrace()")
    public native def getStackTrace() : ValRail[String];

    @Native("java", "#0.printStackTrace()")
    @Native("c++", "do { fprintf(stdout,\"%s\\n\",(#0)->toString()->c_str()); x10aux::ref<ValRail<x10aux::ref<String> > > trace = (#0)->getStackTrace(); for (int i=0 ; i<trace->FMGL(length) ; ++i) fprintf(stdout,\"        at %s\\n\",(*trace)[i]->c_str()); } while (0)")
    public native def printStackTrace() : Void;
    
    @Native("java", "#0.printStackTrace(new java.io.PrintStream((#1).getNativeOutputStream()))")
    @Native("c++", "do {    (#1)->println((#0)->toString());    x10aux::ref<ValRail<x10aux::ref<String> > > trace = (#0)->getStackTrace();    for (int i=0 ; i<trace->FMGL(length) ; ++i) {        (#1)->print(String(\"        at \");        (#1)->println((*trace)[i]);    }} while (0)")
    public native def printStackTrace(p: Printer) : Void;

    /*
    public synchronized native java.lang.Throwable fillInStackTrace();
    public java.lang.StackTraceElement[] getStackTrace();
    public void setStackTrace(java.lang.StackTraceElement[]);
    */

}
