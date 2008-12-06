// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_165 extends x10Test {

    public def run():boolean {
        for (p:Place in Place.places)
            async(p)
                System.out.println("hi");
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_165().execute();
    }
}
