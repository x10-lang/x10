/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Only final free variables can be passed to async body.
 *
 * @author kemal 4/2005
 */
public class AsyncTest4_MustFailCompile extends x10Test {

	public const N: int = 20;

	public def run(): boolean = {
		var s: int = 0;
		for (var i: int = 0; i < N; i++) {
			//==> compiler error expected here
			finish async(here) x10.io.Console.OUT.println("s="+s+" i="+i);
			s += i;
		}
		// no compiler error here
		s = 0;
		for (var i: int = 0; i < N; i++) {
			{
				val i1: int = i;
				val s1: int = s;
				finish async(here) x10.io.Console.OUT.println("s1="+s1+" i1="+i1);
			}
			s += i;
		}
		val y: int;
		//==> Compiler error expected here
		finish async(here) { async(here) y = 3; }
		x10.io.Console.OUT.println("y="+y);
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new AsyncTest4_MustFailCompile().execute();
	}
}
