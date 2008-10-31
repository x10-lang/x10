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

/*
 * I put this class in the kernel package instead of x10.lang because
 * I think (maybe?) that this exception can be confined to 
 * the XRX implementation and we won't have to expose it 
 * at the X10 language level.  If that turns out to not be true,
 * then we should move this class to x10.lang.
 */

/**
 * InterruptedException that may be thrown by NativeWorker.sleep.
 */
@NativeRep("java", "java.lang.InterruptedException")
public class InterruptedException extends Exception {

}
 