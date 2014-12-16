/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;


/**
 * Check lazy, per-field, per-place initialization semantics of static fields.
 * Valid for X10 2.2.3 and later.
 * 
 * @author mtake 6/2012
 */
public class InitStaticField extends x10Test {

    static val timea = System.currentTimeMillis();
    static val timeb = System.currentTimeMillis();
        
    public def run():Boolean {
        var ok:Boolean = true;
        
        val time = System.currentTimeMillis();
        Console.OUT.println(time);
        
        System.sleep(5000); // 5 secs
        
        val timea = InitStaticField.timea;
        Console.OUT.println(timea);
        if (timea - time < 3000) {
            Console.OUT.println("Lazy initialization check failed.");
            ok = false;
        }
        
        System.sleep(5000); // 5 secs

        val timeb = InitStaticField.timeb;      
        Console.OUT.println(timeb);
        if (timeb - timea < 3000) {
            Console.OUT.println("Per-field initialization check failed.");
            ok = false;
        }
        
        if (Place.numPlaces() >= 2) {
            val timea2 = at (Place.places().next(here)) { return InitStaticField.timea; };
            Console.OUT.println(timea2);
            if (timea2 == timea) {
                Console.OUT.println("Per-place initialization check failed.");
                ok = false;
            }
        }
        
        return ok;
    }

    public static def main(Rail[String]) {
        new InitStaticField().execute();
    }

}
