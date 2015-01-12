/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import x10.io.Printer;
import x10.regionarray.*;
import x10.io.StringWriter;


import x10.util.Box;

import harness.x10Test;


abstract public class TestDist extends x10Test {
    
    val os: GlobalRef[StringWriter];
    val out: GlobalRef[Printer];
    val testName = typeName();

    def this() {
        System.setProperty("line.separator", "\n");

        val tmp = new StringWriter();
        os = GlobalRef[StringWriter](tmp);
        out = GlobalRef[Printer](new Printer(tmp));
    }

    abstract def expected():String;

    def status() {
        val got = at (os.home) os().result();
        if (got.equals(expected())) {
            return true;
        } else {
            x10.io.Console.OUT.println("=== got:\n" + got);
            x10.io.Console.OUT.println("=== expected:\n" + expected());
            x10.io.Console.OUT.println("=== ");
            return false;
        }
    }

    //
    //
    //

    abstract class R {
	val testName:String;
	
        def this(test: String): R = {
	    testName = test;
	}

	def runTest() {
            var r:String;
            try {
                r = run();
            } catch (e: Exception) {
                r = e.getMessage();
            }
            pr(testName + " " + r);
        }

        abstract def run(): String;

    }
            
    class Grid {

        var os: Rail[Any] = new Rail[Any](10);

        def set(i0: long, vue: double): void = {
            os(i0) = new Box[double](vue);
        }

        def set(i0: long, i1: long, vue: double): void = {
            if (os(i0)==null) os(i0) = new Grid();
            val grid = os(i0) as Grid;
            grid.set(i1, vue);
        }

        def set(i0: long, i1: long, i2: long, vue: double): void = {
            if (os(i0)==null) os(i0) = new Grid();
            val grid = os(i0) as Grid;
            grid.set(i1, i2, vue);
        }

        def pr(rank: long): void = {
            var min: long = os.size;
            var max: long = 0L;
            for (var i: long = 0L; i<os.size; i++) {
                if (os(i)!=null) {
                    if (i<min) min = i;
                    else if (i>max) max = i;
                }
            }
            for (var i: long = 0L; i<os.size; i++) {
                var o: Any = os(i);
                if (o==null) {
                    if (rank==1)
                        print(".");
                    else if (rank==2) {
                        if (min<=i && i<=max)
                            print("    " + i + "\n");
                    }
                } else if (o instanceof Grid) {
                    if (rank==2)
                        print("    " + i + "  ");
                    else if (rank>=3) {
                        print("    ");
                        for (var j: long = 0; j<rank; j++)
                            print("-");
                        print(" " + i + "\n");
                    }
                    (o as Grid).pr(rank-1);
                } else {
                    val d = (o as Box[double]).value;
                    print(""+(d as int));
                }

                if (rank==1)
                    print(" ");
            }
            if (rank==1)
                print("\n");
        }
    }

    private def print(s:String):void {
        at (out.home) out().print(s);
    }

    def prArray(test: String, r: Region): Array[double]{rank==r.rank} = {
        return prArray(test, r, false);
    }

    def prArray(test: String, r: Region, bump: boolean): Array[double]{rank==r.rank} = {

        val init1 = (pt: Point) => {
            var v: long = 1;
            for (var i: long = 0; i<pt.rank; i++)
                v *= pt(i);
            return v%10 as double;
        };

        val init0 = (Point) => 0.0D;

        val a = new Array[double](r, bump? init0 : init1);
        prArray(test, a, bump);

        return a as Array[double]{rank==r.rank};
    }

    def prRegion(test: String, r: Region): void = {

        pr("--- " + testName + ": " + test);

        new R("rank")		{def run(): String = {return "" + r.rank;}}.runTest();
        new R("rect")		{def run(): String = {return "" + r.rect;}}.runTest();
        new R("zeroBased")	{def run(): String = {return "" + r.zeroBased;}}.runTest();
        new R("rail")		{def run(): String = {return "" + r.rail;}}.runTest();

        new R("isConvex()")	{def run(): String = {return "" + r.isConvex();}}.runTest();
        new R("size()")		{def run(): String = {return "" + r.size();}}.runTest();

        pr("region: " + r);
    }

    def prArray(test: String, a: Array[double]): void = {
        prArray(test, a, false);
    }

    def prArray(test: String, a: Array[double], bump: boolean): void = {

        val r: Region = a.region;

        prRegion(test, r);

        pr("  iterator");
        prArray1(a, bump);
    }

    def prArray1(a: Array[double], bump: boolean): void = {
        var grid: Grid = new Grid();
        for (p:Point in a.region) {
            if (p.rank==1) {
                val a2 = a as Array[double](1);
                if (bump) a2(p(0)) = a2(p(0)) + 1;
                grid.set(p(0), a2(p(0)));
            } else if (p.rank==2) {
                val a2 = a as Array[double](2);
                if (bump) a2(p(0), p(1)) = a2(p(0), p(1)) + 1;
                grid.set(p(0), p(1), a2(p(0),p(1)));
            } else if (p.rank==3) {
                val a2 = a as Array[double](3);
                if (bump) a2(p(0), p(1), p(2)) = a2(p(0), p(1), p(2)) + 1;
                grid.set(p(0), p(1), p(2), a2(p(0),p(1),p(2)));
            }
        }
        grid.pr(a.rank);
    }


    def prDist(test: String, d: Dist): void = {

        pr("--- " + test + ": " + d);

        val init = (Point) => -1.0D;
        val a = new Array[double](d.region, init);

        val ps = d.places();
        for (z in ps) {
            val r: Region = d.get(z);
            for (p:Point(r.rank) in r) {
                val q = p as Point(a.region.rank);
                a(q) = a(q) + z.id + 1;
            }
        }
        prArray1(a, false);
    }
        
    def pr(s:String):void {
        at (out.home) out().println(s);
    }

    static def xxx(s: String): void {
        x10.io.Console.OUT.println("xxx " + s);
    }

    // substitute for [a:b,c:d]
    def r(a: long, b: long, c: long, d: long): Region(2) {
        return Region.makeRectangular(a..b, c..d);
    }

}
