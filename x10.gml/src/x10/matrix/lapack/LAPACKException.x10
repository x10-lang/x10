/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 */

package x10.matrix.lapack;

/**
 * Thrown when a call from the Global Matrix Library to LAPACK fails.
 */
public class LAPACKException extends Exception {

    /**
     * Construct an LAPACKException with the specified detail message.
     *
     * @param message the detail message
     */
    public def this(info:Int, message:String) { super("LAPACK call failed with info code " + info + ": " + message); }
}

// vim:tabstop=4:shiftwidth=4:expandtab
