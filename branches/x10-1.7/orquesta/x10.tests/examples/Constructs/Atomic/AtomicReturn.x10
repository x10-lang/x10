/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Atomic return test
 */
public class AtomicReturn extends x10Test {
	var a: int = 0;
	const N: int = 100;

	def update1(): int = {
		atomic {
			a++;
			return a;
		}
	}

	def update3(): int = {
		atomic {
			return a++;
		}
	}

	public def run(): boolean = {
		update1();
		update3();
		System.out.println(a);
		return a == 2;
	}

	public static def main(var args: Rail[String]): void = {
		new AtomicReturn().execute();
	}
}
