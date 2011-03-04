// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

import harness.x10Test;

/**
 * Testing that an at(b) d.x is legal.
 * @author vj
 */

public class AtCheck2_MustFailCompile extends x10Test {
	var x:AtCheck2_MustFailCompile =null;
    def m(b: AtCheck2_MustFailCompile, d:AtCheck2_MustFailCompile) {
    	val c = b;
	    at ( b) {
	     val e = d.x;
	    }
    }
    
    public def run()=true;

    public static def main(Rail[String]) {
        new AtCheck2_MustFailCompile().execute();
    }
}
