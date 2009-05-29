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
 *
 *
 * @author kemal 4/2005
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
// In trying to determine if two final variables of type dist are equa, 
// the compiler does not currently try to check statically whether there values are compile-time constants
// and are equal.

public class ArrayTypeCheck2_MustFailCompile extends x10Test {

	public def run(): boolean = {
		final val O: dist = Dist.makeConstant([0..2, 0..3], here);
		var a1: Array[int]{distribution==O} = new Array[int](O, (var p: point[i]): int => { return i; });
		System.out.println("1");
		final val E: dist = Dist.makeConstant([-1..-2], here);
		var a2: Array[int]{distribution==E} = (Array[int]{distribution==E})a1;
		System.out.println("2");
		final val D: dist = distmakeUnique();
		var a3: Array[int] = (Array[int]{distribution == D})a2;
		System.out.println("3");
		var i: int = 1;
		var j: int = 2;
		var k: int = 0;
		var p: point = [i, j, k];
		var q: point = [i, j];
		var r: point = [i];
		if (p == q) return false;
		System.out.println("4");
		if (a1(q) + a3(q) != 2) return false;

		var gotException: boolean;
		System.out.println("5");
		try {
			return a1(i) == a1(i, j, k);
		} catch (var e: RankMismatchException) {
			gotException = true;
			System.out.println("Caught "+e);
		}
		if (!gotException) return false;

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ArrayTypeCheck2_MustFailCompile().execute();
	}
}
