// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

//LIMITATION:

import harness.x10Test;

/**
 * @author bdlucas 8/2008
 */

public class GenericCast08 extends GenericTest {

    interface I[T] {
        def m(T):int;
    }

    interface J[T] {
        def m(T):int;
    }

    class A implements I[int], J[String] {
        public def m(int) = 0;
        public def m(String) = 1;
    }

    public def run() = {
        
        var a:Object = new A();
        var i:I[int]! = a as I[int];
        var j:J[String]! = a as J[String];

        check("i.m(0)", i.m(0), 0);
        check("j.m(0)", j.m("0"), 1);

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericCast08().execute();
    }
}
