/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Checks if finish also consideres grand-children.
 * @author Christoph von Praun
 */
public class FinishTest2 extends x10Test {

	var flag: boolean;
	var foo: int;

	public def run(): boolean = {
		atomic flag = false;
		finish {
			async (here) {
				atomic foo = 123;
				async (here) {
					atomic foo = 42;
					System.out.print("waiting ...");
					x10.lang.Runtime.sleep(2000);
					System.out.println("done.");
					atomic flag = true;
				}
			}
		}
		var b: boolean;
		atomic b = flag;
		System.out.println("The flag is b = " + b + " (should be true).");
		return (b == true);
	}

	public static def main(var args: Rail[String]): void = {
		new FinishTest2().execute();
	}
}
