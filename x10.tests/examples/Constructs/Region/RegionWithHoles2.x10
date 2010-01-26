/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

public class RegionWithHoles2 extends x10Test {

    public def run(): boolean = {

        // all of those are contiguous
        var r: Region{rank==1} = [0..10];
        var r1: Region{rank==1} = [1..2];
        var r2: Region{rank==1} = [5..6];

        // create holes in r
        r = r - r1;
        r = r - r2;

        x10.io.Console.OUT.println("r " + r);
        x10.io.Console.OUT.println("r.boundingBox() " + r.boundingBox());

        val a = Array.make[short](r);

        // check if r is convex - it should not!
        var cv: boolean = r.isConvex();
        x10.io.Console.OUT.println("convex: " + cv + " (should be false)");
        chk(!cv);

        x10.io.Console.OUT.print("indexes: ");
        for (val (i): Point in r) {
            x10.io.Console.OUT.print(i + " ");
        }

        x10.io.Console.OUT.println();
        for (val (i): Point in r) {
            x10.io.Console.OUT.println("val[" + i + "] = " + a(i));
        }

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new RegionWithHoles2().execute();
    }
}
