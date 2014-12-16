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

import x10.regionarray.*;
import x10.compiler.Pinned;

// NUM_PLACES: 4

/**
 * Remote accesses must be flagged by the compiler.
 *
 * @author Kemal 4/2005
 */
public class AsyncTest3 extends x10Test {

    public def run() {
        try {
            val A: DistArray[int](1) = DistArray.make[int](Dist.makeUnique());
            chk(Place.numPlaces() >= 2);
            chk(A.dist(0) == here);
            chk(A.dist(1) != here);
            val x= new X();

            finish async  { A(0) += 1n; }
            A(0) += 1n;
            finish async { A(1) += 1n; }
            A(1) += 1n; //  remote communication
            x10.io.Console.OUT.println("1");
            
            finish async  { A(x.zero()) += 1n; }
            A(x.zero()) += 1n;

            finish async  { A(0) += A(x.one()); }
            A(0) += A(x.one());//  remote communication
            x10.io.Console.OUT.println("2");
        
            chk(A(0) == 8n && A(1) == 2n);
            x10.io.Console.OUT.println("3");
            return false;
        } catch (z:MultipleExceptions) {
            return (z.exceptions.size == 1L && z.exceptions(0) instanceof BadPlaceException);
        }
    }

    public static def main(Rail[String]) {
        new AsyncTest3().execute();
    }

    /**
     * Dummy class to make static memory disambiguation difficult
     * for a typical compiler
     */
    @Pinned static class X {
        public var z: Rail[int] = [ 1n, 0n ];
        def zero() = z(z(z(1))); 
        def one() = z(z(z(0))); 
        def modify() { z(0)++; }
    }
}
