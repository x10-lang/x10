// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

import x10.compiler.ArithmeticOps;

/**
 * @author bdlucas 8/2008
 */

public class GenericInstanceof extends GenericTest {

    //
    // 01
    //


    interface I[T] {
        def m(T):int;
    }

    class A implements I[int] {
        public def m(int) = 0;
    }

    public def run() = {

        var a:Object = new A();

        return a instanceof I[int];
    }


    //
    // 02
    //

    interface I[T] {
        def m(T):int;
    }

    class A implements I[int], I[String] {
        public def m(int) = 0;
        public def m(String) = 1;
    }

    public def run() = {

        var a:Object = new A();

        return a instanceof I[int] && a instanceof I[String];
    }


    //
    // 03
    //

    interface I[T] {
        def m(T):int;
    }

    class A[T] implements I[T] {
        public def m(T) = 0;
    }

    public def run() = {

        var a:Object = new A[int]();

        return a instanceof I[int];
    }


    //
    // 04
    //

    interface I[T] {
        def m(T):int;
        def n(T):int;
    }

    interface J[T] {
        def m(T):int;
        def o(T):int;
    }

    class A implements I[int], J[int] {
        public def m(int) = 0;
        public def n(int) = 1;
        public def o(int) = 2;
    }

    public def run() = {

        var a:Object = new A();

        return a instanceof I[int] && a instanceof J[int];
    }


    //
    // 05
    //


    interface I[T] {
        def m(T):int;
        def n(T):int;
    }

    interface J[T] {
        def m(T):int;
        def o(T):int;
    }

    class A[T] implements I[T], J[T] {
        public def m(T) = 0;
        public def n(T) = 1;
        public def o(T) = 2;
    }

    public def run() = {

        var a:Object = new A[int]();

        return a instanceof I[int] && a instanceof J[int];
    }



    //
    // 06
    //


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

        return a instanceof I[int] && a instanceof J[String];
    }


    public static def main(var args: Rail[String]): void = {
        new GenericInstanceof().execute();
    }
}
