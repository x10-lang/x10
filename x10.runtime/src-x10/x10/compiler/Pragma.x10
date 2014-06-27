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

package x10.compiler;

import x10.lang.annotations.StatementAnnotation;

public interface Pragma(pragma:Int) extends StatementAnnotation {
    // a finish with a unique async possibly remote
    public static FINISH_ASYNC = 1n;

    // a finish counting local events but ignoring remote events
    public static FINISH_HERE = 2n;

    // a finish without nested remote asyncs in remote asyncs
    public static FINISH_SPMD = 3n;

    // a finish without remote asyncs
    public static FINISH_LOCAL = 4n;

    // a finish implementation for large place counts
    // using two hops for finish update messages
    public static FINISH_DENSE = 5n;

    public static FINISH_ASYNC_AND_BACK = FINISH_HERE;
    public static FINISH_ATEACH_UNIQUE = FINISH_SPMD;
}
