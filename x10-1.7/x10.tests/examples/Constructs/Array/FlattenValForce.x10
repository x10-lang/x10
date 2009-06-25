/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;


/**
 * Test for array reference flattening. Check that flattening works inside a Future.
 */

public class FlattenValForce extends x10Test {
   
    static def rd(val e: Array[Future[Int]](1), val i: int)  = {
        val fd = future { 3.0 };
        val x  = fd();
        return future { e(i).force() };
    }
   
    public def run(): boolean = {
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new FlattenValForce().execute();
    }
}
