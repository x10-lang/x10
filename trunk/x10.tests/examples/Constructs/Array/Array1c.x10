/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Simple array test.
 *
 * Same as Array1 but shorthand forms (such as ia[p]) are used.
 *
 * This only has the difference of a distribution declaration
 * from Array1b, yet it caused a class cast exception at
 * run time (5/2005)
 */
public class Array1c extends x10Test {

	public def run(): boolean = {
		val e: Region(1) = 1..10;
		val r: Region = [e, e];
		chk(r.equals([1..10, 1..10]));
		val d: dist = Dist.makeConstant(r, here);
		//final dist d = [1:10,1:10]->here;
		chk(d.equals(Dist.makeConstant([1..10, 1..10], here)));
		chk(d.equals(Dist.makeConstant([e, e], here)));
		chk(d.equals(Dist.makeConstant(r, here)));
		val ia: Array[int] = Array.make[int](d, (point)=>0);

		for (val p in e) for (val q: point in e) {
				val i= p(0);
				val j = q(0);
				chk(ia(i, j) == 0);
				ia(i, j) = i+j;
			}
/*
		for (val p in ia) {
			val i = p(0);
			val j = p(1);
			var q1: point(2) = [i, j];
			chk(i == q1(0));
			chk(j == q1(1));
			chk(ia(i, j) == i+j);
			//Class cast exc. occurred at next line:
			chk(ia(i, j) == ia(p));
			chk(ia(q1) == ia(p));
			ia(p) = ia(p) - 1;
			chk(ia(p) == i + j - 1);
			chk(ia(q1) == ia(p));
		}
*/
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Array1c().execute();
	}
}
