/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
/*
 * NAS Parallel Benchmarks 3.0
 *
 * CG
 */
import NPB3_0_X10.*;
import harness.x10Test;

public class cg extends x10Test {

	public def run(): boolean = {
		NPB3_0_X10.CG.entryPoint(["-np8", "CLASS=W"]);
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new cg().execute();
	}
}
