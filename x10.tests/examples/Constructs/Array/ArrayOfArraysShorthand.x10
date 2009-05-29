/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test the syntax for creating an array of arrays.
 *
 * @author igor, 12/2005
 */

public class ArrayOfArraysShorthand extends x10Test {

    public def run(): boolean = {

        val r1: Region(1) = 0..7;
        val r2: Region(1) = 0..9;
        val r: Region(2) = Region.make([r1, r2]);
        val ia: Array[Array[Int]{rank==1}]{rank==1} = Array.make[Array[Int]{rank==1}](r1->here, (Point)=> Array.make[Int](r2->here, (Point)=>0));

        for (val (i,j): Point in r) chk(ia(i)(j) == 0);

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayOfArraysShorthand().execute();
    }
}
