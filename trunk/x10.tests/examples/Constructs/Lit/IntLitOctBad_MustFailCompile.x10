/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This test is a limitation of the current release.
//Oct literals are not being checked for well-formedness.
import harness.x10Test;

/**
 * An error must be thrown by the compiler on encountering an oct literal that
 * is badly formed.
 * @author vj 1/2006
 */
public class IntLitOctBad_MustFailCompile extends x10Test {

	public def run(): boolean = {
		x10.io.Console.OUT.println(09);
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new IntLitOctBad_MustFailCompile().execute();
	}

	
}
