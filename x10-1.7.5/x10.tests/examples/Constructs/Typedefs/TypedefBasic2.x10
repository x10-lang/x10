// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * Basic typdefs and type equivalence.
 *
 * Type definitions are applicative, not generative; that is, they define
 * aliases for types and do not introduce new types.
 *
 * @author bdlucas 9/2008
 */

public class TypedefBasic2 extends TypedefTest {

    public def run(): boolean = {
        
        class A[T] {
            val t:T;
            def this(t:T) = {this.t=t;}
        }


        type T1 = A[String];
        type T2 = A[String];

        var t0:A[String] = new A[String]("0");
        var t1:T1 = new T1("1");
        var t2:T2 = new T2("2");

        t0 = t1;
        t1 = t2;
        t2 = t0;

        check("t0.t", t0.t, "1");
        check("t1.t", t1.t, "2");
        check("t2.t", t2.t, "1");

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new TypedefBasic2().execute();
    }
}
