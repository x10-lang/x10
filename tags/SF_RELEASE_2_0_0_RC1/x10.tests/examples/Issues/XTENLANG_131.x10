// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

class XTENLANG_131 extends x10Test {

    static class C {}
    
    public def run():boolean {
        val x = future(Place.places(1)) {new C()};
        val y = x.force();
        x10.io.Console.OUT.println("Place.places(1) " + Place.places(1));
        x10.io.Console.OUT.println("y.location() " + y.location());
        return Place.places(1)==y.location();
    }

    public static def main(Rail[String]) {
        new XTENLANG_131().execute();
    }
}
