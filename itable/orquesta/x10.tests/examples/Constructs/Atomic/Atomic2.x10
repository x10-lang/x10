/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * atomic enclosing a void function call
 * that throws an exception.
 *
 * @author kemal 4/2005
 */
public class Atomic2 extends x10Test {

	var x: int = 0;


	public def run(): boolean = {
		finish async(this) atomic x++;
		atomic chk(x == 1);

		var gotException: boolean = false;
		try {
			atomic chk(x == 0);
		} catch (var e: Throwable) {
			gotException = true;
		}
		chk(gotException);
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Atomic2().execute();
	}
}
