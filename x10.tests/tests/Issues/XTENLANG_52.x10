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

import harness.x10Test;
import x10.compiler.NonEscaping;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_52 extends x10Test {

    class R(rank:int) {
        protected def this(rank:int) = property(rank);
    }
    
    class BD(rank:int) {
    
        static type R(rank:int) = R{self.rank==rank};
    
        @NonEscaping public final def get(): R(rank) { throw new Exception(); }
    
        val rs = [get()];
    
        protected def this(rank:int) = property(rank);
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_52().execute();
    }
}
