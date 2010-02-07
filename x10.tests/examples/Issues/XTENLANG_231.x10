// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author hhorii 02/2010
 */

class XTENLANG_231 extends x10Test {

    public def run(): boolean {
        3.hashCode();
        val i:Int = 10;
        i.hashCode();
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_231().execute();
    }
}
