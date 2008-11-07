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
	@Native("java", "x10.runtime.impl.java.Runtime.setExitCode(#1)")
	public native static def setExitCode(code: int): void;
}
