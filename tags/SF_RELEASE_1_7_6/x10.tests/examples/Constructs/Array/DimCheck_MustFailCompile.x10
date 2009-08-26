/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This check is not being done by the compiler currently.
import harness.x10Test;

/**
 * Dimensionality of array initializer must be checked.
 *
 * @author vj 12 2006
 */

public class DimCheck_MustFailCompile extends x10Test {

    public def run(): boolean = {
        // The compiler should check the type of the distribution in the array constructor.
        // If  the type does not specify a constraint on the arity of the underlying region
        // then the initializer cannot specify the arity of the point. Otherwise the arity of the
        // point must be the same as the arity of the distribution.
        var a1: Array[int] = new Array[int](Dist.makeConstant([0..2, 0..3], here), (var p: Point[i]): int => { return i; });
        x10.io.Console.OUT.println(a1);
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new DimCheck_MustFailCompile().execute();
    }
}
