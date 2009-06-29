/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * This must compile and run fine. Checks that the initializer may not specify
 * the arity of the region.

 * @author vj 12 2006
 */

public class DimCheck extends x10Test {

    public def run(): boolean = {
        var a1: Array[int] = Array.make[int](Dist.makeConstant([0..2, 0..3] as Region, here), (p: Point) => p(0));
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new DimCheck().execute();
    }
}
