/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;


/**
 * Test for array reference flattening. 
 */

public class Force extends x10Test {
   
    static def rd(val e: Future[Int], val i: int, val j: int): int = {
        val x: int = e();
        return (future { x }).force();
    }
   
    public def run(): boolean = {
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new Force().execute();
    }
}
