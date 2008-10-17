/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test the shorthand syntax for an array initializer.
 */

public class IntArrayInitializerShorthand extends x10Test {

    public def run(): boolean = {

        var d: Dist = Dist.makeConstant([1..10, 1..10], here);
        var ia: Array[int] = new Array[int](d, (var Point [i,j]: Point): int => { return i+j; });

        for (val p(i,j): Point(2) in ([1..10, 1..10] to Region)) chk(ia(p) == i+j);

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new IntArrayInitializerShorthand().execute();
    }
}
