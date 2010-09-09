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

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_52 extends x10Test {

    class R(rank:int) {
        protected def this(rank:int) = property(rank);
    }
    
    class BD(rank:int) {
    
        static type R(rank:int) = R{self.rank==rank};
    
        incomplete public def get(): R(rank);
    
        val rs: Rail[R] = [get()];
        //workaround: val rs:Rail[R] = ValRail.make[R](1, (int)=&gt;get());
    
        protected def this(rank:int) = property(rank);
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_52().execute();
    }
}
