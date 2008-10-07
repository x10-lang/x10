/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * A rank 2 array can be initialized with a point of rank 1. The
 * pattern (i) merely checks that the point has rank >= 1.
 *
 * @author vj 12 2006
 */

public class DimCheckN extends x10Test {

    public def run(): boolean = {
        val d  = Dist.makeConstant([0..2, 0..3], here);
        val a1  = Array.make[int](d, ((i): Point) => i);
        return true;
    }
    
    public static def main(var args: Rail[String]): void = {
        new DimCheckN().execute();
    }
}
