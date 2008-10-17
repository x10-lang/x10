/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 runtime. It is 
 *  governed by the licence under which 
 *  X10 is released.
 *
 */
package x10.runtime.kernel;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * Runnable interface
 */
@NativeRep("java", "java.lang.Runnable")
public interface Runnable {
	
	@Native("java", "#0.run()")
	public def run():void;

}
 