/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Simple unsigned test.
 */
public class Unsigned4_MustFailCompile extends x10Test {

    public def run(): boolean = {
        var a: int = 0;
        var b: uint = 1u;
        val c = a < b;
        return c;
    }

    public static def main(Rail[String]) = {
        new Unsigned4_MustFailCompile().execute();
    }
}
