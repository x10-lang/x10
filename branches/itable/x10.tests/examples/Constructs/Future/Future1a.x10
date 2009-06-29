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
public class Future1a extends x10Test {
	public def run() = (future 41)()+1 == 42;
	public static def main(var args: Rail[String]): void = {
		new Future1a().execute();
	}
}
