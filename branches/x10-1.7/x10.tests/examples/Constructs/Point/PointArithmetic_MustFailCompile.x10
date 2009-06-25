/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing point arithmetic operations.
 *
 * @author igor, 2/2006
 */

public class PointArithmetic_MustFailCompile extends x10Test {

    public const DIM: int = 5;

    public def run(): boolean = {

        var sum: int = 0;
        val p = [2, 2, 2, 2, 2] as Point(DIM);
        val q = [1, 1, 1, 1, 1] as Point(DIM);
        var c: int = 2;

        // Now test that the dimensionality is properly checked

        var gotException: boolean;
        var r: Point = [1, 2, 3, 4] as Point;

        var a: Point;
        var s: Point;
        var m: Point;
        var d: Point;
        
        a = p + r;
        s = p - r;
        m = p * r;
        d = p / r;
        a = r + p;
        s = r - p;
        m = r * p;
        d = r / p;
        
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new PointArithmetic_MustFailCompile().execute();
    }
}
