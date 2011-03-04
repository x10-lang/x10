// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_52 extends x10Test {

    class R(rank:nat) {
        protected def this(rank:nat) = property(rank);
    }
    
    class BD(rank:nat) {
    
        static type R(rank:nat) = R{self.rank==rank};
    
        incomplete public def get(): R(rank);
    
        val rs: Rail[R] = [get()];
        //workaround: val rs:Rail[R] = Rail.makeVal[R](1, (nat)=&gt;get());
    
        protected def this(rank:nat) = property(rank);
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_52().execute();
    }
}
