/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;
import x10.regionarray.*;

/**
 * Testing arrays with user-defined element types.
 *
 * @author Christian Grothoff
 * @author Kemal Ebcioglu
 */
import x10.util.concurrent.Future;
public class UserDefinedArray extends x10Test {

    static R = Region.make(0,1);
    static D = Dist.makeBlock(R, 0);

    public def run(): boolean = {

        chk(Place.numPlaces() <= 1 || D(0) != D(1));
        // create an array a such that
        // a[0] is in D[0] but points to an object in D[1]
        // and a[1] is in D[1] but points to an object in D[0]
        val v1: E = at(D(1)) new E(1n);
        val v2: E = at(D(0)) new E(2n);
        val a:DistArray[E](1)  = DistArray.make[E](D, ([i]: Point(1))=> (i==0L) ? v1 : v2);

        chk(a.dist(0) == D(0));
        chk(at(a.dist(0)) a(0).root == v1.root);
        x10.io.Console.OUT.println("v1.home() " + v1.home() + " D(1) " + D(1));
        chk(v1.home() == D(1));
        chk(at(v1.home())v1.v == 1n);


        chk(a.dist(1) == D(1));
        chk(at(a.dist(1)) a(1).root == v2.root);
        chk(v2.home() == D(0));
        chk(at(v2.home())v2.v == 2n);

        /*
         //this top level future runs in D[1] since a[0]==v1 && v1.home()==D[1]
        var i0: int = (future((future(a.dist(0)){a(0)}).force().home())
           { (future(a.dist(0)){a(0)}).force().v }).force(); 

        //this top level future runs in D[0] since a[1]==v2 && v2.home()==D[0]
        var i1: int = (future((future(a.dist(1)){a(1)}).force().home())
            { (future(a.dist(1)){a(1)}).force().v }).force();
         */
        
        //this top level future runs in D[1] since a[0]==v1 && v1.home()==D[1]
        var i0: int = at((at(a.dist(0))a(0)).home()) (at (a.dist(0)) a(0)).v;
        var i1: int = at((at(a.dist(1))a(1)).home()) (at (a.dist(1)) a(1)).v;


        return i0 + 1n == i1;
    }

    public static def main(var args: Rail[String]): void = {
        new UserDefinedArray().execute();
    }

    static class E {
    	private val root = GlobalRef[E](this);
    	def home() = root.home;
        var v: int;
        def this(var i: int): E = { v = i; }
    }
}
