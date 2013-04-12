/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

import harness.x10Test;
import x10.interop.Java;
import x10.array.*;

// MANAGED_X10_ONLY

public class JavaArray1 extends x10Test {

    def testArrayAccess(): Boolean {
        val xas = new Array[String](10);
        val xs = xas(0);
        xas(1) = "1";

        val jas = Java.newArray[String](10);
        val js = jas(0);
        jas(1) = "1";

        val xai = new Array[Int](10);
        val xi = xai(0);
        xai(1) = 1;

        val jai = Java.newArray[Int](10);
        val ji = jai(0);
        jai(1) = 1;

        return true;
    }

    def testInstanceOfArray(): Boolean {
        val xas = new Array[String](10);
        val ixas = xas instanceof Array[String];
        Console.OUT.println(ixas);

        val jas = Java.newArray[String](10);
        val ijas = jas instanceof Java.array[String];
        Console.OUT.println(ijas);

        val xai = new Array[Int](10);
        val ixai = xai instanceof Array[Int];
        Console.OUT.println(ixai);

        val jai = Java.newArray[Int](10);
        val ijai = jai instanceof Java.array[Int];
        Console.OUT.println(ijai);

        return true;
    }

    def testInstanceOfArray2(): Boolean {
        val jas = Java.newArray[String](10,20);
        val ijas = jas instanceof Java.array[Java.array[String]];
        Console.OUT.println(ijas);

        val jas0 = jas(0);
        val ijas0 = jas0 instanceof Java.array[String];
        Console.OUT.println(ijas0);
                
        val jai = Java.newArray[Int](10,20);
        val ijai = jai instanceof Java.array[Java.array[Int]];
        Console.OUT.println(ijai);

        val jai0 = jai(0);
        val ijai0 = jai0 instanceof Java.array[Int];
        Console.OUT.println(ijai0);

        return true;
    }

    def testArrayOfArray(): Boolean {
        val xajas = new Array[Java.array[String]](10,(long)=>Java.newArray[String](20));
        val ixajas = xajas instanceof Array[Java.array[String]];
        Console.OUT.println(ixajas);

        val xajai = new Array[Java.array[Int]](10,(long)=>Java.newArray[Int](20));
        val ixajai = xajai instanceof Array[Java.array[Int]];
        Console.OUT.println(ixajai);

        return true;
    }

    def testClosureOfArray(): Boolean {
        val cxas = (i:Int)=>new Array[String](i);
        val cxai = (i:Int)=>new Array[Int](i);
        val cjas = (i:Int)=>Java.newArray[String](i);
        val cjai = (i:Int)=>Java.newArray[Int](i);
        val cxasv = (ja:Array[String])=>{};
        val cxaiv = (ja:Array[Int])=>{};
        val cjasv = (ja:Java.array[String])=>{};
        val cjaiv = (ja:Java.array[Int])=>{};

        return true;
    }


    public def run(): Boolean {
        testArrayAccess();
        testInstanceOfArray();
        testInstanceOfArray2();
        testArrayOfArray();
        testClosureOfArray();
        return true;
    }

    public static def main(args: Rail[String]) {
        new JavaArray1().execute();
    }
}
