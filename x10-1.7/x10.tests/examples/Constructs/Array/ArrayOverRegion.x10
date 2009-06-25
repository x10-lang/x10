/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Region r occuring in a distribution context is converted to r->here
 */

public class ArrayOverRegion extends x10Test {

    public def run(): boolean = {

        val r = [1..10, 1..10] as Region;
        val ia = Array.make[double](r->here, (var (i,j): Point)=> i+j as Double);

        chk(ia.dist.equals(Dist.makeConstant(r, here)));

        for (val p(i,j): Point(2) in r) chk(ia(p) == i+j);

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayOverRegion().execute();
    }
}
