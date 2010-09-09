/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Minimal test for distribution restriction.
 */

public class Restrict extends x10Test {

    public def run(): boolean = {
        val r = 0..100;
        val R = [r,r] to Region;
        val d = R->here;
        val R2  = (d | here).region;
        System.out.println("R " + R);
        System.out.println("R2 " + R2);
        System.out.println("R.size() " + R.size());
        System.out.println("R2.size() " + R2.size());
        System.out.println("d " + d);
        System.out.println("(d|here) " + (d|here));
        return (R.size() == R2.size());
    }

    public static def main(var args: Rail[String]): void = {
        new Restrict().execute();
    }
}
