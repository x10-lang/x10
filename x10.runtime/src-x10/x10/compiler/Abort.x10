/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.compiler;

/** 
 * A class that is used to implement continuations (bypass finally blocks).
 * 
 * NOT INTENDED FOR USE BY X10 PROGRAMMERS
 */
public class Abort extends x10.lang.Exception {
    public static ABORT = new Abort();

    private def this() {}
}
