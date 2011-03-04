// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_131 extends x10Test {

    static class C {}
    
    public def run():boolean {
        val y = at (Place.places(1)) new C();
        x10.io.Console.OUT.println("Place.places(1) " + Place.places(1));
        x10.io.Console.OUT.println("y.home() " + y.home());
        return Place.places(1)==y.home();
    }

    public static def main(Rail[String]) {
        new XTENLANG_131().execute();
    }
}
