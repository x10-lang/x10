/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Ensures boolean arrays are implemented.
 */
public class Array3Boolean extends x10Test {

    public def run(): boolean = {
        val r= [1..10, 1..10] as Region;
        val ia = Array.make[Boolean](r, (x:Point)=>false);
        ia(1, 1) = true;
        return ia(1, 1);
    }

    public static def main(var args: Rail[String]): void = {
        new Array3Boolean().execute();
    }
}
