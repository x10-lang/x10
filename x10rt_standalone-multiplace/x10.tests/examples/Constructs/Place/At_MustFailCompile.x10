// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

import harness.x10Test;

/**
 * Testing that remote field access (for this) is detected by the compiler.
 * 
 * @author vj
 */

public class At_MustFailCompile extends x10Test {
	var x:At_MustFailCompile =null;
    def m(b: At_MustFailCompile) =
	   at (b) {
    	 // We dont know that this local. 
	     this.x
    };
    
    public def run()=true;

    public static def main(Rail[String]) {
        new At_MustFailCompile().execute();
    }
}
