/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * An error must be thrown by the compiler on encountering an oct literal that is badly formed.
 * @author vj 1/2006
 */

public class IntLitHexBad_MustFailCompile extends x10Test {

	public def run(): boolean ={
		x10.io.Console.OUT.println(0xR);
		return true;
	}

	public static def main(args: Rail[String]): void= {
		new IntLitHexBad_MustFailCompile().execute();
	}

	
}

