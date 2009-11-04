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
 * What is highly annoying is that ateach (i:Point(D.rank) in D) b(i)=i
 * succeeds. The compiler cant figure out that when iterating over D
 * the points have rank D.rank??

 */

public class ArrayAccessEqualRank5 extends x10Test {

    public def run(): boolean = {
	val b:Array[Int](1) = Array.make[Int](0..9->here,(Point)=>0);
	finish ateach (x(i):Point(1) in b.dist) 
	   b(x)=i;
        return true;
    }

    public static def main(Rail[String]) = {
        new ArrayAccessEqualRank5().execute();
    }
}
