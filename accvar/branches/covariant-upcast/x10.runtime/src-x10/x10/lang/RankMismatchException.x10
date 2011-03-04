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

package x10.lang;

/**
 * @author Igor Peshansky
   @author vj -- This class should not be needed. X10 1.7 catches rank mismatches
   statically.
 */
public class RankMismatchException extends x10.lang.Exception {
    private global val p: Point;
    private global val r: Region;
    private global val d: Dist;
    private global val n: int;

    public def this(p_: Point, n_:int)  = {
        p = p_;
        r = null;
        d = null;
        n = n_;
    }

    public def this(r_: Region, n_:int) = {
        p = null;
        r = r_;
        d = null;
        n = n_;
    }

    public def this(d_: Dist, n_:Int) = {
        p = null;
        r = null;
        d = d_;
        n = n_;
    }

    public global safe def toString() =  "RankMismatchException(" +
        (p != null ? "point " + p :
         r != null ? "region " + r :
         "distribution " + d) +
         " accessed as rank " + n + ")";
}

