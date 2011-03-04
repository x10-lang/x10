/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing returns in an async body.
 *
 * @author vj
 */
public class AsyncReturn extends x10Test {

	public def run(): boolean = {
		class T {
			var t: int;
		}
		val f: T = new T();
		f.t = 1;
		val v: int = f.t;
		val body = ()=> {
			if (v == 1)
			return;
		    async (f.location) {
			   atomic {
				  f.t = 2;
			   }
		     }
		};
		finish async body();
		return (f.t == 1);
	}

	public static def main(var args: Rail[String]): void = {
		new AsyncReturn().execute();
	}
}
