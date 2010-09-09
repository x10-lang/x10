/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Test to check that unsafe is being parsed correctly.
 */
public class Unsafe extends x10Test {

	public def run(): boolean = {
		var e: Region = [1..10];
		var r: Region = [e,e,e,e];
		var d: Dist = r->here;

		var x: Array[int] = new Array[int](d); // ok
		var y: Array[int] = new Array[int](d); //ok
		var y1: Array[int] = new Array[int](d); // ok
		var zz: Array[int] = new Array[int](d, (p: point): int => 41); // bad
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Unsafe().execute();
	}
}
