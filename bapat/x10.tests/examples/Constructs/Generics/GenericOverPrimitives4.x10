// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericOverPrimitives4 extends GenericTest {

    public def run(): boolean = {
        
        class A[T] {
            val t:T;
            def this(t:T) = {this.t=t;}
            def get() = t;
        }

        a:A[boolean] = new A[boolean](true);
        check("a.get()", a.get(), true);

        return result;
    }


    public static def main(var args: Rail[String]): void = {
        new GenericOverPrimitives4().execute();
    }
}
