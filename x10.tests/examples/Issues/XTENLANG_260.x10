// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

class XTENLANG_260 extends x10Test {

    public def run(): boolean = {
    
        val a = 99;
        val b = 1.2;
        val c = a / b;
        
        x10.io.Console.OUT.println("c==(a/b) " + (c==(a/b)));
        x10.io.Console.OUT.println("c-(a/b) " + (c-(a/b)));
        
        val d = 1.0293402938409823;
        val e = 2.0932840928340983;
        val f = d * e;

        val g = 1.02934029384098230093423;
        val h = 2.0932840928340983309243;
        val i = g + h;

        val k = 1.02934029384098230093423;
        val j = -2.0932840928340983309243;
        val l = k - j;

        return c==(a/b) && f==(d*e) && i==(g+h) && l==(k-j);
    }

    public static def main(Rail[String]) {
        new XTENLANG_260().execute();
    }
}
