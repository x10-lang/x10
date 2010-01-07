// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

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
