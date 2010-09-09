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

import harness.x10Test;

class StructImplicitCoercionToInterface extends x10Test {
    static interface I { 
    def i():Int;
    }
    static struct S implements I {
        val i:Int;
    def this(i:Int) { this.i=i;}
    public def i() = i;
    public static operator (x:S):I = new I() {
        public def i() = x.i;
                public global safe def toString() = "<I i=" + x.i+">";
        };
    }
    static def q(x:I) {
   
    }
    public def run() {
     val x:I= S(4);
        q(x);
        q(S(5));
        return true;
    }
    public static def main(Rail[String]) {
       new StructImplicitCoercionToInterface().execute();
       
    }
}