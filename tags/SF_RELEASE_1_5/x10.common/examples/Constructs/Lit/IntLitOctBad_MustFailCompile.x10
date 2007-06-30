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

	public boolean run() {
		System.out.println(09);
		return true;
	}

	public static void main(String[] args) {
		new IntLitOctBad_MustFailCompile().execute();
	}

	
}

