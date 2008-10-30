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

	int val = 0;

	public boolean run() {
		final clock c = clock.factory.clock();
		async(this) clocked(c) {
			await (val > 43);
			atomic val = 42;
			await (val == 0);
			atomic val = 42;
		}
		atomic val = 44;
		await (val == 42);
		int temp;
		atomic temp = val;
		System.out.println("temp = " + temp);
		if (temp != 42)
			return false;
		atomic val = 0;
		await (val == 42);
		next;
		int temp2;
		atomic temp2 = val;
		System.out.println("val = " + temp2);
		return temp2 == 42;
	}

	public static void main(String[] args) {
		new AwaitTest().executeAsync();
	}
}

