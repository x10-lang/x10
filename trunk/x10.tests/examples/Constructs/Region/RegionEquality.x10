/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

public class RegionEquality extends x10Test {

    // needs fix for XTENLANG-130

    public def run(): boolean = {
        val size: int = 10;
        val R: Region(2) = [0..size-1, 0..size-1];
        return R==[0..size-1, 0..size-1];
    }

    public static def main(var args: Rail[String]): void = {
         new RegionEquality().execute();
    }
}
