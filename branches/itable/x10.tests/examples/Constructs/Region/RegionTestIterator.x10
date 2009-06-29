/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */

import harness.x10Test;

/**
 * Testing the standard region iterator.
 */

public class RegionTestIterator extends x10Test {

    public def run(): boolean = {

        val r = Region.makeRectangular(0, 100); // (low, high)
        val r2 = [r, r];
        val reg = Region.make(r2);

        var sum:int = 0;
        for ((i,j):Point in reg)
            sum += i - j;

        return sum == 0;
    }

    public static def main(var args: Rail[String]): void = {
        new RegionTestIterator().execute();
    }
}
