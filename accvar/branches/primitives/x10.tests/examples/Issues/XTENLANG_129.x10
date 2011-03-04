// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_129 extends x10Test {

    static class R(rank:int) {
        def this(rank:int) = property(rank);
    }
    
    incomplete def z(): R{rank==1};
    
    val w: (int)=>R{rank==1} = (int)=>z();

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_129().execute();
    }
}
