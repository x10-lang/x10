/*
 *
 * (C) Copyright IBM Corporation 2008
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test a the invocation of .equals on a method type parameter.
 *
 * @author nystrom 8/2008
 */
public class GenericMethodEquals extends x10Test {

    static class A {
        public def equals(Ref) {
            return true;
        }
    }

    static class B extends A {

    }

    public static def test[T] (a:T) {
        return a.equals(a);
    }

    public def run(): boolean = {
        return test[B](new B());
    }

    public static def main(var args: Rail[String]): void = {
        new GenericMethodEquals().execute();
    }
}

