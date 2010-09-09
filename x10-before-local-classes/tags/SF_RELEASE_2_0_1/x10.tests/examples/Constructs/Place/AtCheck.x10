// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

import harness.x10Test;

/**
 * Testing that an at(b) b.x is legal.
 * @author vj
 */

public class AtCheck extends x10Test {
	var x:AtCheck =null;
    def m(b: AtCheck) =
	   at (b) {
	     b.x
    };
    
    public def run()=true;

    public static def main(Rail[String]) {
        new AtCheck().execute();
    }
}
