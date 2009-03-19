/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing point subtraction..
 *
 * @author vj 08/29/08
 */

public class PointSub extends x10Test {

    public def run(): boolean = {

        val p  = [2, 2, 2, 2, 2] as Point;
        val q = [1, 1, 1, 1, 1] as Point;
    
        val a = p - q;
        return a+q==p;
    }

    public static def main(var args: Rail[String]): void = {
        new PointSub().execute();
    }
}
