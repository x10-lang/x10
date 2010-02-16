// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author hhorii 02/2010
 */
public class XTENLANG_994 extends x10Test {

    public def run(): boolean {
    	val f = (()=>1.0)() as Float;
        return true;
    }
    
    public static def main(Rail[String]) {
        new XTENLANG_994().execute();
    }
}
