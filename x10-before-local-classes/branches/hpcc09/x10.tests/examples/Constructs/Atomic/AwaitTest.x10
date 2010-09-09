/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Minimal test for await.
 */
public class AwaitTest extends x10Test {

	var val: int = 0;

	public def run(): boolean = {
		val c: Clock = Clock.make();
		async(this.location) clocked(c) {
			await (val > 43);
			atomic val = 42;
			await (val == 0);
			atomic val = 42;
		}
		atomic val = 44;
		await (val == 42);
		var temp: int;
		atomic temp = val;
		x10.io.Console.OUT.println("temp = " + temp);
		if (temp != 42)
			return false;
		atomic val = 0;
		await (val == 42);
		next;
		var temp2: int;
		atomic temp2 = val;
		x10.io.Console.OUT.println("val = " + temp2);
		return temp2 == 42;
	}

	public static def main(var args: Rail[String]): void = {
		new AwaitTest().executeAsync();
	}
}
