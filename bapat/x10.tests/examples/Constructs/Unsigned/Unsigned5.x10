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
public class Unsigned5 extends x10Test {

    public def run(): boolean = {
        val b = 0xffffffffu < 0u;
        return ! b;
    }

    public static def main(Rail[String]) = {
        new Unsigned5().execute();
    }
}
