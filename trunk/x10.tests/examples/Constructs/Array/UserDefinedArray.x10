/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * Testing arrays with user-defined element types.
 *
 * @author Christian Grothoff
 * @author Kemal Ebcioglu
 */

public class UserDefinedArray extends x10Test {

    const R = 0..1;
    const D = Dist.makeBlock(R, 0);

    public def run(): boolean = {

        chk(Place.MAX_PLACES <= 1 || D(0) != D(1));
        // create an array a such that
        // a[0] is in D[0] but points to an object in D[1]
        // and a[1] is in D[1] but points to an object in D[0]
        val v1: E = (future(D(1)){new E(1)}).force();
        val v2: E = (future(D(0)){new E(2)}).force();
        val a  = Array.make[E](D, ((i): Point)=> (i==0) ? v1 : v2);

        chk(a.dist(0) == D(0));
        chk((future(a.dist(0)){a(0)}).force() == v1);
        x10.io.Console.OUT.println("v1.home() " + v1.home() + " D(1) " + D(1));
        chk(v1.home() == D(1));
        chk((future(v1.home){v1.v}).force() == 1);


        chk(a.dist(1) == D(1));
        chk((future(a.dist(1)){a(1)}).force() == v2);
        chk(v2.home() == D(0));
        chk((future(v2.home){v2.v}).force() == 2);

        //this top level future runs in D[1] since a[0]==v1 && v1.home()==D[1]
        var i0: int = (future((future(a.dist(0)){a(0)}).force().home())
           { (future(a.dist(0)){a(0)}).force().v }).force();

        //this top level future runs in D[0] since a[1]==v2 && v2.home()==D[0]
        var i1: int = (future((future(a.dist(1)){a(1)}).force().home())
            { (future(a.dist(1)){a(1)}).force().v }).force();

        return i0 + 1 == i1;
    }

    public static def main(var args: Rail[String]): void = {
        new UserDefinedArray().execute();
    }

    static class E {
        var v: int;
        def this(var i: int): E = { v = i; }
    }
}
