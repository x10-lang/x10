/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing int[] method parameters and fields.
 */
public class Array5 extends x10Test {

    var ia: Array[int](1);

    public def this(): Array5 = {}

    public def this(var ia: Array[int](1)): Array5 = {
        this.ia = ia;
    }

    private def runtest(): boolean = {
        ia(0) = 42;
        return 42 == ia(0);
    }

    public def run(): boolean = {
        val temp = Array.make[int](1, (Point)=>0);
        temp(0) = 43;
        return (new Array5(temp)).runtest();
    }

    public static def main(var args: Rail[String]): void = {
        new Array5().execute();
    }
}
