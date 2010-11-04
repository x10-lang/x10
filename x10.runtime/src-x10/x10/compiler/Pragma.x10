/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.compiler;

import x10.lang.annotations.StatementAnnotation;

public interface Pragma(pragma:Int) extends StatementAnnotation {
    // a finish with a unique async possibly remote
    public static FINISH_ASYNC = 1;

    // a finish waiting for a unique async to terminate here
    // may contain other asyncs that will be implicitly uncounted
    public static FINISH_ASYNC_AND_BACK = 2;

    // a finish with 2 * Place.MAX_PLACES asyncs
    // e.g. finish ateach (p in Dist.makeUnique())
    public static FINISH_ATEACH_UNIQUE = 3;

    // a finish without remote asyncs
    public static FINISH_LOCAL = 4;
}
