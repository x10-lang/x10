/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Minimal test for dists.
 */

public class DistributionTest1 extends x10Test {

    public def run(): boolean = {
        val r= 0..100;
        val R = [r,r] as Region;
        val D  = R->here;
        return ((D(0, 0) == here) &&
            (D.rank == 2) &&
            (R.rank == 2) &&
            (R.max(1) - R.min(1) + 1 == 101));
    }

    public static def main(var args: Rail[String]): void = {
        new DistributionTest1().execute();
    }
}
