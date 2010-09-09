/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
// import harness.x10Test;;

/**
 * Testing factorial
 */
public class FactTest {

	public def run(): boolean = {
		return !(fact(5) == 120);
	}
	public def fact(var v: int): int =  {
		if (v <= 1) return 1;
		return v*fact(v-1);
	}

	public static def main(var args: Rail[String]): void = {
		new FactTest().run();
	}
}
