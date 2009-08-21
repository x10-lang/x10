/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test the shorthand syntax for object array initializer.
 *
 * @author igor, 12/2005
 */

public class ObjectArrayInitializerShorthand extends x10Test {

    public def run(): boolean = {
        val d  = Dist.makeConstant([1..10, 1..10], here);
        val ia = Array.make[Dist](d, ((i,j): Point) => d);
        for (val (i,j): Point(2) in ia.region) chk(ia(i, j) == d);
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ObjectArrayInitializerShorthand().execute();
    }
}
