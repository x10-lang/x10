// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

import harness.x10Test;

/**
 * @author bdlucas
 */

public class PlaceCheckValueClass extends x10Test {

    value class C {
        property p:int = 0;
        val x:int = 0;
        def foo() {}
        def foo(x:int) {}
        final def foo[T](x:T) {}
    }

    val c = new C();

    public def run01(): boolean = {
        val c = this.c;
        val f = future (Place.places(1)) {
            try {
                val a = c.p;
            } catch (e:BadPlaceException) {
                x10.io.Console.OUT.println("01 fails");
                return false;
            }
            return true;
        };
        return f.force();
    }

    public def run02(): boolean = {
        val c = this.c;
        val f = future (Place.places(1)) {
            try {
                val a = c.x;
            } catch (e:BadPlaceException) {
                x10.io.Console.OUT.println("02 fails");
                return false;
            }
            return true;
        };
        return f.force();
    }

    public def run04(): boolean = {
        val c = this.c;
        val f = future (Place.places(1)) {
            try {
                c.foo();
            } catch (e:BadPlaceException) {
                x10.io.Console.OUT.println("04 fails");
                return false;
            }
            return true;
        };
        return f.force();
    }

    public def run05(): boolean = {
        val c = this.c;
        val f = future (Place.places(1)) {
            try {
                c.foo(1);
            } catch (e:BadPlaceException) {
                x10.io.Console.OUT.println("05 fails");
                return false;
            }
            return true;
        };
        return f.force();

    }

    public def run06(): boolean = {
        val c = this.c;
        val f = future (Place.places(1)) {
            try {
                c.foo("1");
            } catch (e:BadPlaceException) {
                x10.io.Console.OUT.println("06 fails");
                return false;
            }
            return true;
        };
        return f.force();
    }

    public def run(): boolean {
    	if (Place.MAX_PLACES == 1) {
    		x10.io.Console.OUT.println("not enough places to run this test");
    		return false;
    	}
    	return run01() && run02() && run04() && run05() && run06();
	}

    //
    //
    //

    public static def main(var args: Rail[String]): void = {
        new PlaceCheckValueClass().execute();
    }
}
