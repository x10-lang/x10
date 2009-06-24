/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Test resulted in unreachable statement message
 * as of 4/20/2005.
 *
 * @author Mandana Vaziri
 * @author kemal 4/2005
 */
public class Unreachable extends x10Test {

	val N: int = 10;
	val R: region = [0..N];
	val D: dist = dist.factory.arbitrary(R);

	def test(): void = {
		async (D(0)) {
			return;
		}
	}

	public def run(): boolean = {
		finish test();
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Unreachable().execute();
	}
}
