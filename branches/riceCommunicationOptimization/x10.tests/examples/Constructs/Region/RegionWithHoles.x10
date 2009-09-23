/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

public class RegionWithHoles extends x10Test {

    def test1D(): boolean = {

        // all of those are contiguous
        var r: Region{rank==1} = [0..10];
        var r1: Region{rank==1} = [1..2];
        var r2: Region{rank==1} = [5..6];

        // create holes in r
        r = r - r1;
        r = r - r2;

        val a = Array.make[short](r);
        chk(!r.isConvex());
        // check if r is convex - it should not!
        x10.io.Console.OUT.println("convex: " + r.isConvex() + " (should be false)");

        x10.io.Console.OUT.print("indexes: ");
        for (val (i): Point in r) {
            x10.io.Console.OUT.print(i + " ");
        }

        try {
            for (val (i): Point in r) {
                if (a(i) != 0)
                    x10.io.Console.OUT.println("val[" + i + "] = " + a(i));
            }
        }
        catch (var t: Throwable) {
            x10.io.Console.OUT.println(t);
            return false;
        }
        return true;
    }

    def test2D(): boolean = {

        x10.io.Console.OUT.println("testing 2d");
        // all of those are contiguous
        var r: Region{rank==2} = [0..10, 0..3];
        var r1: Region{rank==2} = [1..2, 0..3];
        var r2: Region{rank==2} = [5..6, 0..3];

        // create holes in r
        r = r - r1;
        r = r - r2;

        val a = Array.make[short](r);

        // check if r is convex - it should not!
        x10.io.Console.OUT.println("convex: " + r.isConvex() + " (should be false)");
        chk(!r.isConvex());

        try {
            for (val (i,j): Point in r) {
                if (a(i, j) != 0)
                    x10.io.Console.OUT.println("val[" + i + "] = " + a(i, j));
            }
        }
        catch (var t: Throwable) {
            x10.io.Console.OUT.println(t);
            return false;
        }
        return true;
    }

    def test3D(): boolean = {
        // all of those are contiguous
        var r: Region{rank==3} = [0..10, 0..3, 0..0];
        var r1: Region{rank==3} = [1..2, 0..3, 0..0];
        var r2: Region{rank==3} = [5..6, 0..3, 0..0];

        // create holes in r
        r = r - r1;
        r = r - r2;

        val a = Array.make[short](r);
        chk(!r.isConvex());
        // check if r is convex - it should not!
        x10.io.Console.OUT.println("convex: " + r.isConvex() + " (should be false)");

        try {
            for ((i,j,k): Point in r) {
                if (a(i, j, k) != 0)
                    x10.io.Console.OUT.println("val[" + i + "] = " + a(i, j, k));
            }
        }
        catch (var t: Throwable) {
            x10.io.Console.OUT.println(t);
            return false;
        }
        return true;
    }

    def test4D(): boolean = {
        // all of those are contiguous
        var r: Region{rank==4} = [0..0, 0..10, 0..3, 0..0];
        var r1: Region{rank==4} = [0..0, 1..2, 0..3, 0..0];
        var r2: Region{rank==4} = [0..0, 5..6, 0..3, 0..0];

        // create holes in r
        r = r - r1;
        r = r - r2;

        val a = Array.make[short](r);
        chk(!r.isConvex());
        // check if r is convex - it should not!
        x10.io.Console.OUT.println("4d:convex: " + r.isConvex() + " (should be false)");

        if (false) {
            x10.io.Console.OUT.print("indexes: ");
            for ((i): Point in r) {
                x10.io.Console.OUT.print(i + " ");
            }
        }

        try {
            for ((i,j,k,l): Point in r) {
                if (a(i, j, k, l) != 0)
                    x10.io.Console.OUT.println("val[" + i + "] = " + a(i, j, k, l));
            }
        }
        catch (var e: x10.lang.Exception) {
            x10.io.Console.OUT.println(e);
            return false;
        }
        return true;
    }

    def testPoint(): boolean = {

        x10.io.Console.OUT.println("testing point");
        // all of those are contiguous
        var r: Region{rank==1} = [0..10];
        var r1: Region{rank==1} = [1..2];
        var r2: Region{rank==1} = [5..6];

        // create holes in r
        r = r - r1;
        r = r - r2;

        val a = Array.make[short](r);
        chk(!r.isConvex());
        // check if r is convex - it should not!
        x10.io.Console.OUT.println("convex: " + r.isConvex() + " (should be false)");

        try {
            for (val p: Point in r) {
                if (a(p as Point{rank==1}) != 0) // XTENLANG-128
                    x10.io.Console.OUT.println("val[" + p + "] = " + a(p as Point{rank==1}));
            }
        }
        catch (var t: Throwable) {
            x10.io.Console.OUT.println(t);
            return false;
        }
        return true;
    }

    public def run(): boolean = {
        return test1D() && test2D() && test3D() && test4D() && testPoint();
    }

    public static def main(var args: Rail[String]): void = {
        new RegionWithHoles().execute();
    }
}
