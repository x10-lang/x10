/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Simple array test.
 *
 * Only uses the longhand forms such as ia.get(p) for ia[p].
 * Note: this a test only.  It is not the recommended way to
 * write x10 code.
 */
public class ArrayIndexWithPoint extends x10Test {

	public def run(): boolean = {
	    val e:Region(1) = 1..10;
	    val r:Region(2) = [e,e];
	    val d:Dist = r->here;
	    val ia: Array[int](d) = Array.make[int](d, (point)=>0);
	   
		for (val p(i,j) in d) {
		    chk(ia(i, j) == ia(p));
		    ia(p) = ia(p)-1;
		    chk(ia(p) == i+j-1);
		}
		return true;
	}

	public static def main(Rail[String]) = {
		new ArrayIndexWithPoint().execute();
	}
}
