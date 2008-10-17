/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test for X10 arrays -- tests arrays passed as parameters and stored in fields.
 */
public class Array4 extends x10Test {

    var ia: Array[int](2);

    public def this(): Array4 = {}

    public def this(var ia: Array[int](2)): Array4 = {
        this.ia = ia;
    }

    private def runtest(): boolean = {
        ia(1, 1) = 42;
        return 42 == ia(1, 1);
    }

    /**
     * Run method for the array. Returns true iff the test succeeds.
     */
    public def run(): boolean = {
        return (new Array4(Array.make[int](Dist.makeConstant([1..10, 1..10], here), (Point)=>0))).runtest();
    }

    public static def main(var args: Rail[String]): void = {
        new Array4().execute();
    }
}
