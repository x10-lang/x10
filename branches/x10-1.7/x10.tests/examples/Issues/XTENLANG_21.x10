// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_21 extends x10Test {

    val b = true;
    
    val init1 = (int)=>1;
    val init2 = (int)=>2;
    
    // this works
    val init = b? init1 : init2;
    
    // this does not
    val init3 = b? init1 : (int)=>3;

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_21().execute();
    }
}
