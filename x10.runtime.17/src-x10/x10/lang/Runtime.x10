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

@NativeRep("java", "x10.runtime.impl.java.Runtime")
public value Runtime {
	// HACK to handle sleep calls
    @Native("java", "x10.lang.Activity.sleep(#1)")
    public native static def sleep(millis: long): boolean;

    @Native("java", "x10.runtime.impl.java.Runtime.exit(#1)")
    public native static def exit(code: int): void;

    @Native("java", "x10.runtime.impl.java.Runtime.setExitCode(#1)")
	public native static def setExitCode(code: int): void;
	
    @Native("java", "x10.runtime.impl.java.Runtime.MAX_PLACES")
	public const MAX_PLACES: int = dummy();
	
	private incomplete static def dummy(): int;
}
