// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

public class XTENLANG_241 extends x10Test {

    public operator - this: XTENLANG_241! = this;
    
    public def bar(p: XTENLANG_241!) {
        val q = -p;
    }
    
    public def foo(p: Point) {
        val q = -p;
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_241().execute();
    }
}
