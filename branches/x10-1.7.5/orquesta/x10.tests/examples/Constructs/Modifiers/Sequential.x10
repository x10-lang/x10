/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Check that the sequential annotation is recognized.
 * @author vj  9/2006
 */
public class Sequential extends x10Test {

    public sequential def m(): void = { }

	public def run(): boolean = {
		m();
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new Sequential().execute();
	}

	
}
