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

import x10.io.Printer;
import x10.regionarray.*;
import x10.io.StringWriter;


import x10.util.Box;

import harness.x10Test;
import x10.compiler.Pinned;
import x10.compiler.Global;

abstract public class TestArray extends x10Test {
    private val root = GlobalRef[TestArray](this);
    
    val os: StringWriter;
    val out: Printer;
    transient val testName:String = "TestArray";

    def this() {
        System.setProperty("line.separator", "\n");
        val os_ = new StringWriter();
        os = os_;
        out = new Printer(os_);
    }

    abstract def expected():String;

    def status() {
        val got = os.result();
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

    class R {

        def this(o:Printer, test: String, run: ()=>String ): R = {
            var r: String;
            try {
                r = (run)();
            } catch (e: Exception) {
                r = e.getMessage();
            }
            o.println(test + " " + r);
        }
    }
            
    class Grid {
    	private val root = GlobalRef[Grid](this);
        transient var os: Rail[Any] = new Rail[Any](10);

        @Pinned def set(i0: long, vue: double): void = {
            os(i0) = new Box[Double](vue);
        }

        @Pinned def set(i0: long, i1: long, vue: double): void = {
            if (os(i0)==null) os(i0) = new Grid();
            val grid = os(i0) as Grid;
            grid.set(i1, vue);
        }

        @Pinned def set(i0: long, i1: long, i2: long, vue: double): void = {
            if (os(i0)==null) os(i0) = new Grid();
            val grid = os(i0) as Grid;
            grid.set(i1, i2, vue);
        }

        @Pinned def pr(rank: int): void = {
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
                    if (rank==1n)
                        out.print(".");
                    else if (rank==2n) {
                        if (min<=i && i<=max)
                            out.print("    " + i + "\n");
                    }
                } else if (o instanceof Grid) {
                    if (rank==2n)
                        out.print("    " + i + "  ");
                    else if (rank>=3n) {
                        out.print("    ");
                        for (var j: int = 0n; j<rank; j++)
                            out.print("-");
                        out.print(" " + i + "\n");
                    }
                    (o as Grid).pr(rank-1n);
                } else {
                    val d = (o as Box[double]).value;
                    out.print("" + (d as int));
                }

                if (rank==1n)
                    out.print(" ");
            }
            if (rank==1n)
                out.print("\n");
        }
    } // Grid

    @Global def prArray(test: String, r: Region): DistArray[double]{rank==r.rank} = {
        return prArray(test, r, false);
    }

    @Global def prArray(test: String, r: Region, bump: boolean): DistArray[double]{rank==r.rank} = {

        val init1 = (pt: Point) => {
            var v: long = 1;
            for (var i: int = 0n; i<pt.rank; i++)
                v *= pt(i);
            return v%10 as double;
        };

        val init0 = (Point) => 0.0D;

        val a = DistArray.make[double](r->here, bump? init0 : init1);
        prArray(test, a, bump);

        return a as DistArray[double]{rank==r.rank};
    }

    @Global def prDistributed(test:String, a: DistArray[double]): void = {
        for (p in a.dist.places()) {
            val root = this.root;
            finish {
                async at(p) {
                    val ans1 = a | p;
                    at (root) prArray(test + " at " + p + " (by place)", ans1);
                    val r = a.dist.get(p);
                    val ans2 = a | r; 
                    at (root) prArray(test + " at " + p + " (by region)", ans2);
                }
            }
        }
    }


    @Global def prUnbounded(test: String, r: Region): void = {
        try {
            prRegion(test, r);
            val i = r.iterator();
        } catch (e: UnboundedRegionException) {
            pr(e.toString());
        }
    }


    @Global def prRegion(test: String, r: Region): void = {

        pr("--- " + testName + ": " + test);

        new R(out, "rank", () => ("" + r.rank) );
        new R(out, "rect", () => ("" + r.rect) );
        new R(out, "zeroBased", () => ("" + r.zeroBased) );
        new R(out, "rail", () => ("" + r.rail) );

        new R(out, "isConvex()", () => ("" + r.isConvex()) );
        new R(out, "size()", () => ("" + r.size()) );

        pr("region: " + r);

    }

   @Global def prArray(test: String, a: DistArray[double]): void = {
        prArray(test, a, false);
    }

    @Global def prArray(test: String, a: DistArray[double], bump: boolean): void = {

        val r: Region = a.region;

        prRegion(test, r);

        pr("  iterator");
	prArray1(a, bump);
    }


    @Global def prArray1(a: DistArray[double], bump: boolean): void = {
        var grid: Grid = new Grid();
        for (p:Point in a.region) {
            //var v: double = a(p as Point(a.rank));
            if (p.rank==1) {
                val a2 = a as DistArray[double](1);
                if (bump) a2(p(0)) = a2(p(0)) + 1;
                grid.set(p(0), a2(p(0)));
            } else if (p.rank==2) {
                val a2 = a as DistArray[double](2);
                if (bump) a2(p(0), p(1)) = a2(p(0), p(1)) + 1;
                grid.set(p(0), p(1), a2(p(0),p(1)));
            } else if (p.rank==3) {
                val a2 = a as DistArray[double](3);
                if (bump) a2(p(0), p(1), p(2)) = a2(p(0), p(1), p(2)) + 1;
                grid.set(p(0), p(1), p(2), a2(p(0),p(1),p(2)));
            }
        }
        grid.pr(a.rank as int);
    }

    @Global def prPoint(test: String, p: Point): void = {
        var sum: long = 0;
        for (var i: int = 0n; i<p.rank; i++)
            sum += p(i);
        pr(test + " " + p + " sum=" + sum);
    }


    @Global def prDist(test: String, d: Dist): void = {

        pr("--- " + test + ": " + d);

        val init = (Point) => -1.0D;
        val a = DistArray.make[double](d.region->here, init);

        val ps = d.places();
        for (pl in d.places()) {
            val r: Region = d.get(pl);
            for (p:Point(r.rank) in r) {
                val q = p as Point(a.dist.region.rank);
                a(q) = a(q) + pl.id + 1;
            }
        }
        prArray1(a, false);
    }
        

    def pr(s: String): void = {
        out.println(s);
    }

    static def xxx(s: String): void {
        x10.io.Console.OUT.println("xxx " + s);
    }

    // substitute for [a:b,c:d]
    def r(a: long, b: long, c: long, d: long): Region(2) {
        return Region.makeRectangular(a..b, c..d);
    }

}
