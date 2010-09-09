/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing the maxAbs function on arrays.
 */

public class IntArrayMaxAbs extends x10Test {

    public def run(): boolean = {

        val D  = Dist.makeConstant([1..10, 1..10] to Region, here);
        val ia  = Array.make[int](D, (Point)=>0);

        finish ateach (val p(i,j): Point(2) in D) { ia(p) = -i; }

        return ia.reduce((a:Int, b:Int):Int => {
            val ma = Math.abs(a), mb =Math.abs(b);
            ma <= mb? mb : ma
        }) == 10;
    }

    public static def main(var args: Rail[String]): void = {
        new IntArrayMaxAbs().execute();
    }
}
