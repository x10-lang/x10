/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing arrays of future<T>.
 *
 * @author kemal, 5/2005
 */

public class ArrayFutureFlatten extends x10Test {

    public def run(): boolean = { 
        val A = Array.make[int]([1..10, 1..10]->here, (Point)=>0);
        val B = Array.make[int]([1..10, 1..10]->here, (Point)=>0);
        val b = (future 3) .force();
        chk(0 == (future B(1,1)).force());
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayFutureFlatten().execute();
    }
}
