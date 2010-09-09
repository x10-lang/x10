/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 *
 * Submitted by Doug Lovell.
 * @author igor 1/2006
 * @author vj 09/2008 -- Apparently the compiler cant deal with a(i)(j) = e.
 */

public class MultiDimensionalJavaArray extends x10Test {

    const MIN = 0..99;
    const MAJ = 0..9;
    const PI = Math.PI;

    public def run(): boolean = {

        val a = Array.make[Array[Double](1)](MIN, (Point) => Array.make[Double](MAJ));

        for (val (i,j): Point(2) in ([MIN, MAJ] as Region))
            a(i)(j) = (i * j / PI);

        val d = a(MIN.max(0)/2);
        for (val (j): Point in MAJ) 
            chk(d(j) == (MIN.max(0)/2 * j / PI));

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new MultiDimensionalJavaArray().execute();
    }
}
