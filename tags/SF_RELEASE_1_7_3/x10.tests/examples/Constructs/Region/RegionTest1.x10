/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Minimal test for regions.
 */

public class RegionTest1 extends x10Test {

    public def run(): boolean = {

        var r: Region = 0..100;
        var reg: Region = [r,r];

        var sum: int = 0;
        for (p(i,j) in reg) sum += i-j;

        return sum == 0;
    }

    public static def main(var args: Rail[String]): void = {
        new RegionTest1().execute();
    }
}
