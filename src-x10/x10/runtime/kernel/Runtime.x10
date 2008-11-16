/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.runtime.kernel;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * Interface with native runtime
 * @author tardieu
 */
@NativeRep("java", "x10.runtime.impl.java.Runtime", null, null)
public value Runtime {

	/**
	 * Set system exit code
	 */
	@Native("java", "#0.setExitCode(#1)")
	public native static def setExitCode(code: int): void;

	// Configuration options

	@Native("java", "#0.PLACE_CHECKS")
    public const PLACE_CHECKS = true;

	@Native("java", "#0.MAX_PLACES")
    public const MAX_PLACES = 4;

	@Native("java", "#0.INIT_THREADS")
    public const INIT_THREADS = 3;
    
    /**
     * Run body at place(id).
     * Wait for body to terminate.
     * Must use current thread if in the same node!!!
     */
	@Native("java", "#0.runAt(#1, #2)")
	public native static def runAt(id:Int, body:()=>Void):Void;

	/**
	 * Return true if place(id) is in the current node.
	 */
	@Native("java", "#0.local(#1)")
	public native static def local(id:Int):Boolean;
}
