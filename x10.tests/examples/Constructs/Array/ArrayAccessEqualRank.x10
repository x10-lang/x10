/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test that a can be accessed through point p if p ranges over b.dist
 and a.rank has been declared to be b.rank.

 * @author vj 03/17/09 -- fails compilation.

 */

public class ArrayAccessEqualRank extends x10Test {

    public def arrayEqual(A: Array[int], B: Array[int](A.rank)) {
        finish
            ateach (p in A.dist) {
            val v = at (B.dist(p)) B(p);
            chk(A(p) == v);
	}
    }

    public def run(): boolean = {

	val D = Dist.make(0..9 as Region);
        val a = Array.make[Int](D, (Point)=>0), 
	b = Array.make[Int](D,(Point)=>0);
	arrayEqual(a,b);
        return true;
    }

    public static def main(Rail[String]) = {
        new ArrayAccessEqualRank().execute();
    }
}
