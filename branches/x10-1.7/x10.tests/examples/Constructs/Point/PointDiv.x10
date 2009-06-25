/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing point div..
 *
 * @author vj 08/29/08
 */

public class PointDiv extends x10Test {

    public def run(): boolean = {

        val p  = [2, 2, 2, 2, 2] as Point;
        val q = [1, 1, 1, 1, 1] as Point;
    
        val a = p / q;
        return p==a*q;
    }

    public static def main(var args: Rail[String]): void = {
        new PointDiv().execute();
    }
}
