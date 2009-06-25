/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: Illustrates various scenario where constraints may causes problems with short.
 * Issue: Contraint value is stored as an integer
 * @author vcave
 **/
public class AssignmentIntegerLitteralToConstrainedShort_MustFailCompile extends x10Test {

	public def run(): boolean = {
		val constraint: short = 0;
		var i: short{self == constraint} = 0; // should fail because constraint: short, not short{self==0}
		return false;
	}

	public static def main(var args: Rail[String]): void = {
		new AssignmentIntegerLitteralToConstrainedShort_MustFailCompile().execute();
	}

}
