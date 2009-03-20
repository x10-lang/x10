// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

//LIMITATION: cannot extend more than one instantiation of the same interface

import harness.x10Test;

/**
 * @author bdlucas 8/2008
 */

public class GenericCast02 extends GenericTest {

    interface I[T] {
        def m(T):int;
    }

    class A implements I[int], I[String] {
        public def m(int) = 0;
        public def m(String) = 1;
    }

    public def run() = {

        var a:Object = new A();
        var i1:I[int] = a as I[int];
        var i2:I[String] = a as I[String];
        check("i1.m(0)", i1.m(0), 0);
        check("i2.m(\"1\")", i2.m("1"), 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericCast02().execute();
    }
}
