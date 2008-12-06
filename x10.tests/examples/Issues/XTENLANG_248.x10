// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_248 extends x10Test {

    public def run():boolean {
    
        val r = (0..2) - (1..1);
    
        System.out.println("r " + r);
        System.out.println("rect " + r.rect);
    
        System.out.print("indexes: ");
        for (val (i): Point in r) {
            System.out.print(i + " ");
            if (i==1) return false;
        }
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_248().execute();
    }
}
