/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Ensures char arrays are implemented.
 */
public class Array3Char extends x10Test {

    public def run(): boolean = {
        val r  = [1..10, 1..10] as Region;
        val ia  = Array.make[Char](r, (x:Point)=>'_');
        ia(1, 1) = 'a';
        return ('a' == ia(1, 1));
    }

    public static def main(var args: Rail[String]): void = {
        new Array3Char().execute();
    }
}
