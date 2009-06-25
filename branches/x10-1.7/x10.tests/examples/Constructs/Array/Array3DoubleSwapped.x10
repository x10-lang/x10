/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Ensures double arrays are implemented. Tests literal occurring in RHS of an ==, with array
 access in LHS.
 */
public class Array3DoubleSwapped extends x10Test {

    public def run(): boolean = {
        val r  = [1..10, 1..10] as Region;
        val ia = Array.make[Double](r, (x:Point)=>0.0D);
        ia(1, 1) = 42.0D;
        x10.io.Console.OUT.println("ia(1,1)=" + ia(1,1));
        return ia(1,1) == 42.0D;
    }

    public static def main(Rail[String]) = {
        new Array3DoubleSwapped().execute();
    }
}
