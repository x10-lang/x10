// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericOverPrimitives3x extends GenericTest {

    public def run(): boolean = {
        
        class A[T]{T<:(nat)=>char} {
            val t:T;
            def this(t:T) = {this.t=t;}
            def get(i:nat) = t(i);
        }

        a:A[String] = new A[String]("012");
        check("a.get(1)", a.get(1), '1');

        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new GenericOverPrimitives3x().execute();
    }
}
