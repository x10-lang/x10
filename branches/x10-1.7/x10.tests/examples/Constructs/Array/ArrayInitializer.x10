/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Array Initializer test.
 */

public class ArrayInitializer extends x10Test {

    public def run(): boolean = {

        val e: Region(1) = 0..9;
        val r= [e, e, e];

        //TODO: next line causes runtime error
        //dist d=r->here;
        val d  = Dist.makeConstant([0..9, 0..9, 0..9], here);

        val ia = Array.make[Int](d, (val (i,j,k): Point)=> i);
        for (val (i,j,k): Point in ia.region) chk(ia(i, j, k) == i);

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayInitializer().execute();
    }
}
