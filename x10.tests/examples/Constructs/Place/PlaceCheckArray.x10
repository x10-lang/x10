// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

import harness.x10Test;

/**
 * @author bdlucas
 */

public class PlaceCheckArray extends x10Test {


    val a = Array.make[int](Dist.makeUnique());

    public def run01(): boolean {
        try {
            (future (Place.places(1)) a(0)).force();
        } catch (BadPlaceException) {
            return true;
        }
        System.out.println("01 fails");
        return false;
    }

    public def run02(): boolean {
        try {
            a(1);
        } catch (BadPlaceException) {
            return true;
        }
        System.out.println("02 fails");
        return false;
    }

    public def run(): boolean {
        a(0);
        (future (Place.places(1)) a(1)).force();
        return run01() && run02();
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new PlaceCheckArray().execute();
    }
}
