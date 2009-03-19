/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Future test.
 */
public class Future0a extends x10Test {
	public def run() = (future 47)() == 47;

	public static def main(var args: Rail[String]): void = {
		new Future0a().execute();
	}
}
