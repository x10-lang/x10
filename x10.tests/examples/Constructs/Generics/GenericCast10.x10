// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

//LIMITATION: cannot extend more than one instantiation of the same interface

import harness.x10Test;

/**
 * @author bdlucas 8/2008
 */

public class GenericCast10 extends GenericTest {

    interface I[T] {
        def m(T):int;
    }

    class A implements I[int], I[String] {
        public def m(int) = 0;
        public def m(String) = 1;
    }

    public def run() = {

        var a:Object = new A();

        try {
            var i:I[float] = a as I[float];
        } catch (ClassCastException) {
            return true;
        }

        return false;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericCast10().execute();
    }
}
