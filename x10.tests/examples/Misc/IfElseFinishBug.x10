/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Exposes a possible parsing bug with
 * if (true) S1 else finish S2; .
 * As of 11/2005, this was executing the finish (it should not)
 * @author vj
 * @author kemal 11/2005
 */
public class IfElseFinishBug extends x10Test {

	public def run(): boolean = {
		if (true) x10.io.Console.OUT.println("True branch");
		else finish foreach (val (i): Point in [0..1]) { throw new Error("Throwing "+i); }
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new IfElseFinishBug().execute();
	}
}
