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

//OPTIONS: -STATIC_CHECKS

import x10.compiler.tests.*; // err markers
import harness.x10Test;

/**
 * See XTENLANG-2329 (Method guards are not type checked on calls to unary operators or closures.)
 * @author bdlucas 10/2008
 */
public class XTENLANG_2329_MustFailCompile(x:Long) extends x10Test {
    public operator - this{x==0} : Long = 1; 
    public operator this * (g:XTENLANG_2329_MustFailCompile){x==0} = 2;
    public operator this(i:Long){x==0} = 3;
    public operator this(i:Long) = (j:Long){x==0}  = 4;

    def test(g1:XTENLANG_2329_MustFailCompile, g2:XTENLANG_2329_MustFailCompile) {
        @ERR val a = -g1;
        @ERR val b = g1*g2;
        @ERR val c = g1(42);
        @ERR val d = g1(42)=43;
    }

    def closureTest(c: (i:Long){i==0} => Long, k:Long) {
        @ERR val a = c(k);
    }

    public def run() = true;

    public static def main(Rail[String]) {
        new XTENLANG_2329_MustFailCompile(5).execute();
    }
}
