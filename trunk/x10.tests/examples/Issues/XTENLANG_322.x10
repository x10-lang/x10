// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author tardieu 01/2009
 */

value V {}

class XTENLANG_322 extends x10Test {

    public def run(): boolean {
	val v = new V();
        return v.equals(v);
    }

    public static def main(Rail[String]) {
        new XTENLANG_322().execute();
    }
}
