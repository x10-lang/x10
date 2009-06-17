/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test the shorthand syntax for an array initializer.
 */

public class ArrayInitializerShorthand extends x10Test {

    public def run(): boolean = {

        val r:Region = [1..10,1..10];

        val d  = Dist.makeConstant(r, here);
        val ia = Array.make[double](d, (val (i,j): Point)=> i+j as Double);

        for (val p(i,j): Point{rank==2} in r) chk(ia(p) == i+j);

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayInitializerShorthand().execute();
    }
}
