/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
 
import harness.x10Test;;

/**
 * Array operations and points must be type checked.
 *
 * The expected result is that compilation must fail.
 * A val of type Point(3) should be statically known to not == a val
 * of type Point(2).
 *
 * @author kemal 4/2005
 * @author vj 9/2008
 */

public class ArrayTypeCheck2_MustFailCompile extends x10Test {

	public def run(): boolean = {
		val O = Dist.makeConstant([0..2, 0..3], here);
		val a1 = Array.make[int](O, (var p(i): Point): int => { return i; });
		System.out.println("1");
		val E = Dist.makeConstant(-1..-2, here);
		val a2  = a1 to Array[int](E);
		System.out.println("2");
		val D = Dist.makeUnique();
		val a3  = a2 to Array[int](D);
		System.out.println("3");
		var i: int = 1;
		var j: int = 2;
		var k: int = 0;
		val p = [i, j, k] to Point;
		val q = [i, j] to Point;
		val r = [i] to Point;
		if (p == q)  
		// should be a compile time error. a Point(3) can never equal a Point(2)
		  return false;

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ArrayTypeCheck2_MustFailCompile().execute();
	}
}
