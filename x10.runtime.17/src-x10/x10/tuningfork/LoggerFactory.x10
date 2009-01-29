/*
 *
 * (C) Copyright IBM Corporation 2009
 *
 *  This file is part of X10 runtime. It is 
 *  governed by the licence under which 
 *  X10 is released.
 *
 */
package x10.tuningfork;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.io.IOException;

/**
 * Create a TuningFork logger object for use in instrumenting X10 programs.
 */
@NativeRep("java", "com.ibm.tuningfork.tracegen.LoggerFactory", null, null)
public class LoggerFactory {
	
	@Native("java", "#0.makeFileLogger(#1)")
	public native static def makeFileLogger(fileName:String):Logger throws IOException;
}
