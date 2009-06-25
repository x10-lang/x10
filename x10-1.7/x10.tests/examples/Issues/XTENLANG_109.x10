// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_109 extends x10Test {

    class R(zb:boolean) {
    
        def this(zb:boolean) = property(zb);
    
        incomplete static def m(min:int, max:int): R{zb==(min==0)};
    
        static def make(min:int, max:int): R{zb==(min==0)} {
            return m(min, max);
            //return m(min, max) as R{zb==(min==0)}; // this doesn't work either
        }
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_109().execute();
    }
}
