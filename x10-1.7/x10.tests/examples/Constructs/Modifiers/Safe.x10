/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that the safe annotation is recognized.
 * @author vj  9/2006
 */
public class Safe extends x10Test {

    public safe def m(): void = { }

	public def run(): boolean = {
		m();
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Safe().execute();
	}

	
}
