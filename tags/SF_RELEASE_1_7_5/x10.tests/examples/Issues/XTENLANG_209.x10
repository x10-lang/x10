// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

public class XTENLANG_209 extends x10Test {

    public def $plus(c: int) = this;
    
    public def run():boolean {
        val i = 0;
        val sum = this + i;
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_209().execute();
    }
}
