// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_130 extends x10Test {

    static value class S {}
    
    static value class R {
        public static def $convert(S): R;
        public static def $convert(int): R;
    }
    
    val r = new R();
    val s = new S();
    
    val e = r==s;
    val f = r==1;

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_130().execute();
    }
}
