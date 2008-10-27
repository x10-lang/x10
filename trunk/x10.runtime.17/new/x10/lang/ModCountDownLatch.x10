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

@NativeRep("java", "x10.runtime.ModCountDownLatch")
public class ModCountDownLatch {
	public native def this(i: int);
	
	@Native("java", "#0.updateCount()")
    public native def updateCount(): void;

	@Native("java", "#0.countDown()")
    public native def countDown(): void;

	@Native("java", "#0.await()")
    public native def await(): void throws InterruptedException;

	@Native("java", "#0.getCount()")
    public native def getCount(): int;
}
