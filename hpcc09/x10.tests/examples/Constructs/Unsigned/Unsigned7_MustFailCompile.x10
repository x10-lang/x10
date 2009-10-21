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
public class Unsigned7_MustFailCompile extends x10Test {

    public def run(): boolean = {
        val a: ubyte = 0;
        val b: ubyte = 1;
        val c: ubyte = -1; // error
        val d: ubyte = 127;
        val e: ubyte = 128;
        val f: ubyte = 255;
        val g: ubyte = 256; // error
        return true;
    }

    public static def main(Rail[String]) = {
        new Unsigned7_MustFailCompile().execute();
    }
}
