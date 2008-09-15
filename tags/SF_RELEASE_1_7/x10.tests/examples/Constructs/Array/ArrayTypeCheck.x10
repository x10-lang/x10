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

 * @author kemal 4/2005
 */
public class ArrayTypeCheck extends x10Test {

	public def run(): boolean = {
		var a1: Array[int] = Array.make[int](Dist.makeConstant([0..2, 0..3], here),
		  (var p(i): Point): int => { return i; });
		System.out.println("1");
		val E: dist = Dist.makeConstant(-1..-2, here);
		try {
		var a2: Array[int] = a1 to Array[int](E);
		return false;
		} catch (var z: ClassCastException) {
		System.out.println("2");
		}
		try {
		  val D: dist = dist.makeUnique();
		  var a3: Array[int] = a1 to Array[int](D);
		  return false;
		} catch (var z: ClassCastException) {
			System.out.println("3");
		}
		
		var i: int = 1;
		var j: int = 2;
		var k: int = 0;
		val p = [i, j, k] to Point;
		val q = [i, j] to Point;
		val r = [i] to Point;
		if (p == q) return false;
		
/*
		var gotException: boolean;
		System.out.println("5");
		try {
			return a1(i) == a1(i, j, k);
		} catch (var e: RankMismatchException) {
			gotException = true;
			System.out.println("Caught "+e);
		}
		if (!gotException) return false;
*/
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ArrayTypeCheck().execute();
	}
}
