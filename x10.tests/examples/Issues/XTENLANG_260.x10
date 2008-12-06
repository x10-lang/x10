// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_260 extends x10Test {

    public def run(): boolean = {
    
        val a = 99;
        val b = 1.2;
        val c = a / b;
        
        System.out.println("c==(a/b) " + (c==(a/b)));
        System.out.println("c-(a/b) " + (c-(a/b)));
        
        return c==(a/b);
    }

    public static def main(Rail[String]) {
        new XTENLANG_260().execute();
    }
}
