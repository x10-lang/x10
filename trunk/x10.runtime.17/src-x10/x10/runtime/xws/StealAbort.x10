/*
 *
 * (C) Copyright IBM Corporation 2007
 *
 *  This file is part of X10 runtime. It is 
 *  governed by the licence under which 
 *  X10 is released.
 *
 */
package x10.runtime.xws;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
* An instance of StealAbort is thrown by the victim when it discovers that the current
 * frame has been stolen. This causes the call stack to be unwound. The exception is
 * caught in the scheduler and should not be handled by client code. 
 * Since the exception is not meant to carry any meaningful information,
 * a single exception is precreated and used to respond to all steals.
 *
 * Currently just an x10 facade using NativeRep around
 * the Java implementation.  Eventually will mostly
 * migrate to being in x10.
 */
@NativeRep("java", "x10.runtime.xws.impl.StealAbort")
public class StealAbort extends Exception {
	
}
