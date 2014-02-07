/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;


/*
 * This class is in the runtime package instead of x10.lang because
 * we think (maybe?) that this exception can be confined to
 * the XRX implementation and we won't have to expose it
 * at the X10 language level.  If that turns out to not be true,
 * then we should move this class to x10.lang.
 */

/**
 * InterruptedException that may be thrown by NativeWorker.sleep.
 */
class InterruptedException extends Exception {

    public def this() { super(); }

    public def this(message: String) { super(message); }

}

// vim:shiftwidth=4:tabstop=4:expandtab
