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

public class PointAddC extends x10Test {

    public def run(): boolean = {

        val p:Point = [2, 2, 2, 2, 2] as Point;
        var c:int = 2;
        val a = p + c;

        var sum: int= 0;
        for (var i: int = 0; i < p.rank(); i++)
            sum += a(i);

        if (sum != 20) return false;
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new PointAddC().execute();
    }
}
