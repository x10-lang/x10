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

public class ArrayFuture extends x10Test {

    public def run(): boolean = {
        val d = Dist.makeConstant([1..10, 1..10], here);
        val ia  = Array.make[Future[Int]](d, ((i,j): Point) => future(here){i+j});
        for ((i,j): Point in ia.region) chk(ia(i, j)() == i+j);
        return true;
    }

    public static def main(Rail[String]): Void = {
        new ArrayFuture().execute();
    }
}
