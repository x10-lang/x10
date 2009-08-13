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

/**
 * Create a TuningFork logger object for use in instrumenting X10 programs.
 */
@NativeRep("java", "com.ibm.tuningfork.tracegen.types.EventTypeSpaceVersion", null, null)
public value EventTypeSpaceVersion {
	
    public native def this(name:String, version:int);
}
