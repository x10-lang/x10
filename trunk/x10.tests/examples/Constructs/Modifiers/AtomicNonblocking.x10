/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a safe method can be overridden only by a safe method.
 * @author vj  9/2006
 */
public class AtomicNonblocking extends x10Test {

        atomic def m2(): void = { }

	public nonblocking def run(): boolean = {
		m2();
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new AtomicNonblocking().execute();
	}

	
}
