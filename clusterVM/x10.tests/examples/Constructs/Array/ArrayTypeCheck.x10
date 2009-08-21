/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Array operations and points must be type checked.
 *
 * @author kemal 4/2005
 */

public class ArrayTypeCheck extends x10Test {

    public def run(): boolean = {

        var a1: Array[int] = Array.make[int](Dist.makeConstant([0..2, 0..3], here), (var p(i): Point): int => { return i; });

        x10.io.Console.OUT.println("1");

        val E: Dist = Dist.makeConstant(-1..-2, here);

        try {
            x10.io.Console.OUT.println("a1.dist " + a1.dist);
            x10.io.Console.OUT.println("E " + E);
            x10.io.Console.OUT.println("== " + (a1.dist==E));
            // x10.io.Console.OUT.println(".equals " + a1.dist.equals(E)); XXXX
            var a2: Array[int] = a1 as Array[int](E);
            x10.io.Console.OUT.println("did not get exception");
            return false;
        } catch (var z: ClassCastException) {
            x10.io.Console.OUT.println("2");
        }

        try {
            val D: Dist = Dist.makeUnique();
            var a3: Array[int] = a1 as Array[int](D);
            return false;
        } catch (var z: ClassCastException) {
            x10.io.Console.OUT.println("3");
        }
        
        var i: int = 1;
        var j: int = 2;
        var k: int = 0;
        val p = [i, j, k] as Point;
        val q = [i, j] as Point;
        val r = [i] as Point;

        if (p == q) return false;
        
/*
        var gotException: boolean;
        x10.io.Console.OUT.println("5");
        try {
            return a1(i) == a1(i, j, k);
        } catch (var e: RankMismatchException) {
            gotException = true;
            x10.io.Console.OUT.println("Caught "+e);
        }
        if (!gotException) return false;
*/
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayTypeCheck().execute();
    }
}
