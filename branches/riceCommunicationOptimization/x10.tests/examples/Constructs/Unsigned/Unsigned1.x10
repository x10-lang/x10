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
public class Unsigned1 extends x10Test {

    public def run(): boolean = {
        val x: uint = 1 as uint;
        x10.io.Console.OUT.println(x.toString());
        x10.io.Console.OUT.println(x as int);
        val y: uint = (Int.MAX_VALUE as uint) + (1 as uint);
        x10.io.Console.OUT.println(y.toString());
        x10.io.Console.OUT.println(y as int);
        return (x as int) == 1 && (y as int) == Int.MIN_VALUE;
    }

    public static def main(Rail[String]) = {
        new Unsigned1().execute();
    }
}
