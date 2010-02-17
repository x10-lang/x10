/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * @author bdlucas
 */

public class PlaceCheckInRail extends x10Test {


    class C {
        val x:int = 0;
        def foo() = 0;
    }


    public def run01(): boolean {

        val r = Rail.make[C](3, (int)=>new C());

        try {
            (future (Place.places(1)) r(0).x).force();
        } catch (BadPlaceException) {
            return true;
        }
        x10.io.Console.OUT.println("01 fails");
        return false;
    }


    public def run02(): boolean {

        val r = Rail.make[C](3, (int)=>new C());

        try {
            (future (Place.places(1)) r(0).foo()).force();
        } catch (BadPlaceException) {
            return true;
        }
        x10.io.Console.OUT.println("01 fails");
        return false;
    }


    public def run03(): boolean {

        val r = ValRail.make[C](3, (int)=>new C());

        try {
            (future (Place.places(1)) r(0).x).force();
        } catch (BadPlaceException) {
            return true;
        }
        x10.io.Console.OUT.println("01 fails");
        return false;
    }


    public def run04(): boolean {

        val r = ValRail.make[C](3, (int)=>new C());

        try {
            (future (Place.places(1)) r(0).foo()).force();
        } catch (BadPlaceException) {
            return true;
        }
        x10.io.Console.OUT.println("01 fails");
        return false;
    }


    public def run(): boolean {
    	if (Place.MAX_PLACES == 1) {
    		x10.io.Console.OUT.println("not enough places to run this test");
    		return false;
    	}
    	return run01() && run02() && run03() && run04();
	}

    public static def main(var args: Rail[String]): void = {
        new PlaceCheckInRail().execute();
    }
}
