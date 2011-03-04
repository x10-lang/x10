// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

import harness.x10Test;

/**
 * Testing that an at(b) c.x is legal, where val c = b;
 * @author vj
 */

public class AtCheck2 extends x10Test {
	var x:AtCheck =null;
    def m(b: AtCheck2) {
    	val c = b;
	    at ( b) {
	     val e = c.x;
	    }
    }
    
    public def run()=true;

    public static def main(Rail[String]) {
        new AtCheck2().execute();
    }
}
